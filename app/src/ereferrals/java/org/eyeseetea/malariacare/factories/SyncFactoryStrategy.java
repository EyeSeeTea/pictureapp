package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.data.database.datasources.ConfigurationLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.LanguagesLocalDataSource;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.AppInfoDataSource;
import org.eyeseetea.malariacare.data.database.datasources.CountryVersionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.DeviceDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.data.sync.exporter.ConvertToWSVisitor;
import org.eyeseetea.malariacare.data.sync.exporter.WSPushController;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.data.sync.importer.WSPullController;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICountryVersionRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
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
        IDeviceRepository deviceDataSource = new DeviceDataSource();
        ICredentialsRepository credentialsRepository = new CredentialsLocalDataSource();
        ISettingsRepository settingsRepository = new SettingsDataSource(
                PreferencesState.getInstance().getContext());
        IAppInfoRepository appInfoDataSource = new AppInfoDataSource(context);
        eReferralsAPIClient mEReferralsAPIClient = new eReferralsAPIClient(settingsRepository.getSettings().getWsServerUrl());
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        IProgramRepository programRepository = new ProgramRepository();
        ICountryVersionRepository countryVersionRepository = new CountryVersionLocalDataSource();
        ConvertToWSVisitor mConvertToWSVisitor = new ConvertToWSVisitor(deviceDataSource, credentialsRepository, settingsRepository,
                appInfoDataSource, programRepository, countryVersionRepository);

        return new WSPushController(mConvertToWSVisitor, mEReferralsAPIClient, surveyRepository);
    }

    public PullPresenter getPullPresenter(Context context) {

        return new PullPresenter(getPullUseCase(context),
                settingsFactory.getSettingsUseCase(context),
                settingsFactory.saveSettingsUseCase(context));
    }
}
