package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;

import java.util.List;

public class GetSurveysByProgram implements UseCase {

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private ISurveyRepository mSurveyRepository;
    private Callback mCallback;
    private String mIdProgram;

    public GetSurveysByProgram(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            ISurveyRepository surveyRepository) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mSurveyRepository = surveyRepository;
    }

    public void execute(Callback callback, String idProgram) {
        mCallback = callback;
        mIdProgram = idProgram;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        List<Survey> surveys = mSurveyRepository.getSurveysByProgram(mIdProgram);
        notifyGetSurveys(surveys);
    }

    private void notifyGetSurveys(final List<Survey> surveys) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onGetSurveys(surveys);
            }
        });
    }

    public interface Callback {
        void onGetSurveys(List<Survey> surveys);
    }

}
