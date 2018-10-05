package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.network.factory.HTTPClientFactory;

public class MetadataConfigurationDataSourceFactory {

    private Context context;
    public MetadataConfigurationDataSourceFactory(Context mContext) {
        context = mContext;
    }

    @NonNull
    public IMetadataConfigurationDataSource getMetadataConfigurationDataSource()
            throws Exception {
        ISettingsRepository settingsRepository = new SettingsDataSource(context);

        Settings settings = settingsRepository.getSettings();

        return new MetadataConfigurationApiClient(settings.getProgramUrl(),
                HTTPClientFactory.getAuthenticationInterceptor(settings.getUser(),
                        settings.getPass()));
    }
}
