package org.eyeseetea.malariacare.domain.usecase;


import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.strategies
        .AGetReviewValuesBySurveyIdUseCaseStrategy;
import org.eyeseetea.malariacare.domain.usecase.strategies.GetReviewValuesBySurveyIdUseCaseStrategy;

import java.util.List;

public class GetReviewValuesBySurveyIdUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private Callback mCallback;
    private IValueRepository mIValueRepository;
    private long mSurveyId;
    private AGetReviewValuesBySurveyIdUseCaseStrategy mGetReviewValuesBySurveyIdUseCaseStrategy;


    public GetReviewValuesBySurveyIdUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IValueRepository IValueRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mIValueRepository = IValueRepository;
        mGetReviewValuesBySurveyIdUseCaseStrategy = new GetReviewValuesBySurveyIdUseCaseStrategy();
    }

    public void execute(Callback callback, long surveyId) {
        mCallback = callback;
        mSurveyId = surveyId;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        List<Value> values = mIValueRepository.getValuesFromSurvey(mSurveyId);
        final List<Value> orderValues = mGetReviewValuesBySurveyIdUseCaseStrategy.orderValues(
                values);
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onGetValues(orderValues);
            }
        });
    }


    public interface Callback {
        void onGetValues(List<Value> values);
    }
}
