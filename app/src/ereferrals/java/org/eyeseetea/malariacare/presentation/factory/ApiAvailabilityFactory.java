package org.eyeseetea.malariacare.presentation.factory;

import org.eyeseetea.malariacare.data.remote.ApiStatusRemoteDataSource;
import org.eyeseetea.malariacare.data.repositories.ApiStatusRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IApiStatusRepository;
import org.eyeseetea.malariacare.domain.usecase.GetWebAvailableUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public class ApiAvailabilityFactory {
    private IAsyncExecutor mAsyncExecutor = new AsyncExecutor();
    private IMainExecutor mMainExecutor = new UIThreadExecutor();

    public GetWebAvailableUseCase getGetWebViewAvailableUseCase() {
        IApiStatusRepository apiStatusRepository = new ApiStatusRepository(
                new ApiStatusRemoteDataSource());
        return new GetWebAvailableUseCase(mAsyncExecutor, mMainExecutor, apiStatusRepository);
    }
}
