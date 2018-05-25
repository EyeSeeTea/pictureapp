package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;

public class SaveValueUseCase implements UseCase {

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private IValueRepository mValueRepository;
    private Callback mCallback;
    private long mIdSurvey;
    private Value mValue;

    public SaveValueUseCase(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            IValueRepository valueRepository) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mValueRepository = valueRepository;
    }

    public void execute(Callback callback, long idSurvey, Value value) {
        mCallback = callback;
        mIdSurvey = idSurvey;
        mValue = value;
        mAsyncExecutor.run(this);
    }


    @Override
    public void run() {
        mValueRepository.saveValue(mValue, mIdSurvey);
        notifyValueSaved(mValue);
    }

    private void notifyValueSaved(final Value value) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onValueSaved(value);
            }
        });
    }

    public interface Callback {
        void onValueSaved(Value value);
    }
}
