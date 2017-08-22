package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.CSVFileDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.FileCsvsStrategy;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.remote.repositories.CSVFileRepository;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.MetadataUpdater;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.data.sync.importer.models.CategoryOptionGroupExtended;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICSVFileRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.CategoryOptionGroupFlow;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PullControllerStrategy extends APullControllerStrategy {
    private int csvNewVersion;
    private MetadataUpdater mMetadataUpdater;

    public PullControllerStrategy(PullController pullController) {
        super(pullController);
        mMetadataUpdater = new MetadataUpdater(PreferencesState.getInstance().getContext());
    }

    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter) {
        for (CategoryOptionGroupFlow categoryOptionGroupFlow : SdkQueries.getCategoryOptionGroups
                ()) {
            CategoryOptionGroupExtended categoryOptionGroupExtended =
                    new CategoryOptionGroupExtended(categoryOptionGroupFlow);
            categoryOptionGroupExtended.accept(converter);
        }
    }

    @Override
    public void pull(PullFilters pullFilters, IPullController.Callback callback) {
        Log.d(TAG, "Starting PULL process...");
        callback.onStep(PullStep.METADATA);

        try {


            if (isNetworkAvailable()) {
                checkCSVVersion(callback, pullFilters);
            }

            if(!pullFilters.isDemo()) {
                mPullController.pullData(pullFilters, new ArrayList<OrganisationUnit>(), callback);
            }else{
                mPullController.onPullDataComplete(callback, true);
                callback.onComplete();
            }
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    @Override
    public void onPullDataComplete(final IPullController.Callback callback, boolean isDemo) {
        if(isDemo){
            IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
            ProgramDB programDB = ProgramDB.getFirstProgram();
            Program program = new Program(programDB.getName(), programDB.getUid());
            programLocalDataSource.saveUserProgramId(program);
            callback.onComplete();
        }else {
            ICredentialsRepository credentialsLocalDataSource = new CredentialsLocalDataSource();
            IOrganisationUnitRepository orgUnitDataSource = new OrganisationUnitRepository();
            IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
            try {
                org.eyeseetea.malariacare.domain.entity.OrganisationUnit orgUnit =
                        orgUnitDataSource.getUserOrgUnit(
                                credentialsLocalDataSource.getOrganisationCredentials());
                org.eyeseetea.malariacare.domain.entity.Program program = orgUnit.getProgram();
                programLocalDataSource.saveUserProgramId(program);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(e);
            }
            mPullController.convertData(callback);
        }
    }

    private void checkCSVVersion(final IPullController.Callback callback, PullFilters pullFilters)
            throws IOException {
        if (mMetadataUpdater.hasToUpdateMetadata()) {
            checkNotSentSurveys(callback);
        } else {
            IUserRepository userDataSource = new UserAccountDataSource();
            UserAccount userAccount = userDataSource.getLoggedUser();
            userAccount.setCanAddSurveys(true);
            userDataSource.saveLoggedUser(userAccount);
            mPullController.populateMetadataFromCsvs(pullFilters.isDemo());
        }
    }

    private void checkNotSentSurveys(final IPullController.Callback callback) {
        ISurveyRepository surveyLocalDataSource = new SurveyLocalDataSource();
        surveyLocalDataSource.getUnsentSurveys(new IDataSourceCallback<List<Survey>>() {
            @Override
            public void onSuccess(List<Survey> surveys) {
                IUserRepository userDataSource = new UserAccountDataSource();
                UserAccount currentUser = userDataSource.getLoggedUser();
                if (surveys.size() > 0) {
                    currentUser.setCanAddSurveys(false);
                    userDataSource.saveLoggedUser(currentUser);
                } else {
                    currentUser.setCanAddSurveys(true);
                    userDataSource.saveLoggedUser(currentUser);
                    downloadCsvsAndRepopulateDB(callback);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    private void downloadCsvsAndRepopulateDB(final IPullController.Callback callback) {
        List<String> csvsToImport = FileCsvsStrategy.getCsvsToCreate();
        ICSVFileRepository csvFileRepository = new CSVFileRepository();
        final int[] csvImportedSize = {0};
        final ICSVFileRepository csvFileDataSource = new CSVFileDataSource(
                PreferencesState.getInstance().getContext());
        for (final String csvToImport : csvsToImport) {
            csvFileRepository.getCSVFile(csvToImport, new IDataSourceCallback<byte[]>() {
                @Override
                public void onSuccess(byte[] result) {
                    csvFileDataSource.saveCSVFile(csvToImport, result,
                            new IDataSourceCallback<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    csvImportedSize[0]++;

                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    callback.onError(throwable);
                                }
                            });
                }

                @Override
                public void onError(Throwable throwable) {
                    callback.onError(throwable);
                }
            });
        }
        if (csvImportedSize[0] == csvsToImport.size()) {
            clearCSVSDBAndRepopulate(csvFileDataSource, callback);
        } else {
            callback.onError(new Exception(
                    "Not all csv imported needed: " + csvsToImport.size() + " imported: "
                            + csvImportedSize[0]));
        }

    }

    private void clearCSVSDBAndRepopulate(ICSVFileRepository csvFileDataSource,
            final IPullController.Callback callback) {
        csvFileDataSource.updateCSVDB(new IDataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                try {
                    mMetadataUpdater.saveNewVersion(csvNewVersion);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
