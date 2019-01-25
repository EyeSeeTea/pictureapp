package org.eyeseetea.malariacare.data.sync.importer;

import static org.eyeseetea.malariacare.domain.exception.InvalidMetadataException.TypeOfFailure
        .CONFIGURATION_FILES;
import static org.eyeseetea.malariacare.domain.exception.InvalidMetadataException.TypeOfFailure
        .TRANSLATIONS;
import static org.eyeseetea.malariacare.domain.exception.InvalidMetadataException.TypeOfFailure
        .TRANSLATIONS_AND_CONFIGURATION_FILES;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.CredentialsReader;
import org.eyeseetea.malariacare.data.database.datasources.AppInfoDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.data.sync.factory.ConverterFactory;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationDBImporter;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationDataSourceFactory;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IConfigurationRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ILanguageRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.InvalidMetadataException;
import org.eyeseetea.malariacare.domain.exception.WarningException;
import org.eyeseetea.malariacare.domain.usecase.DownloadLanguageTranslationUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.network.factory.NetworkManagerFactory;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class WSPullController implements IPullController {
    private static String TAG = "WSPullController";
    private MetadataUpdater mMetadataUpdater;
    private MetadataConfigurationDBImporter importer;
    private Context mContext;
    private IAppInfoRepository mAppInfoRepository;

    IConfigurationRepository mIConfigurationRepository;
    ILanguageRepository mILanguageRepository;

    public WSPullController(Context context,
            IConfigurationRepository configurationRepository,
            ILanguageRepository languageRepository) {
        mMetadataUpdater = new MetadataUpdater(context);
        mContext = context;
        mAppInfoRepository = new AppInfoDataSource(context);

        this.mIConfigurationRepository = configurationRepository;
        this.mILanguageRepository = languageRepository;
    }

/*    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter,
            IPullController.Callback callback) {
        for (CategoryOptionGroupFlow categoryOptionGroupFlow : SdkQueries.getCategoryOptionGroups
                ()) {
            CategoryOptionGroupExtended categoryOptionGroupExtended =
                    new CategoryOptionGroupExtended(categoryOptionGroupFlow);
            categoryOptionGroupExtended.accept(converter);
        }
        callback.onComplete();
    }*/

    @Override
    public void pull(PullFilters pullFilters, Callback callback) {
        Log.d(TAG, "Starting PULL process...");

        try {

            if (pullFilters.isDemo()) {
                populateMetadataFromCsvs(pullFilters.isDemo());
            }

            pullMetadata(callback, pullFilters.isDemo());
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    public void pullMetadata(final IPullController.Callback callback, boolean isDemo) {
        try {

            if (isDemo) {
                callback.onStep(PullStep.METADATA);
                IProgramRepository programRepository = new ProgramRepository();
                ProgramDB programDB = ProgramDB.findByUID(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.demo_program_uid));
                Program program = new Program(programDB.getName(), programDB.getUid());
                programRepository.saveUserProgramId(program);
                callback.onComplete();
            } else {
                MetadataConfigurationDataSourceFactory metadataConfigurationDataSourceFactory =
                        new MetadataConfigurationDataSourceFactory(mContext);
                IMetadataConfigurationDataSource metadataConfigurationDataSource =
                        metadataConfigurationDataSourceFactory.getMetadataConfigurationDataSource();
                importer = new MetadataConfigurationDBImporter(
                        metadataConfigurationDataSource, ConverterFactory.getQuestionConverter()
                );

                IProgramRepository programRepository = new ProgramRepository();

                Program program = programRepository.getUserProgram();

                boolean isLastVerificationValid = true;

                if (importer.hasToUpdateMetadata(program)) {
                    checkCompletedSurveys(callback, program);
                    isLastVerificationValid = verifyLanguagesAndMetadataIsDownloaded(callback);
                }
                programRepository.saveUserProgramId(program);

                if (isLastVerificationValid) {
                    callback.onComplete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }

    private boolean verifyLanguagesAndMetadataIsDownloaded(Callback callback) {
        boolean configFilesWereDownloaded =
                mIConfigurationRepository.configurationFilesWereDownloaded();

        boolean translationsWereDownloaded = mILanguageRepository.translationsWereDownloaded();

        if (!translationsWereDownloaded && !configFilesWereDownloaded) {
            callback.onError(
                    new InvalidMetadataException(TRANSLATIONS_AND_CONFIGURATION_FILES));
        } else if (!translationsWereDownloaded) {
            callback.onError(new InvalidMetadataException(TRANSLATIONS));
        } else if (!configFilesWereDownloaded) {
            callback.onError(new InvalidMetadataException(CONFIGURATION_FILES));
        }

        return configFilesWereDownloaded && translationsWereDownloaded;
    }

    private void checkCompletedSurveys(final IPullController.Callback callback,
            Program userProgram) {
        ISurveyRepository surveyLocalDataSource = new SurveyLocalDataSource();
        List<Survey> surveys = surveyLocalDataSource.getUnsentSurveys();

        IUserRepository userDataSource = new UserAccountDataSource();
        UserAccount currentUser = userDataSource.getLoggedUser();
        if (surveys.size() > 0) {
            currentUser.setCanAddSurveys(false);
            userDataSource.saveLoggedUser(currentUser);
        } else {
            try {
                callback.onStep(PullStep.METADATA);
                downloadMetadataAndRepopulateDB(userDataSource, currentUser, userProgram);

            } catch (IOException e) {
                e.printStackTrace();
                callback.onError(e);
            } catch (WarningException e) {
                e.printStackTrace();
                callback.onWarning(e);
            }
        }
    }

    private void downloadMetadataAndRepopulateDB(IUserRepository userDataSource,
            UserAccount currentUser, Program userProgram)
            throws IOException, WarningException {
        currentUser.setCanAddSurveys(true);
        userDataSource.saveLoggedUser(currentUser);
        try {
            if (isNetworkAvailable()) {
                downloadMetadataFromConfigurationFiles(userProgram);
                updateDownloadMetadataDate();
            }
        } catch (WarningException e) {
            currentUser.setCanAddSurveys(false);
            userDataSource.saveLoggedUser(currentUser);
            throw e;
        }
    }

    private void updateDownloadMetadataDate() {
        Date date = new Date();
        AppInfo appInfo = mAppInfoRepository.getAppInfo();
        appInfo = new AppInfo(appInfo.getMetadataVersion(), appInfo.getConfigFileVersion(),
                appInfo.getAppVersion(), date, appInfo.getLastPushDate());
        mAppInfoRepository.saveAppInfo(appInfo);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void downloadMetadataFromConfigurationFiles(
            Program actualProgram) throws WarningException {
        try {
            downloadAsyncLanguagesFromServer();
            importer.importMetadata(actualProgram);
        } catch (WarningException e1) {
            throw e1;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void downloadAsyncLanguagesFromServer() throws Exception {
        Log.i(TAG, "Starting to download Languages From Server");
        AsyncExecutor asyncExecutor = new AsyncExecutor();
        CredentialsReader credentialsReader = CredentialsReader.getInstance();
        IConnectivityManager connectivity = NetworkManagerFactory.getConnectivityManager(
                PreferencesState.getInstance().getContext());
        DownloadLanguageTranslationUseCase downloader =
                new DownloadLanguageTranslationUseCase(credentialsReader, connectivity);

        downloader.downloadAsync(asyncExecutor);
    }


    @Override
    public void cancel() {
        //not implemented
    }

    public void populateMetadataFromCsvs(boolean isDemo) throws IOException {
        PopulateDB.initDataIfRequired(mContext);
        if (isDemo) {
            new PopulateDBStrategy().createDummyOrgUnitsDataInDB(mContext);
            new PopulateDBStrategy().createDummyOrganisationInDB();
        }
    }
}
