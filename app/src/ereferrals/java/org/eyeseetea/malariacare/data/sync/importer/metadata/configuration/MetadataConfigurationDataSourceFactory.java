package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;

public class MetadataConfigurationDataSourceFactory {
    private Context context;
    public MetadataConfigurationDataSourceFactory(Context mContext) {
        context = mContext;
    }

    @NonNull
    public IMetadataConfigurationDataSource getMetadataConfigurationDataSource(
            BasicAuthInterceptor basicAuthInterceptor)
            throws Exception {
        return new MetadataConfigurationApiClient(PreferencesEReferral.getProgramUrl(context),
                basicAuthInterceptor);
    }
}
