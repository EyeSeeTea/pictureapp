package org.eyeseetea.malariacare.data.repositories;

import org.eyeseetea.malariacare.data.IApiStatusDataSource;
import org.eyeseetea.malariacare.domain.boundary.repositories.IApiStatusRepository;
import org.eyeseetea.malariacare.domain.entity.ApiStatus;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;

import java.io.IOException;

public class ApiStatusRepository implements IApiStatusRepository {
    IApiStatusDataSource mApiStatusDataSource;

    public ApiStatusRepository(IApiStatusDataSource apiStatusDataSource) {
        mApiStatusDataSource = apiStatusDataSource;
    }

    @Override
    public ApiStatus getApiStatus() throws IOException, AvailableApiException {
        return mApiStatusDataSource.getApiStatus();
    }
}
