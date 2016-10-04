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

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.EventExtended;
import org.eyeseetea.malariacare.database.model.CompositeScore;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.LocationMemory;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.phonemetadata.PhoneMetaData;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Jose on 20/06/2015.
 */
public class PushClient {

    private static String TAG=".PushClient";

    /**
     * Current server url
     */
    private static String DHIS_SERVER ="https://www.psi-mis.org";

    /**
     * Endpoint sending events
     */
    private static final String DHIS_PUSH_API="/api/events";

    /**
     * Content type request: json + utf8
     */
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private static final String COMPLETED="COMPLETED";
    private static final String TAG_PROGRAM="program";
    private static final String TAG_ORG_UNIT="orgUnit";
    private static final String TAG_EVENTDATE="eventDate";
    private static final String TAG_STATUS="status";
    private static final String TAG_STOREDBY="storedBy";
    private static final String TAG_COORDINATE="coordinate";
    private static final String TAG_COORDINATE_LAT="latitude";
    private static final String TAG_COORDINATE_LNG="longitude";
    private static final String TAG_DATAVALUES="dataValues";
    private static final String TAG_DATAELEMENT="dataElement";
    private static final String TAG_VALUE="value";

    /**
     * Hardcoded UID for dataElement PhoneMetaData
     */
    public static String PHONEMETADA_UID ="RuNZUhiAmlv";
    /**
     * Hardcoded UID for dataElement DATETIME CAPTURE
     */
    public static String DATETIME_CAPTURE_UID ="qWMb2UM2ikL";
    /**
     * Hardcoded UID for dataElement DATETIME SENT
     */
    public static String DATETIME_SENT_UID ="aBahytzj2u0";

    Survey survey;
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

    public PushClient(Survey survey, Activity activity) {
        this((Activity) activity);
        this.survey = survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    /**
     * Get the user settings values from the shared Preferences
     */
    public void getPreferenceValues(){
        PreferencesState.getInstance().reloadPreferences();
        String url= PreferencesState.getInstance().getDhisURL();
        if(url!=null || !("".equals(url))){
            DHIS_SERVER=url;
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
        } catch (Exception ex) {
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
                this.survey.setStatus(Constants.SURVEY_SENT);
                this.survey.save();

                //check if the user was sent more than the limit
                ServerAPIController.banOrgUnitIfRequired();
            }
            return result;
        } catch (Exception ex) {
            //Log.e(TAG, ex.getMessage());
            return new PushResult(ex);
        }
    }

