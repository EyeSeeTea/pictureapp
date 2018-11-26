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

package org.eyeseetea.malariacare.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.CompositeScoreDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.LocationMemory;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.EmptyLocationException;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.network.factory.UnsafeOkHttpsClientFactory;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class PushClient {

    /**
     * Content type request: json + utf8
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    /**
     * Endpoint sending events
     */
    private static final String DHIS_PUSH_API = "/api/events";
    private static final String COMPLETED = "COMPLETED";
    private static final String TAG_PROGRAM = "program";
    private static final String TAG_ORG_UNIT = "orgUnit";
    private static final String TAG_EVENTDATE = "eventDate";
    private static final String TAG_STATUS = "status";
    private static final String TAG_STOREDBY = "storedBy";
    private static final String TAG_COORDINATE = "coordinate";
    private static final String TAG_COORDINATE_LAT = "latitude";
    private static final String TAG_COORDINATE_LNG = "longitude";
    private static final String TAG_DATAVALUES = "dataValues";
    private static final String TAG_DATAELEMENT = "dataElement";
    private static final String TAG_VALUE = "value";
    private static String TAG = ".PushClient";
    /**
     * Current server url
     */
    private static String DHIS_SERVER = "https://www.psi-mis.org";
    SurveyDB mSurveyDB;
    Activity activity;
    Context applicationContext;


    public PushClient(Context applicationContext) {
        this.applicationContext = applicationContext;
        getPreferenceValues();
    }

    public PushClient(Activity activity) {
        this((Context) activity);
        this.activity = activity;
    }

    public PushClient(SurveyDB surveyDB, Activity activity) {
        this((Activity) activity);
        this.mSurveyDB = surveyDB;
    }

    public void setSurveyDB(SurveyDB surveyDB) {
        this.mSurveyDB = surveyDB;
    }

    /**
     * Get the user settings values from the shared Preferences
     */
    public void getPreferenceValues() {
        PreferencesState.getInstance().reloadPreferences();
        String url = PreferencesState.getInstance().getDhisURL();
        if (url != null || !("".equals(url))) {
            DHIS_SERVER = url;
        }
    }

    public PushResult push() {
        try {
            JSONObject data = prepareMetadata();
            data = prepareDataElements(data);
            PushResult result = new PushResult(pushData(data));
            if (result.isSuccessful()) {
                updateSurveyState();
            }
            return result;
        } catch (EmptyLocationException ex) {
            new ApiCallException(ex);
            return new PushResult(ex);
        } catch (JSONException ex) {
            new ApiCallException(ex);
            return new PushResult(ex);
        } catch (ApiCallException ex){
            return new PushResult(ex);
        }
    }

    public PushResult pushBackground() {
        try {
            JSONObject data = prepareMetadata();
            data = prepareDataElements(data);
            PushResult result = new PushResult(pushData(data));
            if (result.isSuccessful()) {
                //Survey -> Sent
                this.mSurveyDB.setStatus(Constants.SURVEY_SENT);
                this.mSurveyDB.save();

                //check if the user was sent more than the limit
                //ServerAPIController.banOrgUnitIfRequired();
            }
            return result;
        } catch (Exception ex) {
            //Log.e(TAG, ex.getMessage());
            return new PushResult(ex);
        }
    }

    /**
     * Pushes data to DHIS Server
     */
    private JSONObject pushData(JSONObject data) throws ApiCallException {
        Response response = null;

        final String DHIS_URL = getDhisURL();

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator();

        OkHttpClient client = UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient(basicAuthenticator);

        RequestBody body = RequestBody.create(JSON, data.toString());
        Request request = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER,
                        basicAuthenticator.getCredentials())
                .url(DHIS_URL)
                .post(body)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new ApiCallException(e);
        }
        return ServerApiUtils.getApiResponseAsJSONObject(response);
    }

    public void updateSurveyState() {

        //Reload data using service
        Intent surveysIntent = new Intent(activity, SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        activity.startService(surveysIntent);
    }

    /**
     * Adds metadata info to json object
     *
     * @return JSONObject with progra, orgunit, eventdate and so on...
     */
    private JSONObject prepareMetadata()
            throws EmptyLocationException, JSONException, ApiCallException {
        Log.d(TAG, "prepareMetadata for survey: " + mSurveyDB.getId_survey());

        JSONObject object = new JSONObject();
        object.put(TAG_PROGRAM, mSurveyDB.getProgramDB().getUid());
        object.put(TAG_ORG_UNIT, ServerAPIController.getOrgUnitUID());
        object.put(TAG_EVENTDATE,
                android.text.format.DateFormat.format("yyyy-MM-dd",
                        mSurveyDB.getEventDate()));
        object.put(TAG_STATUS, COMPLETED);
        object.put(TAG_STOREDBY, mSurveyDB.getUserDB().getName());
        //TODO: put it in the object.

        Location lastLocation = LocationMemory.get(mSurveyDB.getId_survey());
        //If there is no location (location is required) -> exception
        if (lastLocation == null) {
            throw new EmptyLocationException(activity.getString(R.string.dialog_error_push_no_location));
        }
        object.put(TAG_COORDINATE, prepareCoordinates(lastLocation));

        Log.d(TAG, "prepareMetadata: " + object.toString());
        return object;
    }

    private JSONObject prepareCoordinates(Location location) throws JSONException {
        JSONObject coordinate = new JSONObject();

        if (location == null) {
            coordinate.put(TAG_COORDINATE_LAT, JSONObject.NULL);
            coordinate.put(TAG_COORDINATE_LNG, JSONObject.NULL);
        } else {
            coordinate.put(TAG_COORDINATE_LAT, location.getLatitude());
            coordinate.put(TAG_COORDINATE_LNG, location.getLongitude());
        }
        return coordinate;
    }

    /**
     * Adds questions and scores values to the JSON object
     * Format: {dataValues: [{dataElement:'234567',value:'34'}, ...]}
     *
     * @param data JSON object to update
     */
    private JSONObject prepareDataElements(JSONObject data) throws JSONException {
        Log.d(TAG, "prepareDataElements for survey: " + mSurveyDB.getId_survey());

        //Add dataElement per values
        JSONArray values = prepareValues(new JSONArray());

        //Add dataElement per compositeScores
        values = prepareCompositeScores(values);

        data.put(TAG_DATAVALUES, values);
        Log.d(TAG, "prepareDataElements result: " + data.toString());
        return data;
    }

    /**
     * Add a dataElement per value (answer)
     */
    private JSONArray prepareValues(JSONArray values) throws JSONException {
        for (ValueDB valueDB : mSurveyDB.getValuesFromDB()) {
            values.put(prepareValue(valueDB));
        }
        return values;
    }

    private JSONArray prepareCompositeScores(JSONArray values) throws JSONException {

        //Cleans score
        ScoreRegister.clear();

        //Prepare scores info
        List<CompositeScoreDB> compositeScoreDBList = ScoreRegister.loadCompositeScores(mSurveyDB);

        //1 CompositeScoreDB -> 1 dataValue
        for (CompositeScoreDB compositeScoreDB : compositeScoreDBList) {
            values.put(prepareValue(compositeScoreDB));
        }

        values.put(prepareDataElementValue((PreferencesState.getInstance().getContext().getString(R.string.control_data_element_phone_metadata)), Session.getPhoneMetaDataValue()));
        if (PreferencesState.getInstance().getContext().getString(R.string.control_data_element_datetime_capture) != null && !PreferencesState.getInstance().getContext().getString(R.string.control_data_element_datetime_capture).equals("")) {
            values.put(prepareDataElementValue(PreferencesState.getInstance().getContext().getString(R.string.control_data_element_datetime_capture),
                    EventExtended.format(mSurveyDB.getCreationDate(),
                            EventExtended.AMERICAN_DATE_FORMAT)));
        }
        if (PreferencesState.getInstance().getContext().getString(R.string.control_data_element_datetime_sent) != null && !PreferencesState.getInstance().getContext().getString(R.string.control_data_element_datetime_sent).equals("")) {
            values.put(prepareDataElementValue(PreferencesState.getInstance().getContext().getString(R.string.control_data_element_datetime_sent),
                    EventExtended.format(new Date(), EventExtended.AMERICAN_DATE_FORMAT)));
        }
        return values;
    }

    /**
     * Adds a pair dataElement|value according to the passed value.
     * Format: {dataValues: [{dataElement:'234567',value:'34'}, ...]}
     */
    private JSONObject prepareValue(ValueDB valueDB) throws JSONException {
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DATAELEMENT, valueDB.getQuestionDB().getUid());
        elementObject.put(TAG_VALUE, valueDB.getValue());
        return elementObject;
    }

    /**
     * Adds a pair dataElement|value according to the passed value.
     * Format: {dataValues: [{dataElement:'234567',value:'34'}, ...]}
     */
    private JSONObject prepareDataElementValue(String uid, String value) throws JSONException {
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DATAELEMENT, uid);
        elementObject.put(TAG_VALUE, value);
        return elementObject;
    }

    /**
     * Adds a pair dataElement|value according to the 'compositeScoreDB' of the value.
     * Format: {dataValues: [{dataElement:'234567',value:'34'}, ...]}
     */
    private JSONObject prepareValue(CompositeScoreDB compositeScoreDB) throws JSONException {
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DATAELEMENT, compositeScoreDB.getUid());
        elementObject.put(TAG_VALUE, Utils.round(ScoreRegister.getCompositeScore(compositeScoreDB)));
        return elementObject;
    }

    /**
     * Returns the URL that points to the DHIS server API according to preferences.
     */
    private String getDhisURL() {
        String url = DHIS_SERVER + DHIS_PUSH_API;
        return ServerApiUtils.encodeBlanks(url);
    }

}
