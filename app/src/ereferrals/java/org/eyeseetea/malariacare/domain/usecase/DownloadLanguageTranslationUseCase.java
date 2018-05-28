package org.eyeseetea.malariacare.domain.usecase;


import org.eyeseetea.malariacare.data.authentication.CredentialsReader;
import org.eyeseetea.malariacare.data.sync.importer.strategies.ILanguagesClient;
import org.eyeseetea.malariacare.data.sync.importer.LanguageDownloader;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.locale.factory.LanguageFactory;

public class DownloadLanguageTranslationUseCase implements UseCase {

    private IConnectivityManager connectivity;
    private CredentialsReader credentialsReader;
    private ISettingsRepository mSettingsRepository;

    public DownloadLanguageTranslationUseCase(CredentialsReader credentialsReader,
            IConnectivityManager connectivity,ISettingsRepository settingsRepository) {
        this.connectivity = connectivity;
        this.credentialsReader = credentialsReader;
        mSettingsRepository=settingsRepository;
    }

    public void downloadAsync(IAsyncExecutor mAsyncExecutor) throws Exception {
        mAsyncExecutor.run(this);
    }

    public void download() throws Exception {

        LanguageDownloader downloader = LanguageFactory.getLanguageDownloader(getClient(),
                connectivity,mSettingsRepository.getSettings());
        downloader.start();

    }

    private ILanguagesClient getClient() throws Exception {
        String token = credentialsReader.getPOEditorToken();
        String projectID = credentialsReader.getPOEditorProjectID();
        return LanguageFactory.getPOEditorApiClient(projectID, token);
    }

    @Override
    public void run() {
        try {
            download();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

