package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.data.IApiStatusDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.data.sync.exporter.model.ApiAvailable;
import org.eyeseetea.malariacare.domain.entity.ApiStatus;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;

import java.io.IOException;

public class ApiStatusRemoteDataSource implements IApiStatusDataSource {
    @Override
    public ApiStatus getApiStatus() throws IOException, AvailableApiException {
        ApiAvailable apiAvailable = new eReferralsAPIClient(
                PreferencesEReferral.getWSURL()).getIfIsApiAvailable();

        return mapApiAvailableToApiStatus(apiAvailable);
    }

    private ApiStatus mapApiAvailableToApiStatus(ApiAvailable apiAvailable) {
        return new ApiStatus(apiAvailable.isAvailable(), apiAvailable.getMsg());
    }
}
