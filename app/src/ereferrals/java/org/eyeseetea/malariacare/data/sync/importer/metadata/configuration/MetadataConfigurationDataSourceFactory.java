package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;

public class MetadataConfigurationDataSourceFactory {
    @NonNull
    public static IMetadataConfigurationDataSource getMetadataConfigurationDataSource(
            BasicAuthInterceptor basicAuthInterceptor, String url)
            throws Exception {
        return new MetadataConfigurationApiClient(url,
                basicAuthInterceptor);
    }
}
