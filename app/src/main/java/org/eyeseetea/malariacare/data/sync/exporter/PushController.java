/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.remote.PushDhisSDKDataSource;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.entity.pushsummary.PushReport;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ClosedUserPushException;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.domain.exception.push.PushDhisException;
import org.eyeseetea.malariacare.domain.exception.push.PushReportException;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A static controller that orchestrate the push process
 */
public class PushController implements IPushController {

    private final String TAG = ".PushControllerB&D";

    private Context mContext;
    private PushDhisSDKDataSource mPushDhisSDKDataSource;
    private ConvertToSDKVisitor mConvertToSDKVisitor;


    public PushController(Context context) {
        mContext = context;
        mPushDhisSDKDataSource = new PushDhisSDKDataSource();
        mConvertToSDKVisitor = new ConvertToSDKVisitor(mContext);
    }

    public void push(final IPushControllerCallback callback) {

        if (!ServerAPIController.isNetworkAvailable()) {
            Log.d(TAG, "No network");
            callback.onError(new NetworkException());
        } else {
            Log.d(TAG, "Network connected");
            AsyncUserPush asyncOpenUserPush = new AsyncUserPush();
            asyncOpenUserPush.execute(callback);
        }
    }

    private void pushData(final IPushControllerCallback callback) {
        mPushDhisSDKDataSource.pushData(
                new IDataSourceCallback<Map<String, PushReport>>() {
                    @Override
                    public void onSuccess(
                            Map<String, PushReport> mapEventsReports) {
                        if(mapEventsReports==null || mapEventsReports.size()==0){
                            onError(new PushReportException("EventReport is null or empty"));
                            return;
                        }
                        try {
                            mConvertToSDKVisitor.saveSurveyStatus(mapEventsReports, callback);
                            callback.onComplete();
                        }catch (Exception e){
                            onError(new PushReportException(e));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof PushReportException
                                || throwable instanceof PushDhisException) {
                            mConvertToSDKVisitor.setSurveysAsQuarantine();
                        }
                        callback.onError(throwable);
                    }
                });
    }

    /**
     * Launches visitor that turns an APP survey into a SDK event
     */
    private void convertToSDK(List<Survey> surveys) throws Exception {
        Log.d(TAG, "Converting APP survey into a SDK event");
        for (Survey survey : surveys) {
            survey.setStatus(Constants.SURVEY_SENDING);
            survey.save();
            Log.d(TAG, "Status of survey to be push is = " + survey.getStatus());
            survey.accept(mConvertToSDKVisitor);
        }
    }

    public class AsyncUserPush extends AsyncTask<IPushControllerCallback, Void, Void> {
        //userCloseChecker is never saved, Only for check if the date is closed.
        Boolean isUserClosed = false;
        IPushControllerCallback callback;

        List<Survey> surveys = new ArrayList<>();
        @Override
        protected Void doInBackground(IPushControllerCallback... params) {
            Log.d(TAG, "Async user push running");
            callback = params[0];
            surveys = Survey.getAllCompletedSurveysNoReceiptReset();

            User loggedUser = User.getLoggedUser();
            if (loggedUser != null && loggedUser.getUid() != null) {
                isUserClosed = ServerAPIController.isUserClosed(User.getLoggedUser().getUid());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "Async user push finish");
            if(isUserClosed==null){
                callback.onError(new ApiCallException("The user api call returns a exception"));
                return;
            }
            if (isUserClosed) {
                Log.d(TAG, "The user is closed, Surveys not sent");
                callback.onError(new ClosedUserPushException());
            } else {
                if (surveys == null || surveys.size() == 0) {
                    callback.onError(new SurveysToPushNotFoundException("Null surveys"));
                    return;
                }

                Log.d(TAG, "wipe events");
                mPushDhisSDKDataSource.wipeEvents();
                try {
                    Log.d(TAG, "convert surveys to sdk");
                    convertToSDK(surveys);
                } catch (Exception ex) {
                    callback.onError(new ConversionException(ex));
                    return;
                }

                if (EventExtended.getAllEvents().size() == 0) {
                    callback.onError(new ConversionException());
                    return;
                } else {
                    Log.d(TAG, "push data");
                    pushData(callback);
                }
            }
        }
    }
}
