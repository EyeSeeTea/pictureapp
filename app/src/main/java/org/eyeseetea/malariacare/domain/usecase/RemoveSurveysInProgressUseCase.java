package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;

public class RemoveSurveysInProgressUseCase implements UseCase {

    public interface Callback {
        void onSuccess();
    }

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private ISurveyRepository mSurveyRepository;
    private Callback mCallback;

    public RemoveSurveysInProgressUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            ISurveyRepository surveyRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mSurveyRepository = surveyRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mSurveyRepository.removeInProgress();
                mCallback.onSuccess();
            }
        });
    }
}
