package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;

public class MetadataConfigurationDataSourceFactory {
    private static ISettingsRepository settingsRepository;
    public MetadataConfigurationDataSourceFactory(ISettingsRepository settingsRepository){
        this.settingsRepository = settingsRepository;
    }

    @NonNull
    public IMetadataConfigurationDataSource getMetadataConfigurationDataSource(
            BasicAuthInterceptor basicAuthInterceptor)
            throws Exception {
        return new MetadataConfigurationApiClient(settingsRepository.getSettings().getUrl(),
                basicAuthInterceptor);
    }
}
