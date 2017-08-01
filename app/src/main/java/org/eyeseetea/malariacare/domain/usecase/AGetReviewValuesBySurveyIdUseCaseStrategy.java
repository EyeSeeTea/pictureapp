package org.eyeseetea.malariacare.domain.usecase;


import com.google.common.collect.Iterables;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AGetReviewValuesBySurveyIdUseCaseStrategy implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private Callback mCallback;
    private IValueRepository mIValueRepository;
    private long mSurveyId;


    public AGetReviewValuesBySurveyIdUseCaseStrategy(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IValueRepository IValueRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mIValueRepository = IValueRepository;
    }

    public void execute(Callback callback, long surveyId) {
        mCallback = callback;
        mSurveyId = surveyId;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        List<Value> values = mIValueRepository.getValuesFromSurvey(mSurveyId);
        Iterator<String> colorIterator = Iterables.cycle(
                createBackgroundColorList(values)).iterator();
        for (Value value : values) {
            value.setBackgroundColor(colorIterator.next());
        }
        final List<Value> orderValues = orderValues(values);
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onGetValues(orderValues);
            }
        });
    }

    private List<String> createBackgroundColorList(
            List<Value> values) {
        List<java.lang.String> colorsList = new ArrayList<>();
        for (Value value : values) {
            if (value.getBackgroundColor() != null) {
                java.lang.String color = value.getBackgroundColor();
                if (!colorsList.contains(color)) {
                    colorsList.add(color);
                }
            }
        }
        //Hardcoded colors for a colorList without colors.
        if (colorsList.size() == 0) {
            colorsList.add("#4d3a4b");
        }
        if (colorsList.size() == 1 && values.size() > 1) {
            colorsList.add("#9c7f9b");
        }
        return colorsList;
    }

    public interface Callback {
        void onGetValues(List<Value> values);
    }

    protected abstract List<Value> orderValues(List<Value> values);
}
