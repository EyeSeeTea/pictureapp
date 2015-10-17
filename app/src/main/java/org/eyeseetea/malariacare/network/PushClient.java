/*
 * Copyright (c) 2015.
 *
 * This file is part of Health Network QIS App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.CompositeScore;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Tab;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jose on 20/06/2015.
 */
public class PushClient {



    private static String TAG=".PushClient";

    //This change for a sharedpreferences url that is selected from the settings screen

    private static String DHIS_DEFAULT_SERVER="https://malariacare.psi.org";
    private static String DHIS_PUSH_API="/api/events";
    private static String DHIS_PULL_ORG_UNIT_API ="/api/organisationUnits.json?paging=false&fields=id&filter=code:eq:%s";
    private static String DHIS_PULL_PROGRAM="/api/programs/";
    private static String DHIS_PULL_ORG_UNITS_API=".json?fields=organisationUnits";
    private static String DHIS_USERNAME="testing";
    private static String DHIS_PASSWORD="Testing2015";
    private static String DHIS_DEFAULT_CODE="KH_Cambodia";
    private static String DHIS_PULL_CLOSED_DATE="/api/organisationUnits/%s/closedDate";


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static Boolean BANNED=false;

    private static String COMPLETED="COMPLETED";

    private static String TAG_PROGRAM="program";
    private static String TAG_ORG_UNIT="orgUnit";
    private static String TAG_EVENTDATE="eventDate";
    private static String TAG_STATUS="status";
    private static String TAG_STOREDBY="storedBy";
    private static String TAG_COORDINATE="coordinate";
    private static String TAG_COORDINATE_LAT="latitude";
    private static String TAG_COORDINATE_LNG="longitude";
    private static String TAG_DATAVALUES="dataValues";
    private static String TAG_DATAELEMENT="dataElement";
    private static String TAG_VALUE="value";
    private static String TAG_CloseData="closedDate";


    //When PushClient is sending the event, the activity is null, becouse PushClient is called in a InstanceService without activity.
    //I can´t access to the activity or context for get the r.string values.
    private static String TAG_IMEI="RuNZUhiAmlv";
    private static String TAG_PHONE="UkGuMlmNtJH";
    private static String TAG_PHONE_SERIAL="zZ1LFI0FplS";


    private static int DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR=30;
    private static int DHIS_LIMIT_HOURS=1;

    Survey survey;
    Activity activity;

    public PushClient(Survey survey, Activity activity) {
        this.survey = survey;
        this.activity = activity;
    }

    public PushClient(Survey survey) {
        this.survey = survey;
    }

    public void setUrlPreferentShared(String url) {
        DHIS_DEFAULT_SERVER=url;
    }

