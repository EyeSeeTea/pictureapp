package org.eyeseetea.malariacare.domain.usecase;


import static org.eyeseetea.malariacare.domain.usecase
        .VerifyLanguagesAndConfigFilesWereDownloadedUseCase.Callback.TypeOfFailed
        .CONFIGURATION_FILES;
import static org.eyeseetea.malariacare.domain.usecase
        .VerifyLanguagesAndConfigFilesWereDownloadedUseCase.Callback.TypeOfFailed.TRANSLATIONS;
import static org.eyeseetea.malariacare.domain.usecase
        .VerifyLanguagesAndConfigFilesWereDownloadedUseCase.Callback.TypeOfFailed
        .TRANSLATIONS_AND_CONFIGURATION_FILES;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.boundary.repositories
        .IConfigurationAndLanguagesStatusRepository;
import org.eyeseetea.malariacare.domain.entity.LoginType;

public class VerifyLanguagesAndConfigFilesWereDownloadedUseCase implements UseCase {

    private Callback callback;
    IConfigurationAndLanguagesStatusRepository statusRepository;

    public VerifyLanguagesAndConfigFilesWereDownloadedUseCase(
            IConfigurationAndLanguagesStatusRepository statusRepository
            ,Callback callback) {
        this.statusRepository = statusRepository;
        this.callback = callback;
    }

    @Override
    public void run() {
        LoginType loginType = PreferencesEReferral.getLastLoginType();

        boolean configFilesAndLanguagesWereDownloaded =
                statusRepository.configurationFilesWereDownloaded() &&
                        statusRepository.translationsWereDownloaded();

        switch (loginType) {

            case SOFT:

                boolean stringsTranslationFailed =
                        !statusRepository.translationsWereDownloaded();

                if (stringsTranslationFailed) {
                    callback.onSoftLoginStringTranslationFailed();
                }
                break;

            case FULL:

                if (!configFilesAndLanguagesWereDownloaded) {

                    if (!statusRepository.translationsWereDownloaded() &&
                            !statusRepository.configurationFilesWereDownloaded()) {
                        callback.onFullLoginStringTranslationOrConfigFilesFailed(
                                TRANSLATIONS_AND_CONFIGURATION_FILES);
                        return;
                    }

                    if (!statusRepository.translationsWereDownloaded()) {
                        callback.onFullLoginStringTranslationOrConfigFilesFailed(TRANSLATIONS);
                        return;
                    }
                    if (!statusRepository.configurationFilesWereDownloaded()) {
                        callback.onFullLoginStringTranslationOrConfigFilesFailed(
                                CONFIGURATION_FILES);
                        return;
                    }

                }

                break;
        }
    }

    public interface Callback {
        enum TypeOfFailed {
            TRANSLATIONS, CONFIGURATION_FILES,
            TRANSLATIONS_AND_CONFIGURATION_FILES
        }

        void onSoftLoginStringTranslationFailed();

        void onFullLoginStringTranslationOrConfigFilesFailed(TypeOfFailed typeOfFailed);
    }

}
