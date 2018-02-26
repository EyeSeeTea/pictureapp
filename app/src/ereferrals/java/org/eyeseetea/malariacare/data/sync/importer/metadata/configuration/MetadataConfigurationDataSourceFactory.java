package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;

public class MetadataConfigurationDataSourceFactory {
    @NonNull
    public static IMetadataConfigurationDataSource getMetadataConfigurationDataSource(
            BasicAuthInterceptor basicAuthInterceptor)
            throws Exception {
        return new MetadataConfigurationApiClient(PreferencesState.getInstance().getDhisURL(),
                basicAuthInterceptor);
    }
}
