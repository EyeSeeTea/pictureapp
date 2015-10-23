/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Survelliance App.
 *
 *  QIS Survelliance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Survelliance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Survelliance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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
import org.eyeseetea.malariacare.views.ShowException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jose on 20/06/2015.
 */
public class PushClient {



    private static String TAG=".PushClient";

    //This change for a sharedpreferences url that is selected from the settings screen

    private static String DHIS_SERVER ="https://malariacare.psi.org";
    private static String DHIS_PUSH_API="/api/events";
    private static String DHIS_UID_PROGRAM="";
    private static String DHIS_PULL_ORG_UNIT_API ="/api/organisationUnits.json?paging=false&fields=id,closedDate&filter=code:eq:%s&filter:programs:id:eq:%s";
    private static String DHIS_PULL_PROGRAM="/api/programs/";
    private static String DHIS_PULL_ORG_UNITS_API=".json?fields=organisationUnits";
    private static String DHIS_USERNAME="testing";
    private static String DHIS_PASSWORD="Testing2015";
    public static String DHIS_ORG_NAME ="KH_Cambodia";
    private static String DHIS_ORG_UID ="";
    private static String DHIS_PATCH_URL_CLOSED_DATE ="/api/organisationUnits/%s/closedDate";
    private static String DHIS_PATCH_URL_DESCRIPTIONCLOSED_DATE="/api/organisationUnits/%s/description";
    private static String DHIS_PATCH_DESCRIPTIONCLOSED_DATE ="Android Surveillance App set the closing date to %s because over 30 surveys were pushed within 1 hour.";



    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static Boolean BANNED=false;
    public static String DHIS_UNEXISTENT_ORG_UNIT="";

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
    private static String TAG_CLOSEDATA="closedDate";
    private static String TAG_DESCRIPTIONCLOSEDATA="description";
    private static String TAG_ORGANISATIONUNIT="organisationUnits";


    private static String TAG_PHONEMETADA="RuNZUhiAmlv";

    private static int DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR=30;
    private static int DHIS_LIMIT_HOURS=1;

    Survey survey;
    Activity activity;
    Context applicationContext;


    public PushClient(Activity activity) {
        this.activity = activity;
        this.applicationContext=activity.getApplicationContext();
        getPreferenceValues(applicationContext);
        DHIS_UID_PROGRAM=applicationContext.getResources().getString(R.string.UID_PROGRAM);
    }

    public PushClient(Context applicationContext) {
        this.applicationContext = applicationContext;
        getPreferenceValues(applicationContext);
        DHIS_UID_PROGRAM=applicationContext.getResources().getString(R.string.UID_PROGRAM);
    }
    public PushClient(Survey survey, Activity activity) {
        this.survey = survey;
        this.activity = activity;
        this.applicationContext=activity.getApplicationContext();
        getPreferenceValues(applicationContext);
        DHIS_UID_PROGRAM=applicationContext.getResources().getString(R.string.UID_PROGRAM);
    }

