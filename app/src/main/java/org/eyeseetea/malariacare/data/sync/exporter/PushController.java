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
import android.util.Log;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.PushDhisSDKDataSource;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.ImportSummaryErrorException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;

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

            List<Survey> surveys = Survey.getAllCompletedSurveysNoReceiptReset();

            //Fixme Check if is necessary other conditions
            if (surveys == null || surveys.size() == 0) {

                Log.d("DpBlank", "Sets of Surveys to push");
                callback.onError(new SurveysToPushNotFoundException());
            } else {

                for (Survey srv : surveys) {
                    Log.d("DpBlank", "Survey to push " + srv.toString());
                    for (Value dv : srv.getValuesFromDB()) {
                        Log.d("DpBlank", "Values to push " + dv.toString());
                    }
                }
                mPushDhisSDKDataSource.wipeEvents();
                try {
                    convertToSDK(surveys);
                } catch (Exception ex) {
                    callback.onError(new ConversionException(ex));
                }

                if (EventExtended.getAllEvents().size() == 0) {
                    callback.onError(new ConversionException());
                } else {
                    pushData(callback);
                }
            }
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

    private void pushData(final IPushControllerCallback callback) {
        mPushDhisSDKDataSource.pushData(
                new IDataSourceCallback<Map<String, ImportSummary>>() {
                    @Override
                    public void onSuccess(
                            Map<String, ImportSummary> mapEventsImportSummary) {
                        mConvertToSDKVisitor.saveSurveyStatus(mapEventsImportSummary, callback);
                        ServerAPIController.banOrgUnitIfRequired();
                        callback.onComplete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if(throwable instanceof ImportSummaryErrorException) {
                            mConvertToSDKVisitor.setSurveysAsQuarantine();
                            ServerAPIController.banOrgUnitIfRequired();
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

}