    /**
     * Pushes data to DHIS Server
     * @param data
     */
    private JSONObject pushData(JSONObject data)throws Exception {
        Response response = null;

        final String DHIS_URL = getDhisURL();

        OkHttpClient client = UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator();
        client.setAuthenticator(basicAuthenticator);

        RequestBody body = RequestBody.create(JSON, data.toString());
        Request request = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                .url(DHIS_URL)
                .post(body)
                .build();

        response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            Log.e(TAG, "pushData (" + response.code() + "): " + response.body().string());
            throw new IOException(response.message());
        }
        return  parseResponse(response.body().string());
    }

    public void updateSurveyState(){

        //Reload data using service
        Intent surveysIntent=new Intent(activity, SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        activity.startService(surveysIntent);
    }

    /**
     * Adds metadata info to json object
     * @return JSONObject with progra, orgunit, eventdate and so on...
     * @throws Exception
     */
    private JSONObject prepareMetadata() throws Exception{
        Log.d(TAG, "prepareMetadata for survey: " + survey.getId_survey());

        JSONObject object=new JSONObject();
        object.put(TAG_PROGRAM, survey.getProgram().getUid());
        object.put(TAG_ORG_UNIT, ServerAPIController.getOrgUnitUID());
        object.put(TAG_EVENTDATE, android.text.format.DateFormat.format("yyyy-MM-dd", survey.getCompletionDate()));
        object.put(TAG_STATUS, COMPLETED);
        object.put(TAG_STOREDBY, survey.getUser().getName());
        //TODO: put it in the object.

        Location lastLocation = LocationMemory.get(survey.getId_survey());
        //If there is no location (location is required) -> exception
        if(lastLocation==null){
            throw new Exception(activity.getString(R.string.dialog_error_push_no_location));
        }
        object.put(TAG_COORDINATE, prepareCoordinates(lastLocation));

        Log.d(TAG, "prepareMetadata: " + object.toString());
        return object;
    }

    private JSONObject prepareCoordinates(Location location) throws Exception{
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
     * @param data JSON object to update
     * @throws Exception
     */
    private JSONObject prepareDataElements(JSONObject data)throws Exception{
        Log.d(TAG, "prepareDataElements for survey: " + survey.getId_survey());

        //Add dataElement per values
        JSONArray values=prepareValues(new JSONArray());

        //Add dataElement per compositeScores
        values=prepareCompositeScores(values);

        data.put(TAG_DATAVALUES, values);
        Log.d(TAG, "prepareDataElements result: " + data.toString());
        return data;
    }

    /**
     * Add a dataElement per value (answer)
     * @param values
     * @return
     * @throws Exception
     */
    private JSONArray prepareValues(JSONArray values) throws Exception{
        for (Value value : survey.getValues()) {
            values.put(prepareValue(value));
        }
        return values;
    }

    private JSONArray prepareCompositeScores(JSONArray values) throws Exception{

        //Cleans score
        ScoreRegister.clear();

        //Prepare scores info
        List<CompositeScore> compositeScoreList=ScoreRegister.loadCompositeScores(survey);

        //1 CompositeScore -> 1 dataValue
        for(CompositeScore compositeScore:compositeScoreList){
            values.put(prepareValue(compositeScore));
        }

        PhoneMetaData phoneMetaData= Session.getPhoneMetaData();
        values.put(prepareDataElementValue(PHONEMETADA_UID, phoneMetaData.getPhone_metaData()));
        if(DATETIME_CAPTURE_UID !=null && !DATETIME_CAPTURE_UID.equals(""))
            values.put(prepareDataElementValue(DATETIME_CAPTURE_UID, EventExtended.format(survey.getCompletionDate(),EventExtended.AMERICAN_DATE_FORMAT)));
        if(DATETIME_SENT_UID !=null && !DATETIME_SENT_UID.equals(""))
            values.put(prepareDataElementValue(DATETIME_SENT_UID, EventExtended.format(new Date(),EventExtended.AMERICAN_DATE_FORMAT)));
        return values;
    }

    /**
     * Adds a pair dataElement|value according to the passed value.
     * Format: {dataValues: [{dataElement:'234567',value:'34'}, ...]}
     * @param value
     * @return
     * @throws Exception
     */
    private JSONObject prepareValue(Value value) throws Exception{
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DATAELEMENT, value.getQuestion().getUid());
        elementObject.put(TAG_VALUE, value.getValue());
        return elementObject;
    }

    /**
     * Adds a pair dataElement|value according to the passed value.
     * Format: {dataValues: [{dataElement:'234567',value:'34'}, ...]}
     * @param value
     * @return
     * @throws Exception
     */
    private JSONObject prepareDataElementValue(String uid, String value) throws Exception{
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DATAELEMENT, uid);
        elementObject.put(TAG_VALUE, value);
        return elementObject;
    }
    /**
     * Adds a pair dataElement|value according to the 'compositeScore' of the value.
     * Format: {dataValues: [{dataElement:'234567',value:'34'}, ...]}
     * @param compositeScore
     * @return
     * @throws Exception
     */
    private JSONObject prepareValue(CompositeScore compositeScore) throws Exception{
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DATAELEMENT, compositeScore.getUid());
        elementObject.put(TAG_VALUE, Utils.round(ScoreRegister.getCompositeScore(compositeScore)));
        return elementObject;
    }

    /**
     * Returns the URL that points to the DHIS server API according to preferences.
     * @return
     */
    private String getDhisURL(){
        String url= DHIS_SERVER+DHIS_PUSH_API;
        return ServerAPIController.encodeBlanks(url);
    }

    private JSONObject parseResponse(String responseData)throws Exception{
        try{
            JSONObject jsonResponse=new JSONObject(responseData);
            Log.i(TAG, "parseResponse: " + jsonResponse);
            return jsonResponse;
        }catch(Exception ex){
            throw new Exception(activity.getString(R.string.dialog_info_push_bad_credentials));
        }
    }

}
