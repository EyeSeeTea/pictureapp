package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;

public class SaveSurveyUseCase implements UseCase {
    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private ISurveyRepository mSurveyRepository;
    private Callback mCallback;
    private Survey mSurvey;

    public SaveSurveyUseCase(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            ISurveyRepository surveyRepository) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mSurveyRepository = surveyRepository;
    }

    public void execute(Survey survey, Callback callback) {
        mSurvey = survey;
        mCallback = callback;
        mAsyncExecutor.run(this);
    }


    @Override
    public void run() {
        mSurvey.setId(mSurveyRepository.save(mSurvey));
        notifySurveySaved();
    }

    private void notifySurveySaved() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSurveySaved(mSurvey);
            }
        });
    }

    public interface Callback {
        void onSurveySaved(Survey survey);
    }
}
