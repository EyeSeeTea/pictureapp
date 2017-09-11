package org.eyeseetea.malariacare.domain.usecase.push;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ClosedUserPushException;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.domain.service.OverLimitSurveysDomainService;
import org.eyeseetea.malariacare.domain.usecase.UseCase;
import org.eyeseetea.malariacare.domain.usecase.strategies.PushUseCaseStrategy;

public class PushUseCase implements UseCase {

    public interface Callback {
        void onComplete();

        void onPushError();

        void onPushInProgressError();

        void onSurveysNotFoundError();

        void onConversionError();

        void onNetworkError();

        void onInformativeError(String message);

        void onClosedUser();

        void onBannedOrgUnit();

        void onReOpenOrgUnit();

        void onApiCallError();

        void onApiCallError(ApiCallException e);
    }

    private IAsyncExecutor mAsyncExecutor;

    private Callback mCallback;

    private PushUseCaseStrategy mPushUseCaseStrategy;

    public PushUseCase(IPushController pushController, IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor, SurveysThresholds surveysThresholds,
            ISurveyRepository surveyRepository,
            IOrganisationUnitRepository organisationUnitRepository) {
        mAsyncExecutor = asyncExecutor;
        mPushUseCaseStrategy = new PushUseCaseStrategy(pushController,
                organisationUnitRepository, surveysThresholds, surveyRepository, mainExecutor);
    }

    public void execute(final Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mPushUseCaseStrategy.run(mCallback);
    }

}