    public PushClient(Survey survey, Context applicationContext) {
        this.survey = survey;
        this.applicationContext = applicationContext;
        getPreferenceValues(applicationContext);
        DHIS_UID_PROGRAM=applicationContext.getResources().getString(R.string.UID_PROGRAM);
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
            Log.e(TAG, ex.getMessage());
            return new PushResult(ex);
        }
    }

    public PushResult pushBackground() {
        //Check if the static DHIS_UNEXISTENT_ORG_UNIT is the same than the used DHIS_ORG_NAME.
        //If DHIS_UNEXISTENT_ORG_UNIT!=DHIS_ORG_NAME is the same, the UID not exist, and it was be checked.
        //hasOrgUnitValidCode check the code the program and the closedDate
        //This if is evaluating every push from SurveyService.
        if (isValid() && checkAll() && !BANNED  ) {
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
                    if(isSurveyOverLimit(survey,sentSurveys))
                    {
                        banOrg(DHIS_ORG_NAME);
                    }
                }
                return result;
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                return new PushResult(ex);
            }
        }
        return new PushResult();
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
    /**
     * compares the dates of the surveys and checks if the dates are over the limit
     * @param surveyInit the survey to check.
     * @param surveyList all the sent surveys
     * @return true if the surveys are over the limit
     */
    private boolean isSurveyOverLimit(Survey surveyInit, List<Survey> surveyList){
        int countDates=0;
        for (int i = 0; i < surveyList.size(); i++) {
                if (!Utils.isDateOverLimit(Utils.DateToCalendar(surveyInit.getEventDate()), Utils.DateToCalendar(surveyList.get(i).getEventDate()), DHIS_LIMIT_HOURS)) {
                    countDates++;
                    Log.d(TAG, "Surveys sents in one hour:" + countDates);
                    if(countDates>=DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR){
                        return true;
                    }
                }
        }
        return false;
    }

    public static boolean isValid() {
        boolean result=((!(DHIS_UNEXISTENT_ORG_UNIT.equals(DHIS_ORG_NAME)))&& !BANNED);
        return result;
    }

    /**
     * Get the user settings values from the shared Preferences
     */
    public void getPreferenceValues(Context context){
        SharedPreferences preferences = applicationContext.getSharedPreferences("org.eyeseetea.pictureapp_preferences", applicationContext.MODE_PRIVATE);
        String key=applicationContext.getResources().getString(R.string.org_unit);
        String value=preferences.getString(key, DHIS_ORG_NAME);
        DHIS_ORG_NAME =value;
        key=applicationContext.getResources().getString(R.string.dhis_url);
        value= preferences.getString(key, DHIS_SERVER);
        DHIS_SERVER =value;
        PreferencesState.getInstance().reloadPreferences();
    }


    /**
     * Bans the organization for future requests , reducing one day the system date and keeping closedate on the server.
     */

    private void banOrg(String orgName) {
        String url= PreferencesState.getInstance().getDhisURL();
        if(url==null || "".equals(url)){
            url= DHIS_SERVER;
        }
        String orgid = null;
        try {
            orgid = DHIS_ORG_UID;
            patchClosedDate(getPatchClosedDateUrl(url, orgid));
            patchDescriptionClosedDate(getPatchClosedDescriptionUrl(url, orgid));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Patch the closedDate data in the server
    private void patchClosedDate(String url){
        //https://malariacare.psi.org/api/organisationUnits/u5jlxuod8xQ/closedDate
        try {
            String DHIS_PATCH_URL=url;
            JSONObject data =prepareTodayDateValue();
            Response response=executeCall(data, DHIS_PATCH_URL, "PATCH");
            Log.e(TAG, "closingDatePatch (" + response.code() + "): " + response.body().string());
            if(!response.isSuccessful()){
                Log.e(TAG, "closingDatePatch (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Patch in the server the new description in a closed organisation
     * @param url the patch url
     */

    private void patchDescriptionClosedDate(String url) throws Exception{
        //https://malariacare.psi.org/api/organisationUnits/Pg91OgEIKIm/description
        try {
            String DHIS_PATCH_URL=url;
            JSONObject data =prepareClosingDescriptionValue(url);

            Response response=executeCall(data, DHIS_PATCH_URL, "PATCH");
            if(!response.isSuccessful()){
                Log.e(TAG, "closingDateDescriptionPatch (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Pull the current description and adds new closed organization description.
     * @url url for pull the current description
     * @return new description.
     */
    private JSONObject prepareClosingDescriptionValue(String url) throws Exception{
        String actualDescription= getCurrentDescription(url);
        String dateFormatted=Utils.getClosingDateString("dd-MM-yyyy");
        String description=String.format(DHIS_PATCH_DESCRIPTIONCLOSED_DATE, dateFormatted);
        StringBuilder sb = new StringBuilder();
        sb.append(actualDescription);
        sb.append("");//next line
        sb.append("");//next line
        sb.append(description);
        description=sb.toString();
        sb=null;
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DESCRIPTIONCLOSEDATA, description);
        Log.d(TAG, "closingDateURL:Description:" + description);
        return elementObject;
    }

    /**
     * Prepare the closing value.
     * @return Closing value as Json.
     */
    private JSONObject prepareClosingDateValue() throws Exception{
        String dateFormatted=Utils.getClosingDateString("yyyy-MM-dd");
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_CLOSEDATA, dateFormatted);
        Log.d("closingDateURL", "closingDateURL:EndDate:" + dateFormatted);
        return elementObject;
    }

    /**
     * Prepare the closing value.
     * @return Closing value as Json.
     */
    private JSONObject prepareTodayDateValue() throws Exception{
        String dateFormatted=Utils.geTodayDataString("yyyy-MM-dd");
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_CLOSEDATA, dateFormatted);
        Log.d("closingDateURL", "closingDateURL:EndDate:" + dateFormatted);
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
        Log.d(TAG, "prepareMetadata for survey: " + survey.getId_survey());

        JSONObject object=new JSONObject();
        object.put(TAG_PROGRAM, survey.getProgram().getUid());
        object.put(TAG_ORG_UNIT, DHIS_ORG_UID);
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
     * This method check the org_unit is valid and if the org unit is banned.
     * @return return true if all is correct.
     */
    private boolean checkAll(){
        try {
            DHIS_ORG_UID= getUIDCheckProgramClosedDate(DHIS_ORG_NAME);
            Log.d("ORGUNITNULL", DHIS_ORG_UID);
            if(!DHIS_ORG_UID.equals("null")){
                return true;
            }
            else{
                DHIS_UNEXISTENT_ORG_UNIT = DHIS_ORG_NAME;
                try {
                    throw new ShowException(applicationContext.getString(R.string.exception_org_unit_not_valid), applicationContext);
                } catch (ShowException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method resets the unit checks for invalid and baned orgization
     * @orgName is the DHIS_ORG_NAME
     */
    public static void setUnbanAndNewOrgName(){
        BANNED=false;
        DHIS_UNEXISTENT_ORG_UNIT="";
    }

    //

    /**
     * @code is the DHIS_ORG_NAME
     * @return If org_unit not valid or have no UID Returns null, else the UID
     */
    private String getUIDCheckProgramClosedDate(String code) throws Exception{
        //https://malariacare.psi.org/api/organisationUnits.json?paging=false&fields=id,name,openingDate,closedDate,programs&filter=code:eq:KH_Cambodia
        String DHIS_PULL_URL=getDhisOrgUnitURL(code);
        JSONArray responseArray=null;
        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator =new BasicAuthenticator();
        client.setAuthenticator(basicAuthenticator);
        Log.d("URL",DHIS_PULL_URL);
        Response response=null;
            Request request= new Request.Builder()
                    .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                    .url(DHIS_PULL_URL)
                    .build();
            try {
                response = client.newCall(request).execute();
            }
            catch(Exception e){
                e.printStackTrace();
                return "null";
            }

        try {
        if(!response.isSuccessful()){
            Log.e(TAG, "pullOrgUnitUID (" + response.code()+"): "+response.body().string());
            throw new IOException(response.message());
        }

        JSONObject responseJSON=parseResponse(response.body().string());
            responseArray = (JSONArray) responseJSON.get(TAG_ORGANISATIONUNIT);

            if (responseArray.length() == 0) {
                Log.e(TAG, "pullOrgUnitUID: No UID for code " + code);
                //Assign the used org_unit to the unexistent_org_unit for not make new pulls.
                // throw new IOException(activity.getString(R.string.dialog_error_push_no_uid)+" "+code);
                return "null";
            }
            try {
                String date = responseArray.getJSONObject(0).getString(TAG_CLOSEDATA);
                Calendar calendarDate = Utils.parseStringToCalendar(date);

                if(!Utils.isDateOverSystemDate(calendarDate)){
                    if(BANNED==false) {
                        BANNED = true;
                        try {
                            throw new ShowException(applicationContext.getString(R.string.exception_org_unit_banned), applicationContext);
                        } catch (ShowException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }catch(Exception e){
                //if the date is null is not need check
            }
        }catch(Exception e){
            return "null";
        }
        //Return the org_unit id
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

        BasicAuthenticator basicAuthenticator =new BasicAuthenticator();
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

        JSONObject responseJSON = parseResponse(response.body().string());
        JSONArray responseArray=(JSONArray) responseJSON.get(TAG_ORGANISATIONUNIT);
        if(responseArray.length()==0){
            Log.e(TAG, "pullOrgUnitUID: No org_unit ");
            throw new IOException(activity.getString(R.string.dialog_error_push_no_uid));
        }
        return Utils.jsonArrayToStringArray(responseArray, "code");
    }


    /**
     * Get the current description for a org_unit from the server
     * @return return the description or "".
     */
    public String getCurrentDescription(String url)  throws Exception{
        //https://malariacare.psi.org/api/organisationUnits/Pg91OgEIKIm/description
        String DHIS_PULL_URL=url;
        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator =new BasicAuthenticator();
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
        String description="";
        JSONObject responseObject=new JSONObject(jsonData);
        if(responseObject.length()==0){
            Log.e(TAG, "closingDateURL: No UID for code " + DHIS_ORG_NAME);
//            throw new IOException(activity.getString(R.string.dialog_error_push_no_uid)+" "+code);
            return description;
        }
        Log.d(TAG, "data description:" + responseObject.getString("description"));
        try {
            description =responseObject.getString("description");
        }
        catch(Exception e){
            description="";
            return description;
        }
        return description;
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

        PhoneMetaData phoneMetaData= Session.getPhoneMetaData();
        values.put(preparePhoneValue(TAG_PHONEMETADA, phoneMetaData.getPhone_metaData()));
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
        getPreferenceValues(applicationContext);
        if(url==null || "".equals(url)){
            url= DHIS_SERVER;
        }
        Log.d("uid", DHIS_UID_PROGRAM);
        url=url+String.format(DHIS_PULL_ORG_UNIT_API,code,DHIS_UID_PROGRAM);
        return url.replace(" ","%20");
    }

    /**
     * Returns the ClosedDate that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    private String getPatchClosedDateUrl(String url, String orguid){
        //Get the org_ID
        url=url+String.format(DHIS_PATCH_URL_CLOSED_DATE,orguid);
        return url.replace(" ","%20");
    }

    /**
     * Returns the Description of orgUnit that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    private String getPatchClosedDescriptionUrl(String url, String orguid){
        url=url+String.format(DHIS_PATCH_URL_DESCRIPTIONCLOSED_DATE,orguid);
        return url.replace(" ","%20");
    }

    /**
     * Returns the URL that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    private String getDhisOrgUnitsURL(){
        String url= PreferencesState.getInstance().getDhisURL();
        if(url==null || "".equals(url)){
            url= DHIS_SERVER;
        }

        url= DHIS_SERVER +DHIS_PULL_PROGRAM+ applicationContext.getResources().getString(R.string.UID_PROGRAM)+DHIS_PULL_ORG_UNITS_API;

        return url.replace(" ","%20");
    }
    /**
     * Returns the URL that points to the DHIS server API according to preferences.
     * @return
     */
    private String getDhisURL(){
        String url= PreferencesState.getInstance().getDhisURL();
        if(url==null || "".equals(url)) {
            url = DHIS_SERVER;
        }
        url= url+DHIS_PUSH_API;
        return url.replace(" ","%20");
    }

    /**
     * Call to DHIS Server
     * @param data
     * @param url
     */
    private Response executeCall(JSONObject data, String url, String method) throws IOException {
        final String DHIS_URL=url;

        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        BasicAuthenticator basicAuthenticator =new BasicAuthenticator();
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

    /**
     * Only checks the orgUnit
     * @param orgUnit the organization unit
     * @return true if is correct.
     */
    public boolean checkOrgUnit(String orgUnit) {
        boolean result=false;
        try {
            if(!(getUIDCheckProgramClosedDate(orgUnit).equals("null")))
                result=true;
        } catch (Exception e) {
            result=false;
            e.printStackTrace();
        }

        return result;
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
