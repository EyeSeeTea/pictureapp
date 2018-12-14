package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResponseAction;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.exception.ConfigFileObsoleteException;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.domain.exception.push.PushValueException;
import org.eyeseetea.malariacare.network.ConnectivityStatus;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class WSPushController implements IPushController {

    private static final String TAG = "WSPushController";

    private ConvertToWSVisitor mConvertToWSVisitor;
    private List<SurveyDB> mSurveys;
    private eReferralsAPIClient mEReferralsAPIClient;
    private ISurveyRepository surveyRepository;
    private IPushControllerCallback mCallback;


    public WSPushController(Context context, ISurveyRepository surveyRepository) throws IllegalArgumentException {
        mEReferralsAPIClient = new eReferralsAPIClient(PreferencesEReferral.getWSURL());
        this.surveyRepository = surveyRepository;
        mConvertToWSVisitor = new ConvertToWSVisitor(context);
    }

    public WSPushController(eReferralsAPIClient eReferralsAPIClient, ISurveyRepository surveyRepository,
                            ConvertToWSVisitor convertToWSVisitor) {
        mEReferralsAPIClient = eReferralsAPIClient;
        this.surveyRepository = surveyRepository;
        mConvertToWSVisitor = convertToWSVisitor;
    }

    @Override
    public void push(IPushControllerCallback callback) {
        mCallback = callback;

        if (!ConnectivityStatus.isConnected(PreferencesState.getInstance().getContext())) {
            Log.d(TAG, "No network");
            callback.onError(new NetworkException());
        } else {
            mSurveys = SurveyDB.getAllCompletedSurveys();
            checkQuarantine();
            if (mSurveys == null || mSurveys.size() == 0) {
                callback.onError(new SurveysToPushNotFoundException("Null surveys"));
                return;
            }
            mCallback.onStartPushing();
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
                changeSurveysStatusTo(Constants.SURVEY_CONFLICT);
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


    private void checkQuarantine() {
        final List<Survey> surveys = surveyRepository.getAllQuarantineSurveys();
        mEReferralsAPIClient.getExistOnServer(surveys, new eReferralsAPIClient.WSClientCallBack() {
            @Override
            public void onSuccess(Object result) {
                List<String> sentUidList = (List<String>)result;
                for(Survey survey:surveys){
                    if(sentUidList.contains(survey.getUId())) {
                        survey.setStatus(Constants.SURVEY_SENT);
                    }else{
                        survey.setStatus(Constants.SURVEY_COMPLETED);
                    }
                    surveyRepository.save(survey);
                }
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
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
        mEReferralsAPIClient.setTimeoutMillis(getTimeout(mSurveys.size()));
        SurveyContainerWSObject surveyContainerWSObject =
                mConvertToWSVisitor.getSurveyContainerWSObject();
        mEReferralsAPIClient.pushSurveys(surveyContainerWSObject,
                new eReferralsAPIClient.WSClientCallBack<SurveyWSResult>() {
                    @Override
                    public void onSuccess(SurveyWSResult surveyWSResult) {
                        try {
                            checkPushResult(surveyWSResult);
                        } catch (ConversionException e) {
                            e.printStackTrace();
                            mCallback.onInformativeError(e);
                        } catch (Exception e) {
                            putSurveysToConflictStatus();
                            mCallback.onError(e);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Error pushing surveys: " + e.getMessage());
                        int status = Constants.SURVEY_CONFLICT;
                        if (e instanceof NetworkException) {
                            status = Constants.SURVEY_QUARANTINE;
                        } else if (e instanceof ConfigFileObsoleteException) {
                            status = Constants.SURVEY_SENT;
                        }
                        changeSurveysStatusTo(status);
                        mCallback.onError(e);
                    }
                });
    }

    private void putSurveysToConflictStatus() {
        for (SurveyDB surveyDB : mSurveys) {
            surveyDB.setStatus(Constants.SURVEY_CONFLICT);
            surveyDB.save();
        }
    }

    private int getTimeout(int vouchers) {
        return vouchers * 2000;
    }

    private void checkPushResult(SurveyWSResult surveyWSResult) throws ConversionException {

        for (SurveyDB survey : mSurveys) {
            survey.setStatus(Constants.SURVEY_SENT);
            survey.save();
        }
        try {
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
                String eventUid = responseAction.getActionId();
                String voucherId = responseAction.getResponse().getData().getVoucherCode();
                if (voucherId != null) {
                    for (SurveyDB survey : mSurveys) {
                        if (voucherId.contains(survey.getVoucherUid())) {
                            surveyDB = survey;
                            break;
                        }
                    }

                    if (surveyDB != null && !surveyDB.getVoucherUid().equals(voucherId)) {
                        Log.d(TAG,
                                "Changing the UID of the survey old:" + surveyDB.getVoucherUid()
                                        + " new:"
                                        + voucherId);
                        Context context = PreferencesState.getInstance().getContext();

                        mCallback.onInformativeMessage(String.format(
                                context.getResources().getString(R.string.voucher_id_changed),
                                surveyDB.getVoucherUid(), voucherId));

                        surveyDB.setVoucherUid(voucherId);

                        surveyDB.setEventUid(eventUid);

                        surveyDB.save();
                    }

                }
                putActionsStatusToSurveys(responseAction);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConversionException(e);
        }
        mCallback.onComplete();
    }

    private void putActionsStatusToSurveys(SurveyWSResponseAction responseAction) {
        if (responseAction.getActionId() != null) {
            for (SurveyDB survey : mSurveys) {
                if (responseAction.getActionId().equals(survey.getEventUid())) {
                    if (responseAction.isFailed()) {
                        survey.setStatus(Constants.SURVEY_CONFLICT);
                    } else if (!responseAction.isSuccess()) {
                        survey.setStatus(Constants.SURVEY_CONFLICT);
                    }
                    survey.save();
                }
            }
        }
    }


    private void changeSurveysStatusTo(int status) {
        for (SurveyDB survey : mSurveys) {
            survey.setStatus(status);
            survey.save();
        }
    }
}
