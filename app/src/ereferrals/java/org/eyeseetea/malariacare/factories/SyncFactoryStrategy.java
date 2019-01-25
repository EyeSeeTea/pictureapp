package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.data.database.datasources.ConfigurationLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.LanguagesLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.sync.exporter.WSPushController;
import org.eyeseetea.malariacare.data.sync.importer.WSPullController;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IConfigurationRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ILanguageRepository;
import org.eyeseetea.malariacare.presentation.presenters.PullPresenter;

public class SyncFactoryStrategy extends ASyncFactory {
    private SettingsFactory settingsFactory = new SettingsFactory();

    @Override
    protected IPullController getPullController(Context context) {
        IConfigurationRepository configurationRepository = new ConfigurationLocalDataSource();
        ILanguageRepository languageRepository = new LanguagesLocalDataSource();

        return new WSPullController(context, configurationRepository, languageRepository);
    }

    @Override
    protected IPushController getPushController(Context context) {
        return new WSPushController(context, new SurveyLocalDataSource());
    }

    public PullPresenter getPullPresenter(Context context) {

        return new PullPresenter(getPullUseCase(context),
                settingsFactory.getSettingsUseCase(context),
                settingsFactory.saveSettingsUseCase(context));
    }
}
