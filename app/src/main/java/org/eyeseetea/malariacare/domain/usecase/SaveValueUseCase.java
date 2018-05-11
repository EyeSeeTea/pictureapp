package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;

import java.util.ArrayList;
import java.util.List;

public class SaveValueUseCase implements UseCase {

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private IValueRepository mValueRepository;
    private Callback mCallback;
    private long mIdSurvey;
    private List<Value> mValues;

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
        mValues = new ArrayList<>();
        mValues.add(value);
        mAsyncExecutor.run(this);
    }

    public void execute(Callback callback, long idSurvey, List<Value> values) {
        mCallback = callback;
        mIdSurvey = idSurvey;
        mValues = values;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        for(Value value : mValues) {
            mValueRepository.saveValue(value, mIdSurvey);
        }
        notifyValueSaved(mValues);
    }

    private void notifyValueSaved(final List<Value> values) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onValueSaved(values);
            }
        });
    }

    public interface Callback {
        void onValueSaved(List<Value> values);
    }
}