    public PushResult push() {
        //Check the organization is banned, if not, check if closeddate for check if the survey can be sent
        if(!BANNED && isOrganizationClosed()) {
            try {
                JSONObject data = prepareMetadata();
                data = prepareDataElements(data);
                PushResult result = new PushResult(pushData(data));
                if (result.isSuccessful()) {
                    updateSurveyState();
                }
                return result;
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                return new PushResult(ex);
            }
        }
        BANNED=true;
        JSONObject banned= null;
        try {
            banned = new JSONObject("{\"Simulate_response\":\"Org_unitBanned\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new PushResult(banned);
    }

    public PushResult pushBackground() {
        //Check the organization is banned, if not, check if closeddate for check if the survey can be sent
        if(!BANNED && isOrganizationClosed()) {
            try {
                JSONObject data = prepareMetadata();
                data = prepareDataElements(data);
                PushResult result = new PushResult(pushData(data));
                if (result.isSuccessful()) {
                    this.survey.setStatus(Constants.SURVEY_SENT);
                    this.survey.save();
                    //Change status
                    //check if the user was sent more than the limit
                    List<Survey> sentSurveys = Survey.getAllHideAndSentSurveys();
                    int countDates = 0;
                    for (int i = sentSurveys.size() - 1; i >= 0; i--) {
                        //If isDateOverLimit is TRUE the survey is out of the limit control
                        if (!Utils.isDateOverLimit(Utils.DateToCalendar(sentSurveys.get(i).getEventDate()), DHIS_LIMIT_HOURS)) {
                            countDates++;
                        }
                    }
                    if (countDates > DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR) {
                        this.banOrg(DHIS_DEFAULT_CODE);
                    }
                }
                return result;
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                return new PushResult(ex);
            }
        }
        BANNED=true;
        JSONObject banned= null;
        try {
            banned = new JSONObject("{\"Simulate_response\":\"Org_unitBanned\"}");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new PushResult(banned);
    }

    //Get the url for the closed data
    private String getPatchClosedDateUrl(String dhis_default_code){
        //Get the org_ID
        String DHIS_PULL_URL=dhis_default_code;
        try {
        String orgid= null;
            orgid = pullOrgUnitUID(DHIS_PULL_URL);
        //Get the url with the org_Id
        DHIS_PULL_URL=getClosingDateURL(orgid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DHIS_PULL_URL;
    }

    //Block the organization for future push actions. deducting one day to the closed date than the systemdate.
    private void banOrg(String code) {
        try {
            //https://malariacare.psi.org/api/organisationUnits/Pg91OgEIKIm/closedDate
            String DHIS_PULL_URL=getPatchClosedDateUrl(code);

            JSONObject data =prepareClosingDateValue();
            Response response=executeCall(data, DHIS_PULL_URL, "PATCH");

            if(!response.isSuccessful()){
                Log.e(TAG, "closingDateURL (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }

            JSONObject responseJSON = parseResponse(response.body().string());
            //TODO:edit closeddata
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject prepareClosingDateValue() throws Exception{
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        sysDate.set(Calendar.HOUR, sysDate.get(Calendar.HOUR) - 24);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateFormatted = format.format(sysDate.getTime());
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_CloseData, dateFormatted);
        Log.d(TAG,"closingDateURL:EndDate:"+ dateFormatted);
        return elementObject;
    }
    public void updateSurveyState(){
        //Change status
        this.survey.setStatus(Constants.SURVEY_SENT);
        this.survey.save();

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
        Log.d(TAG, "prepareMetadata for survey: " + survey.getId());

        JSONObject object=new JSONObject();
        object.put(TAG_PROGRAM, survey.getProgram().getUid());
        object.put(TAG_ORG_UNIT, prepareOrgUnit());
        object.put(TAG_EVENTDATE, android.text.format.DateFormat.format("yyyy-MM-dd", survey.getCompletionDate()));
        object.put(TAG_STATUS, COMPLETED);
        object.put(TAG_STOREDBY, survey.getUser().getName());
        //TODO: put it in the object.

        Location lastLocation = LocationMemory.get(survey.getId());
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

    private String prepareOrgUnit() throws Exception{
        String orgUnit;

        //take orgUnit code from sharedPreferences
        String code=PreferencesState.getInstance().getOrgUnit();
        if(code==null || "".equals(code)){
            code=DHIS_DEFAULT_CODE;
        }
        //pull UID from DHIS
        orgUnit=pullOrgUnitUID(code);

        return orgUnit;
    }

    private String pullOrgUnitUID(String code) throws Exception{
        //https://malariacare.psi.org/api/organisationUnits.json?paging=false&fields=id&filter=code:eq:KH_Cambodia
        final String DHIS_PULL_URL=getDhisOrgUnitURL(code);

        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator=new BasicAuthenticator();
        client.setAuthenticator(basicAuthenticator);

        Request request = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                .url(DHIS_PULL_URL)
                .build();

        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()){
            Log.e(TAG, "pullOrgUnitUID (" + response.code()+"): "+response.body().string());
            throw new IOException(response.message());
        }

        JSONObject responseJSON=parseResponse(response.body().string());
        JSONArray responseArray=(JSONArray) responseJSON.get("organisationUnits");
        if(responseArray.length()==0){
            Log.e(TAG, "pullOrgUnitUID: No UID for code " + code);
            throw new IOException(activity.getString(R.string.dialog_error_push_no_uid)+" "+code);
        }
        return responseArray.getJSONObject(0).getString("id");
    }

    /**
     * This method returns a String[] whit the Organitation codes
     * @throws Exception
     */
    public String[] pullOrgUnitsCodes() throws Exception{
        //https://malariacare.psi.org/api/programs/IrppF3qERB7.json?fields=organisationUnits
        final String DHIS_PULL_URL=getDhisOrgUnitsURL();

        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator=new BasicAuthenticator();
        client.setAuthenticator(basicAuthenticator);

        Log.e(TAG, "pullOrgUnitUID URL (" + DHIS_PULL_URL);
        Request request = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                .url(DHIS_PULL_URL)
                .build();

        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()){
            Log.e(TAG, "pullOrgUnitUID (" + response.code()+"): "+response.body().string());
            throw new IOException(response.message());
        }

        JSONObject responseJSON=parseResponse(response.body().string());
        JSONArray responseArray=(JSONArray) responseJSON.get("organisationUnits");
        if(responseArray.length()==0){
            Log.e(TAG, "pullOrgUnitUID: No org_unit ");
            throw new IOException(activity.getString(R.string.dialog_error_push_no_uid));
        }
        return jsonArrayToStringArray(responseArray,"code");
    }

    /**
     * Get a JSONArray and returns a String array from a key value()
     * @param value is the key in the first level.
     * @param json is JSONArray
     * @throws Exception
     */
    public String[] jsonArrayToStringArray(JSONArray json,String value) {
        int size=0;
        for (int i = 0; i < json.length(); ++i) {
            JSONObject row = null;
            try {
                row = json.getJSONObject(i);
                if(row.getString(value)!=null)
                    size++;
            } catch (JSONException e) {
            }
        }
        int position=0;
        String[] strings=new String[size];
        for (int i = 0; i < json.length(); ++i) {
            JSONObject row = null;
            try {
                row = json.getJSONObject(i);
                if(row.getString(value)!=null)
                    strings[position++] = row.getString(value);
            } catch (JSONException e) {
            }
        }
        return strings;
    }

    /**
     * Get the closedData
     * @param code is the organitation unit
     * @throws Exception
     */
    private Calendar getOrgUnitClosedDate(String code) throws Exception{
        //https://malariacare.psi.org/api/organisationUnits/Pg91OgEIKIm/closedDate
        String DHIS_PULL_URL=getPatchClosedDateUrl(code);
        Log.d(TAG,"orgunticlos"+DHIS_PULL_URL);
        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator=new BasicAuthenticator();
        client.setAuthenticator(basicAuthenticator);

        Request request = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                .url(DHIS_PULL_URL)
                .build();

        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()){
            Log.e(TAG, "closingDateURL (" + response.code()+"): "+response.body().string());
            throw new IOException(response.message());
        }
        String jsonData = response.body().string();
        Log.d(TAG,"Response"+jsonData);
        JSONObject responseObject=new JSONObject(jsonData);
        if(responseObject.length()==0){
            Log.e(TAG, "closingDateURL: No UID for code " + code);
            throw new IOException(activity.getString(R.string.dialog_error_push_no_uid)+" "+code);
        }
        Log.d(TAG,"data:"+responseObject.getString("closedDate"));
        Calendar closeDate;
        try {
            closeDate = Utils.parseStringToCalendar(responseObject.getString("closedDate"));
        }
        catch(Exception e){
            closeDate=null;
        }
        return closeDate;
    }

    /**
     * Adds questions and scores values to the JSON object
     * Format: {dataValues: [{dataElement:'234567',value:'34'}, ...]}
     * @param data JSON object to update
     * @throws Exception
     */
    private JSONObject prepareDataElements(JSONObject data)throws Exception{
        Log.d(TAG, "prepareDataElements for survey: " + survey.getId());

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

        //Register scores for tabs
        List<Tab> tabs=survey.getProgram().getTabs();
        ScoreRegister.registerTabScores(tabs);

        //Register scores for composites
        List<CompositeScore> compositeScoreList=CompositeScore.listAllByProgram(survey.getProgram());
        ScoreRegister.registerCompositeScores(compositeScoreList);

        //Initialize scores x question
        ScoreRegister.initScoresForQuestions(Question.listAllByProgram(survey.getProgram()), survey);

        //1 CompositeScore -> 1 dataValue
        for(CompositeScore compositeScore:compositeScoreList){
            values.put(prepareValue(compositeScore));
        }

        //put in values the phonemetadata for be sent in the survey
        PhoneMetaData phoneMetaData= Session.getPhoneMetaData();
        //Activity is always null here, this is the reason for not use R.String_Phoneimei_uid..
            values.put(preparePhoneValue(TAG_IMEI, phoneMetaData.getImei()));
            //Check if the phonenumber is null, some SIMCards/Operators not give this field.
            if (phoneMetaData.getPhone_number() != null)
                values.put(preparePhoneValue(TAG_PHONE, phoneMetaData.getPhone_number()));
            values.put(preparePhoneValue(TAG_PHONE_SERIAL, phoneMetaData.getPhone_serial()));
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
    private JSONObject preparePhoneValue(String uid, String value) throws Exception{
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
     * Returns the URL that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    private String getDhisOrgUnitURL(String code){
        String url= PreferencesState.getInstance().getDhisURL();
        if(url==null || "".equals(url)){
            url=DHIS_DEFAULT_SERVER;
        }

        return url+String.format(DHIS_PULL_ORG_UNIT_API,code);
    }

    /**
     * Returns the ClosedDate that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    private String getClosingDateURL(String code){
        String url= PreferencesState.getInstance().getDhisURL();
        if(url==null || "".equals(url)){
            url=DHIS_DEFAULT_SERVER;
        }


        return url+String.format(DHIS_PULL_CLOSED_DATE,code);
    }

    /**
     * Returns the URL that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    private String getDhisOrgUnitsURL(){
        String url= PreferencesState.getInstance().getDhisURL();
        if(url==null || "".equals(url)){
            url=DHIS_DEFAULT_SERVER;
        }

        url=DHIS_DEFAULT_SERVER+DHIS_PULL_PROGRAM+ activity.getResources().getString(R.string.UID_PROGRAM)+DHIS_PULL_ORG_UNITS_API;
        return url;
    }
    /**
     * Returns the URL that points to the DHIS server API according to preferences.
     * @return
     */
    private String getDhisURL(){
        String url= PreferencesState.getInstance().getDhisURL();
        if(url==null || "".equals(url)) {
            url = DHIS_DEFAULT_SERVER;
        }
        return url+DHIS_PUSH_API;
    }
    /**
     * Pushes data to DHIS Server
     * @param data
     */
    private JSONObject pushData(JSONObject data)throws Exception {

        final String DHIS_URL=getDhisURL();

        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator=new BasicAuthenticator();
        client.setAuthenticator(basicAuthenticator);

        RequestBody body = RequestBody.create(JSON, data.toString());
        Request request = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                .url(DHIS_URL)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()){
            Log.e(TAG, "pushData (" + response.code()+"): "+response.body().string());
            throw new IOException(response.message());
        }
        return parseResponse(response.body().string());
    }

    /**
     * Call to DHIS Server
     * @param data
     * @param url
     */
    private Response executeCall(JSONObject data, String url, String method) throws IOException {
        final String DHIS_URL=url;

        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator=new BasicAuthenticator();
        client.setAuthenticator(basicAuthenticator);

        Request.Builder builder = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                .url(DHIS_URL);

        switch (method) {
            case "POST":
                RequestBody postBody = RequestBody.create(JSON, data.toString());
                builder.post(postBody);
                break;
            case "PUT":
                RequestBody putBody = RequestBody.create(JSON, data.toString());
                builder.put(putBody);
                break;
            case "PATCH":
                RequestBody patchBody = RequestBody.create(JSON, data.toString());
                builder.patch(patchBody);
                break;
            case "GET":
                builder.get();
                break;
        }

        Request request = builder.build();
        return client.newCall(request).execute();
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

    public boolean isOrganizationClosed() {
        try {
            Calendar closedData=getOrgUnitClosedDate(DHIS_DEFAULT_CODE);
            if(closedData==null){
                Log.d(TAG, "The organitation closeData is null.");
            }
            if(closedData!=null) {
                Calendar sysDate = Calendar.getInstance();
                sysDate.setTime(new Date());

                if (sysDate.after(closedData)) {
                    Log.d(TAG, "The organitation unit has been banned.");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "The organitation unit is open.");
        return false;
    }


    /**
     * Basic
     */
    class BasicAuthenticator implements  Authenticator{

        public final String AUTHORIZATION_HEADER="Authorization";
        private String credentials;

        BasicAuthenticator(){
            credentials = Credentials.basic(DHIS_USERNAME, DHIS_PASSWORD);
        }

        @Override
        public Request authenticate(Proxy proxy, Response response) throws IOException {
            return response.request().newBuilder().header(AUTHORIZATION_HEADER, credentials).build();
        }

        @Override
        public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
            return null;
        }

        public String getCredentials(){
            return credentials;
        }
    }

}
