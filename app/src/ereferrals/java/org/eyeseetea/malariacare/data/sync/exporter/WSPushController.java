package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySendAction;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResponseAction;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.data.sync.exporter.model.Voucher;
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
    private List<SurveyDB> mSurveys;
    private IPushControllerCallback mCallback;
    private SurveyContainerWSObject mSurveyContainerWSObject;


    public WSPushController() throws IllegalArgumentException {
        mConvertToWSVisitor = new ConvertToWSVisitor();
    }

    @Override
    public void push(IPushControllerCallback callback) {
        mCallback = callback;

        if (!ConnectivityStatus.isConnected(PreferencesState.getInstance().getContext())) {
            Log.d(TAG, "No network");
            callback.onError(new NetworkException());
        } else {
            mSurveys = SurveyDB.getAllCompletedSurveys();
            if (mSurveys == null || mSurveys.size() == 0) {
                callback.onError(new SurveysToPushNotFoundException("Null surveys"));
                return;
            }
            for (SurveyDB srv : mSurveys) {
                Log.d("DpBlank", "Survey to push " + srv.toString());
                for (ValueDB dv : srv.getValuesFromDB()) {
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
        for (SurveyDB survey : mSurveys) {
            survey.setStatus(Constants.SURVEY_SENDING);
            survey.save();
            Log.d(TAG, "Status of survey to be push is = " + survey.getStatus());
            survey.accept(mConvertToWSVisitor);
        }
    }

    private void pushSurveys() {
        WSClient mWSClient = new WSClient(getTimeout(mSurveys.size()));
        mSurveyContainerWSObject = mConvertToWSVisitor.getSurveyContainerWSObject();
        mWSClient.pushSurveys(mSurveyContainerWSObject,
                new WSClient.WSClientCallBack<SurveyWSResult>() {
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

    private int getTimeout(int vouchers) {
        return vouchers * 2000;
    }

    private void checkPushResult(SurveyWSResult surveyWSResult) {
        for (SurveyWSResponseAction responseAction : surveyWSResult.getActions()) {
            if (!responseAction.isSuccess()) {
                String message;
                if (responseAction.getResponse().getMsg() != null) {
                    message = String.format(
                            PreferencesState.getInstance().getContext().getString(
                                    R.string.survey_error), responseAction.getActionId(),
                            responseAction.getMessage(), responseAction.getResponse().getMsg());
                } else {
                    message = responseAction.getMessage();
                }
                mCallback.onInformativeError(new PushValueException(message));
            }
            SurveyDB surveyDB = null;
            String voucherId = responseAction.getResponse().getData().getVoucherCode();
            for (SurveyDB survey : mSurveys) {
                if (voucherId.contains(survey.getEventUid())) {
                    surveyDB = survey;
                    break;
                }
            }
            if (surveyDB != null && !surveyDB.getEventUid().equals(voucherId)) {
                Log.d(TAG, "Changing the UID of the survey old:" + surveyDB.getEventUid() + " new:"
                        + voucherId);
                surveyDB.setEventUid(voucherId);
                Context context = PreferencesState.getInstance().getContext();
                if (voucherIsPaper(voucherId)) {
                    mCallback.onInformativeMessage(String.format(
                            context.getResources().getString(R.string.give_voucher),
                            voucherId));
                }
                surveyDB.save();
            }

        }
        for (SurveyDB survey : mSurveys) {
            survey.setStatus(Constants.SURVEY_SENT);
            survey.save();
        }
        mCallback.onComplete();
    }

    private boolean voucherIsPaper(String voucherId) {
        for (SurveySendAction action : mSurveyContainerWSObject.getActions()) {
            if (voucherId.contains(action.getVoucher().getId())
                    && action.getVoucher().getType().equals(
                    Voucher.TYPE_PAPER)) {
                return true;
            }
        }
        return false;
    }


    private void putSurveysAsCompleted() {
        for (SurveyDB survey : mSurveys) {
            survey.setStatus(Constants.SURVEY_COMPLETED);
            survey.save();
        }
    }
}
