package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.HttpUrl;
import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.PushController;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.SyncProgressStatus;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.network.SurveyChecker;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

import java.util.ArrayList;
import java.util.List;

public class PushSurveysUseCase {
    public static final String TAG = ".PushSurveysUseCase";
    private Context context;
    private Callback mCallback;

    public PushSurveysUseCase(Context context) {
        this.context = context;
    }

    public void execute(Callback callback) {
        mCallback = callback;

        Log.d(TAG, "Push in Progress" + PushController.getInstance().isPushInProgress());

        SurveyChecker.launchQuarantineChecker();

        if (PushController.getInstance().isPushInProgress()) {
            return;
        }

        //Launch push according to current server
        pushAllPendingSurveys();
    }

    private synchronized void startProgress() {
        Log.d(TAG, "startProgress, registering in bus");
        Dhis2Application.bus.register(this);
    }

    private synchronized void stopProgress() {
        Log.d(TAG, "stopProgress, unregistering from bus");
        Dhis2Application.bus.unregister(this);
    }

    /**
     * Push all pending surveys
     */
    private void pushAllPendingSurveys() {
        Log.d(TAG, "pushAllPendingSurveys (Thread:" + Thread.currentThread().getId() + ")");

        PushController.getInstance().setPushInProgress(true);

        //Fixme the method getAllUnsentSurveys returns all the surveys not sent(completed,
        // inprogres, and hide)
        //Select surveys from sql
        List<Survey> surveys = Survey.getAllSurveysToBeSent();

        //No surveys to send -> done
        if (surveys == null || surveys.isEmpty()) {
            PushController.getInstance().setPushInProgress(false);
            return;
        }

        //Server is not ready for push -> move on
        if (!ServerAPIController.isReadyForPush()) {
            PushController.getInstance().setPushInProgress(false);
            return;
        }

        pushBySDK();
    }

    /**
     * Push via sdk requires 2 steps:
     * -Login into sdk
     * -Push data via PushController
     */
    private void pushBySDK() {
        Log.i(TAG, "pushBySDK");
        startProgress();

        //Init sdk login
        DhisService.logInUser(HttpUrl.parse(ServerAPIController.getServerUrl()),
                ServerAPIController.getSDKCredentials());
    }

    @Subscribe
    public void callbackLoginPrePush(NetworkJob.NetworkJobResult<ResourceType> result) {
        Log.d(TAG, "callbackLoginPrePush  " + PushController.getInstance().isPushInProgress());


        if (!PushController.getInstance().isPushInProgress()) {
            return;
        }
        Log.d(TAG, "callbackLoginPrePush");
        //Nothing to check
        if (result == null || result.getResourceType() == null || !result.getResourceType().equals(
                ResourceType.USERS)) {
            return;
        }

        //Login failed
        if (result.getResponseHolder().getApiException() != null) {
            Log.e(TAG, "callbackLoginPrePush cannot login via sdk");
            stopProgress();
            PushController.getInstance().setPushInProgress(false);
            return;
        }

        callPushBySDK();
    }

    private void callPushBySDK() {

        List<Survey> filteredSurveys = new ArrayList<>();
        List<Survey> surveys = Survey.getAllSurveysToBeSent();

        //Check surveys not in progress
        for (Survey survey : surveys) {

            if (survey.isCompleted(survey.getId_survey()) && survey.getValues().size() > 0) {
                Log.d("DpBlank", "Survey is completed" + survey.getId_survey());
                filteredSurveys.add(survey);
            } else {
                Log.d("DpBlank", "Survey is sent" + survey.getId_survey());
            }
        }

        if (filteredSurveys.size() == 0) {
            stopProgress();
            PushController.getInstance().setPushInProgress(false);
            return;
        }

        //Login successful start reload
        PushController.getInstance().push(context, filteredSurveys);
    }

    /**
     * Callback that is invoked once the push is over or has failed
     */
    @Subscribe
    public void onPushBySDKFinished(final SyncProgressStatus syncProgressStatus) {
        Log.d(TAG, "onPushBySDKFinished ");
        if (syncProgressStatus == null) {
            Log.i(TAG, "onPushBySDKFinished null");
            stopProgress();
            return;
        }

        //Step
        if (syncProgressStatus.hasProgress()) {
            Log.i(TAG, "onPushBySDKFinished progress: " + syncProgressStatus.getMessage());
            return;
        }

        //Exception
        if (syncProgressStatus.hasError()) {
            Log.w(TAG,
                    "onPushBySDKFinished error: " + syncProgressStatus.getException().getMessage());
            mCallback.onPushError(syncProgressStatus.getException().getMessage());
            stopProgress();
            return;
        }

        //Finish
        if (syncProgressStatus.isFinish()) {
            Log.i(TAG, "onPushBySDKFinished finished");
            mCallback.onPushFinished();
            stopProgress();
        }
    }

    public interface Callback {
        void onPushFinished();

        void onPushError(String message);
    }
}

