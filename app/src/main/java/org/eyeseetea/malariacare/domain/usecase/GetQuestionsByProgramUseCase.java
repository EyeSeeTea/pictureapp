package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public class GetQuestionsByProgramUseCase implements UseCase {

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IQuestionRepository mQuestionRepository;
    private Callback mCallback;
    private String mProgramUID;

    public GetQuestionsByProgramUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IQuestionRepository questionRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mQuestionRepository = questionRepository;
    }

    public void execute(Callback callback, String programUID) {
        mCallback = callback;
        mProgramUID = programUID;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        List<Question> questions = mQuestionRepository.getQuestionsByProgram(mProgramUID);
        notifyGetQuestions(questions);
    }

    private void notifyGetQuestions(final List<Question> questions) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onGetQuestions(questions);
            }
        });
    }

    public interface Callback {
        void onGetQuestions(List<Question> questions);
    }
}
