package org.eyeseetea.malariacare.data;

import org.eyeseetea.malariacare.domain.entity.ApiStatus;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;

import java.io.IOException;

public interface IApiStatusDataSource {
    ApiStatus getApiStatus() throws IOException, AvailableApiException;
}
