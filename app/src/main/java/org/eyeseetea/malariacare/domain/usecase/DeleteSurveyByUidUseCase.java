package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;

public class DeleteSurveyByUidUseCase implements UseCase {

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private ISurveyRepository mSurveyRepository;
    private Callback mCallback;
    private String surveyUid;

    public DeleteSurveyByUidUseCase(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            ISurveyRepository surveyRepository) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mSurveyRepository = surveyRepository;
    }

    public void execute(Callback callback, String surveyUid) {
        mCallback = callback;
        this.surveyUid = surveyUid;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        try {
            mSurveyRepository.deleteSurveyByUid(surveyUid);
            notifyOnSurveyDeleted();
        } catch (Exception e) {
            System.out.println("An error has occurred deleting a survey:" + surveyUid);
        }
    }

    private void notifyOnSurveyDeleted() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSurveyDeleted();
            }
        });
    }

    public interface Callback {
        void onSurveyDeleted();
    }

}
