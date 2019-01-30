package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.ApiStatus;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;

import java.io.IOException;

public interface IApiStatusRepository {
 ApiStatus getApiStatus() throws IOException, AvailableApiException;
}
