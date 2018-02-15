package org.eyeseetea.malariacare.domain.usecase;


import static org.eyeseetea.malariacare.domain.usecase.VerifyLanguagesAndConfigFilesWereDownloadedUseCase.Callback.TypeOfFailure
        .CONFIGURATION_FILES;
import static org.eyeseetea.malariacare.domain.usecase.VerifyLanguagesAndConfigFilesWereDownloadedUseCase.Callback.TypeOfFailure.TRANSLATIONS;
import static org.eyeseetea.malariacare.domain.usecase.VerifyLanguagesAndConfigFilesWereDownloadedUseCase.Callback.TypeOfFailure
        .TRANSLATIONS_AND_CONFIGURATION_FILES;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.boundary.repositories.IConfigurationRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ILanguageRepository;
import org.eyeseetea.malariacare.domain.entity.LoginType;

public class VerifyLanguagesAndConfigFilesWereDownloadedUseCase implements UseCase {

    private Callback callback;
    IConfigurationRepository mIConfigurationRepository;
    ILanguageRepository mILanguageRepository;

    public VerifyLanguagesAndConfigFilesWereDownloadedUseCase(
            IConfigurationRepository configurationRepository,
            ILanguageRepository languageRepository
            ,Callback callback) {
        this.mIConfigurationRepository = configurationRepository;
        this.mILanguageRepository = languageRepository;
        this.callback = callback;
    }

    @Override
    public void run() {
        LoginType loginType = PreferencesEReferral.getLastLoginType();

        boolean configFilesAndLanguagesWereDownloaded =
                mIConfigurationRepository.configurationFilesWereDownloaded() &&
                        mILanguageRepository.translationsWereDownloaded();

        switch (loginType) {
            case SOFT:
                boolean stringsTranslationFailed =
                        !mILanguageRepository.translationsWereDownloaded();

                if (stringsTranslationFailed) {
                    callback.onSoftLoginStringTranslationFailed();
                }
                break;

            case FULL:
                if (!configFilesAndLanguagesWereDownloaded) {
                    if (!mILanguageRepository.translationsWereDownloaded() &&
                            !mIConfigurationRepository.configurationFilesWereDownloaded()) {
                        callback.onFullLoginStringTranslationOrConfigFilesFailed(
                                TRANSLATIONS_AND_CONFIGURATION_FILES);
                        return;
                    }

                    if (!mILanguageRepository.translationsWereDownloaded()) {
                        callback.onFullLoginStringTranslationOrConfigFilesFailed(TRANSLATIONS);
                        return;
                    }

                    if (!mIConfigurationRepository.configurationFilesWereDownloaded()) {
                        callback.onFullLoginStringTranslationOrConfigFilesFailed(
                                CONFIGURATION_FILES);
                        return;
                    }
                }
                break;
        }
    }

    public interface Callback {
        enum TypeOfFailure {
            TRANSLATIONS, CONFIGURATION_FILES,
            TRANSLATIONS_AND_CONFIGURATION_FILES
        }

        void onSoftLoginStringTranslationFailed();

        void onFullLoginStringTranslationOrConfigFilesFailed(TypeOfFailure typeOfFailure);
    }

}
