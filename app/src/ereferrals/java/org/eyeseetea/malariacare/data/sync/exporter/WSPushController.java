package org.eyeseetea.malariacare.data.sync.exporter;

import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResponseAction;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.domain.exception.push.PushValueException;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class WSPushController implements IPushController {

    private static final String TAG = "WSPushController";

    private ConvertToWSVisitor mConvertToWSVisitor;
    private List<Survey> mSurveys;
    private WSClient mWSClient;
    private IPushControllerCallback mCallback;


    public WSPushController() throws IllegalArgumentException {
        mWSClient = new WSClient();
        mConvertToWSVisitor = new ConvertToWSVisitor();
    }

    @Override
    public void push(IPushControllerCallback callback) {
        mCallback = callback;

        if (!ConnectivityStatus.isConnected(PreferencesState.getInstance().getContext())) {
            Log.d(TAG, "No network");
            callback.onError(new NetworkException());
        } else {
            mSurveys = Survey.getAllCompletedSurveys();
            if (mSurveys == null || mSurveys.size() == 0) {
                callback.onError(new SurveysToPushNotFoundException("Null surveys"));
                return;
            }
            for (Survey srv : mSurveys) {
                Log.d("DpBlank", "Survey to push " + srv.toString());
                for (Value dv : srv.getValuesFromDB()) {
                    Log.d("DpBlank", "Values to push " + dv.toString());
                }
            }
            try {
                convertToWSSurveys();
            } catch (Exception e) {
                e.printStackTrace();
                putSurveysAsCompleted();
                callback.onError(new ConversionException(e));
                return;
            }
            pushSurveys();
        }
    }

    @Override
    public boolean isPushInProgress() {
        return PreferencesState.getInstance().isPushInProgress();
    }

    @Override
    public void changePushInProgress(boolean inProgress) {
        PreferencesState.getInstance().setPushInProgress(inProgress);
    }

    private void convertToWSSurveys() throws Exception {
        for (Survey survey : mSurveys) {
            survey.setStatus(Constants.SURVEY_SENDING);
            survey.save();
            Log.d(TAG, "Status of survey to be push is = " + survey.getStatus());
            survey.accept(mConvertToWSVisitor);
        }
    }

    private void pushSurveys() {
        mWSClient.pushSurveys(mConvertToWSVisitor.getSurveyContainerWSObject(),
                new WSClient.WSClientCallBack() {
                    @Override
                    public void onSuccess(SurveyWSResult surveyWSResult) {
                        checkPushResult(surveyWSResult);
                    }

                    @Override
                    public void onError(Exception e) {
                        putSurveysAsCompleted();
                        mCallback.onError(e);
                    }
                });
    }

    private void checkPushResult(SurveyWSResult surveyWSResult) {
        for (SurveyWSResponseAction responseAction : surveyWSResult.getActions()) {
            if (!responseAction.isSuccess()) {
                String message = String.format(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.survey_error), responseAction.getActionId(),
                        responseAction.getMessage(), responseAction.getResponse().getMsg());
                mCallback.onInformativeError(new PushValueException(message));
            }
        }
        for (Survey survey : mSurveys) {
            survey.setStatus(Constants.SURVEY_SENT);
            survey.save();
        }
        mCallback.onComplete();
    }


    private void putSurveysAsCompleted() {
        for (Survey survey : mSurveys) {
            survey.setStatus(Constants.SURVEY_COMPLETED);
            survey.save();
        }
    }
}
