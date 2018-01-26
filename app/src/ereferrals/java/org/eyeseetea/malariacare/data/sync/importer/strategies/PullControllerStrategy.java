package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.factory.ConverterFactory;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.MetadataUpdater;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.MetadataConfigurationDBImporter;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.MetadataConfigurationDataSourceFactory;
import org.eyeseetea.malariacare.data.sync.importer.models.CategoryOptionGroupExtended;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
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
import org.eyeseetea.malariacare.network.factory.HTTPClientFactory;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.CategoryOptionGroupFlow;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PullControllerStrategy extends APullControllerStrategy {
    private MetadataUpdater mMetadataUpdater;
    private MetadataConfigurationDBImporter importer;

    public PullControllerStrategy(PullController pullController) {
        super(pullController);
        mMetadataUpdater = new MetadataUpdater(PreferencesState.getInstance().getContext());

        IMetadataConfigurationDataSource dataSource =
                null;
    }

    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter,
            IPullController.Callback callback) {
        for (CategoryOptionGroupFlow categoryOptionGroupFlow : SdkQueries.getCategoryOptionGroups
                ()) {
            CategoryOptionGroupExtended categoryOptionGroupExtended =
                    new CategoryOptionGroupExtended(categoryOptionGroupFlow);
            categoryOptionGroupExtended.accept(converter);
        }
        callback.onComplete();
    }

    @Override
    public void pull(PullFilters pullFilters, IPullController.Callback callback, Context context) {
        Log.d(TAG, "Starting PULL process...");
        callback.onStep(PullStep.METADATA);

        try {

            if (!pullFilters.isDemo()) {
                mPullController.pullData(pullFilters, new ArrayList<OrganisationUnit>(), callback);
            } else {
                mPullController.populateMetadataFromCsvs(pullFilters.isDemo());
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
        try {
            IMetadataConfigurationDataSource dataSource =
                    MetadataConfigurationDataSourceFactory.getMetadataConfigurationDataSource(
                            HTTPClientFactory.getAuthenticationInterceptor()
                    );
            importer = new MetadataConfigurationDBImporter(
                    dataSource, ConverterFactory.getQuestionConverter()
            );

        if (isDemo) {
            IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
            ProgramDB programDB = ProgramDB.findByUID(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.demo_program_uid));
            Program program = new Program(programDB.getName(), programDB.getUid());
            programLocalDataSource.saveUserProgramId(program);
            callback.onComplete();
        } else {
            ICredentialsRepository credentialsLocalDataSource = new CredentialsLocalDataSource();
            IOrganisationUnitRepository orgUnitDataSource = new OrganisationUnitRepository();
            IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
                org.eyeseetea.malariacare.domain.entity.OrganisationUnit orgUnit =
                        orgUnitDataSource.getUserOrgUnit(
                                credentialsLocalDataSource.getOrganisationCredentials());
                org.eyeseetea.malariacare.domain.entity.Program program = orgUnit.getProgram();

            if (importer.hasToUpdateMetadata(program)) {
                checkCompletedSurveys(callback, program);
            }
                programLocalDataSource.saveUserProgramId(program);

            mPullController.convertData(callback);
        }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    private void checkCompletedSurveys(final IPullController.Callback callback,
            Program userProgram) {
        ISurveyRepository surveyLocalDataSource = new SurveyLocalDataSource();
        List<Survey> surveys = surveyLocalDataSource.getAllCompletedSurveys();

        IUserRepository userDataSource = new UserAccountDataSource();
        UserAccount currentUser = userDataSource.getLoggedUser();
        if (surveys.size() > 0) {
            currentUser.setCanAddSurveys(false);
            userDataSource.saveLoggedUser(currentUser);
        } else {
            try {

                downloadMetadataAndRepopulateDB(userDataSource, currentUser, userProgram);

            } catch (IOException e) {
                e.printStackTrace();
                callback.onError(e);
            }
        }
    }

    private void downloadMetadataAndRepopulateDB(IUserRepository userDataSource,
            UserAccount currentUser, Program userProgram)
            throws IOException {
        currentUser.setCanAddSurveys(true);
        userDataSource.saveLoggedUser(currentUser);

        if (isNetworkAvailable()) {
            downloadMetadataFromConfigurationFiles(userProgram);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void downloadMetadataFromConfigurationFiles(
            Program actualProgram) {
        try {
            importer.importMetadata(actualProgram);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
