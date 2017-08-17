package org.eyeseetea.malariacare.domain.usecase;

import android.util.Log;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICanAddSurveysRepository;

public class CanAddNewSurveyButtonUseCase implements UseCase {
    private static final String TAG = "CanAddNewSurveyButtonUC";
    private Callback mCallback;
    private ICanAddSurveysRepository mCanAddSurveysRepository;

    public CanAddNewSurveyButtonUseCase(
            ICanAddSurveysRepository canAddSurveysRepository) {
        mCanAddSurveysRepository = canAddSurveysRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        run();
    }

    @Override
    public void run() {
        mCanAddSurveysRepository.canAddSurveys(new IDataSourceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                mCallback.canAddNewSurveyButton(result);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "error geting if can add new survey:" + throwable.getMessage());
                mCallback.canAddNewSurveyButton(false);
            }
        });
    }

    public interface Callback {
        void canAddNewSurveyButton(boolean canAddNewSurvey);
    }
}
