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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.ShowException;
import org.hisp.dhis.android.sdk.controllers.wrappers.OrganisationUnitLevelWrapper;
import org.hisp.dhis.android.sdk.controllers.wrappers.ProgramWrapper;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Utility class that shows specific operations to check server status with the given config
 * Created by arrizabalaga on 28/01/16.
 */
public class ServerAPIController {


    private static final String TAG="ServerAPIController";

    /**
     * Tag for version data in json response
     */
    public static final String TAG_VERSION = "version";

    /**
     * Tag for id (program) in json response
     */
    private static final String TAG_ID = "id";

    /**
     * Tag for closedDate (orgUnit) in json response
     */
    private static final String TAG_CLOSEDDATE = "closedDate";

    /**
     * Tag for orgunit description in json request/response
     */
    private static final String TAG_DESCRIPTIONCLOSEDATE ="description";

    /**
     * Tag for organisationUnits in json response
     */
    private static final String TAG_ORGANISATIONUNITS = "organisationUnits";

    /**
     * Tag for code attribute in orgUnits (json)
     */
    private static final String TAG_CODE = "code";

    /**
     * Date format to the closedDate attribute
     */
    private static final String DATE_CLOSED_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Endpoint to retrieve server info (including version)
     */
    private static final String DHIS_SERVER_INFO="/api/system/info";

    /**
     * Endpoint to retrieve program info
     */
    private static final String DHIS_PULL_PROGRAM="/api/programs/";

    /**
     * Endpoint suffix to retrieve program info
     */
    private static final String DHIS_EXIST_PROGRAM=".json?fields=id";

    /**
     * Endpoint to retrieve orgUnits info filtering by CODE (API)
     */
    private static final String DHIS_PULL_ORG_UNIT_API ="/api/organisationUnits.json?paging=false&fields=id,closedDate&filter=code:eq:%s&filter:programs:id:eq:%s";

    /**
     * Endpoint to retrieve orgUnits info filtering by NAME (SDK)
     */
    private static final String DHIS_PULL_ORG_UNIT_API_BY_NAME ="/api/organisationUnits.json?paging=false&fields=id,closedDate&filter=name:eq:%s&filter:programs:id:eq:%s";

    /**
     * Endpoint suffix to retrieve orgUnits
     */
    private static final String DHIS_PULL_ORG_UNITS_API=".json?fields=organisationUnits[*]";
    /**
     * Endpoint to patch closeDate to an OrgUnit
     */
    private static final String DHIS_PATCH_URL_CLOSED_DATE ="/api/organisationUnits/%s/closedDate";

    /**
     * Endpoint to patch description to an OrgUnit
     */
    private static final String DHIS_PATCH_URL_DESCRIPTIONCLOSED_DATE="/api/organisationUnits/%s/description";

    /**
     * New Description to a closed OrgUnit
     */
    private static final String DHIS_PATCH_DESCRIPTIONCLOSED_DATE ="[%s] - Android Surveillance App set the closing date to %s because over 30 surveys were pushed within 1 hour.";

    /**
     * MediaType always json + utf8
     */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * Max surveys that can be sent ...
     */
    private static int DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR=30;

    /**
     * ... In an hour
     */
    private static int DHIS_LIMIT_HOURS=1;

    /**
     * Current program UID (once is calculated never changes)
     */
    private static String programUID;

    private static org.hisp.dhis.android.sdk.network.Credentials sdkCredentials;

    /**
     * Returns current serverUrl
     * @return
     */
    public static String getServerUrl(){
        return PreferencesState.getInstance().getDhisURL();
    }

    /**
     * Returns current orgUnit
     * @return
     */
    public static String getOrgUnit(){
        return PreferencesState.getInstance().getOrgUnit();
    }

    /**
     * Returns the UID of the pictureapp program (from db)
     * @return
     */
    public static String getProgramUID(){
        if(programUID==null){
            programUID = Program.getFirstProgram().getUid();
        }
        return programUID;
    }


    /**
     * Returns hardcoded credentials for its use in sdk
     * @return
     */
    public static org.hisp.dhis.android.sdk.network.Credentials getSDKCredentials(){
        if(sdkCredentials==null){
            sdkCredentials=new org.hisp.dhis.android.sdk.network.Credentials(getUserPush(),getPassPush());
        }
        return sdkCredentials;
    }


    /**
     * Returns the version of the default server
     * Null if something went wrong
     * @return
     */
    public static String getServerVersion(){
        return getServerVersion(PreferencesState.getInstance().getDhisURL());
    }

    /**
     * Returns the version of the given server.
     * Null if something went wrong
     * @param url
     * @return
     */
    public static String getServerVersion(String url){
        String serverVersion;
        try {
            String urlServerInfo = url+DHIS_SERVER_INFO;
            Response response=executeCall(null, urlServerInfo, "GET");

            //Error -> null
            if(!response.isSuccessful()){
                Log.e(TAG, "getServerVersion (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
            JSONObject data=parseResponse(response.body().string());
            serverVersion= data.getString(TAG_VERSION);
        } catch (Exception ex) {
            Log.e(TAG,"getServerVersion: "+ex.toString());
            serverVersion="";
        }
        Log.i(TAG, String.format("getServerVersion(%s) -> %s", url, serverVersion));
        return serverVersion;
    }

    /**
     * Checks if the given url corresponds to a 2.20 server (uses API for some ops)
     * @return
     */
    public static boolean isAPIServer(){
        return isAPIServer(getServerUrl());
    }

    /**
     * Checks if the given url corresponds to a 2.20 server (uses API for some ops)
     * @param url
     * @return
     */
    public static boolean isAPIServer(String url){
        String serverVersion=getServerVersion(url);
        return isAPIVersion(serverVersion);
    }

    /**
     * Checks if the given version corresponds to 2.20
     * @param serverVersion
     * @return
     */
    public static boolean isAPIVersion(String serverVersion){
        return Constants.DHIS_API_SERVER.equals(serverVersion);
    }

    /**
     * Returns true|false depending of the network connectivity.
     * @return
     */
    public static boolean isNetworkAvailable(){
        Context ctx = PreferencesState.getInstance().getContext();
        ConnectivityManager cm =
                (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork==null){
            return false;
        }
        return activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Checks if data can be pushed into the server
     * @return
     */
    public static boolean isReadyForPush(){
        String serverUrl=getServerUrl();
        String orgUnit=getOrgUnit();
        return isReadyForPush(serverUrl, orgUnit);
    }

    /**
     * Checks if data can be pushed into the server
     * @param url
     * @param orgUnitCodeOrName
     * @return
     */
    public static boolean isReadyForPush(String url,String orgUnitCodeOrName){
        if(!isNetworkAvailable()){
            Log.w(TAG,String.format("isReadyForPush(%s,%s) -> Network not available",url,orgUnitCodeOrName));
            return false;
        }

        if(!isValidProgram(url)){
            Log.w(TAG,String.format("isReadyForPush(%s,%s) -> Program not found in server",url,orgUnitCodeOrName));
            return false;
        }

        if(orgUnitCodeOrName == null || orgUnitCodeOrName.equals("") || !isValidOrgUnit(url, orgUnitCodeOrName)){
            Log.w(TAG,String.format("isReadyForPush(%s,%s) -> OrgUnit not found in server",url,orgUnitCodeOrName));
            return false;
        }

        if(!isOrgUnitOpen(url,orgUnitCodeOrName)){
            Log.w(TAG,String.format("isOrgUnitOpen(%s,%s) -> OrgUnit closed, push is not enabled",url,orgUnitCodeOrName));
            return false;
        }

        return true;
    }

    /**
     * Returns if the given url contains the current program
     * @param url
     * @return
     */
    public static boolean isValidProgram(String url){
        Log.d(TAG, String.format("isValidProgram(%s) ...", url));
        String programUIDInServer;
        try {
            String urlValidProgram = getIsValidProgramUrl(url);
            Response response=executeCall(null, urlValidProgram, "GET");

            //Error -> null
            if(!response.isSuccessful()){
                Log.e(TAG, "isValidProgram (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }

            JSONObject data=parseResponse(response.body().string());
            programUIDInServer= String.valueOf(data.get(TAG_ID));
        } catch (Exception ex) {
            Log.e(TAG,"isValidProgram: "+ex.toString());
            return false;
        }
        boolean valid=getProgramUID()!=null && getProgramUID().equals(programUIDInServer);
        Log.d(TAG, String.format("isValidProgram(%s) -> %b (Thread: %d)", url, valid, Thread.currentThread().getId()));
        return valid;
    }

    /**
     * Checks if the given orgUnit is present in the server.
     * XXX The endpoint changes from using code|name field in API|SDK servers.
     * @param url
     * @param orgUnitNameOrCode OrgUnit code if server is 2.20, OrgUnit name if server is 2.21,2.22
     * @return true|false
     */
    public static boolean isValidOrgUnit(String url,String orgUnitNameOrCode){
        JSONObject orgUnitJSON=getOrgUnitData(url, orgUnitNameOrCode);
        return orgUnitJSON!=null;
    }

    /**
     * Checks if the given orgUnit is open in the server.
     * @param url
     * @param orgUnitNameOrCode OrgUnit code if server is 2.20, OrgUnit name if server is 2.21,2.22
     * @return true|false
     */
    public static boolean isOrgUnitOpen(String url,String orgUnitNameOrCode){
        JSONObject orgUnitJSON=getOrgUnitData(url, orgUnitNameOrCode);
        if(orgUnitJSON==null){
            return false;
        }

        return !isBanned(orgUnitJSON);
    }

    /**
     * Closes server if too many surveys have been pushed
     */
    public static void banOrgUnitIfRequired(){
        banOrgUnitIfRequired(getServerUrl(),getOrgUnit());
    }

    /**
     * Closes server if too many surveys have been pushed
     */
    public static void banOrgUnitIfRequired(String url,String orgUnitNameOrCode){
        List<Survey> sentSurveys = Survey.getAllHideAndSentSurveys();
        if(isSurveyOverLimit(sentSurveys)){
            banOrg(url,orgUnitNameOrCode);
        }
    }

    /**
     * Returns the orgUnit UID for the current server + orgunit
     * @return
     */
    public static String getOrgUnitUID(){
        String serverUrl=getServerUrl();
        String orgUnit=getOrgUnit();
        return getOrgUnitUID(serverUrl,orgUnit);
    }

    /**
     * Returns the orgUnit UID for the given url and orgUnit (code or name)
     * @param url
     * @param orgUnitNameOrCode
     * @return
     */
    public static String getOrgUnitUID(String url,String orgUnitNameOrCode){
        JSONObject orgUnitJSON=getOrgUnitData(url, orgUnitNameOrCode);
        if(orgUnitJSON==null){
            return null;
        }
        try{
            return orgUnitJSON.getString(TAG_ID);
        }catch (JSONException ex){
            return null;
        }
    }

    /**
     * Bans the orgUnit for future pushes (too many too quick)
     * @param url
     * @param orgUnitNameOrCode
     */
    public static void banOrg(String url,String orgUnitNameOrCode) {
        Log.i(TAG,String.format("banOrg(%s,%s)",url,orgUnitNameOrCode));
        try {
            JSONObject orgUnitJSON = getOrgUnitData(url, orgUnitNameOrCode);
            String orgUnitUID =orgUnitJSON.getString(TAG_ID);
            String orgUnitDescription = orgUnitJSON.getString(TAG_DESCRIPTIONCLOSEDATE);

            //NO OrgUnitUID -> Non blocking error, go on
            if(orgUnitUID==null){
                Log.e(TAG,String.format("banOrg(%s,%s) -> No UID",url,orgUnitNameOrCode));
                return;
            }

            //Update date and descripcion in the orgunit
            patchClosedDate(url, orgUnitUID);
            patchDescriptionClosedDate(url, orgUnitUID, orgUnitDescription);
        }catch(Exception ex){
            Log.e(TAG,String.format("banOrg(%s,%s): %s",url,orgUnitNameOrCode,ex.getMessage()));
        }
    }


    /**
     * This method returns a String[] whit the Organitation codes
     * @throws Exception
     */
    public static String[] pullOrgUnitsCodes(String url){

        try{
            String orgUnitsURL  = getDhisOrgUnitsURL(url);
            Response response=executeCall(null, orgUnitsURL, "GET");

            //Error -> null
            if(!response.isSuccessful()){
                Log.e(TAG, "pullOrgUnitsCodes (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }

            //{"organisationUnits":[{}]}
            JSONArray orgUnitsArray =parseResponse(response.body().string()).getJSONArray(TAG_ORGANISATIONUNITS);

            //0 matches -> Error
            if (orgUnitsArray.length()==0){
                throw new Exception("Found 0 matches");
            }
            return Utils.jsonArrayToStringArray(orgUnitsArray, TAG_CODE);

        }catch(Exception ex){
            Log.e(TAG,String.format("pullOrgUnitsCodes(%url): %s",url,ex.getMessage()));
            String[] value = new String[1];
            value[0] = "";
            return value;
        }

    }

    /**
     * This method returns a String[] whit the Organitation codes
     * @throws Exception
     */
    public static String[] pullOrgUnitsNotBannedCodes(String url){

        try{
            String orgUnitsURL  = getDhisOrgUnitsURL(url);
            Response response=executeCall(null, orgUnitsURL, "GET");

            //Error -> null
            if(!response.isSuccessful()){
                Log.e(TAG, "pullOrgUnitsCodes (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }

            //{"organisationUnits":[{}]}
            JSONArray orgUnitsArray = parseResponse(response.body().string()).getJSONArray(TAG_ORGANISATIONUNITS);
            //fixme loop removing the orgunits banned (orgUnitsArray)
            //mehtod isBanned()
            //0 matches -> Error
            if (orgUnitsArray.length()==0){
                throw new Exception("Found 0 matches");
            }
            return Utils.jsonArrayToStringArray(orgUnitsArray, TAG_ID);

        }catch(Exception ex){
            Log.e(TAG,String.format("pullOrgUnitsCodes(%url): %s",url,ex.getMessage()));
            String[] value = new String[1];
            value[0] = "";
            return value;
        }

    }
    /**
     * compares the dates of the surveys and checks if the dates are over the limit
     * @param surveyList all the sent surveys
     * @return true if the surveys are over the limit
     */
    static boolean isSurveyOverLimit(List<Survey> surveyList){
        if(surveyList.size()>=DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR) {
            for (int i = 0; i < surveyList.size(); i++) {
                int countDates = 0;
                Calendar actualSurvey=Utils.DateToCalendar(surveyList.get(i).getEventDate());
                for (int d = 0; d < surveyList.size(); d++) {
                    Calendar nextSurvey=Utils.DateToCalendar(surveyList.get(d).getEventDate());
                    if (actualSurvey.before(nextSurvey)) {
                        if (!Utils.isDateOverLimit(actualSurvey, nextSurvey, DHIS_LIMIT_HOURS)) {
                            countDates++;
                            Log.d(TAG, "Surveys sents in one hour:" + countDates);
                            if (countDates >= DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Updates the orgUnit adding a closedDate
     * @param url
     * @param orgUnitUID
     */
    static void patchClosedDate(String url,String orgUnitUID){
        //https://malariacare.psi.org/api/organisationUnits/u5jlxuod8xQ/closedDate
        try {
            String urlPathClosedDate = getPatchClosedDateUrl(url, orgUnitUID);
            JSONObject data =prepareTodayDateValue();
            Response response=executeCall(data, urlPathClosedDate, "PATCH");
            if(!response.isSuccessful()){
                Log.e(TAG, "closingDatePatch (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
        } catch (Exception e) {
            Log.e(TAG,String.format("patchClosedDate(%s,%s): %s",url,orgUnitUID,e.getMessage()));
        }
    }

    /**
     * Prepare the closing value.
     * @return Closing value as Json.
     */
    static JSONObject prepareTodayDateValue() throws Exception {
        String dateFormatted=Utils.geTodayDataString(DATE_CLOSED_DATE_FORMAT);
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_CLOSEDDATE, dateFormatted);
        return elementObject;
    }

    static void patchDescriptionClosedDate(String url,String orgUnitUID,String orgUnitDescription){
        //https://malariacare.psi.org/api/organisationUnits/u5jlxuod8xQ/closedDate
        try {
            String urlPathClosedDescription = getPatchClosedDescriptionUrl(url, orgUnitUID);
            JSONObject data =prepareClosingDescriptionValue(orgUnitDescription);
            Response response=executeCall(data, urlPathClosedDescription, "PATCH");
            if(!response.isSuccessful()){
                Log.e(TAG, "patchDescriptionClosedDate (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
        } catch (Exception e) {
            Log.e(TAG,String.format("patchDescriptionClosedDate(%s,%s): %s",url,orgUnitUID,e.getMessage()));
        }
    }

    /**
     * Pull the current description and adds new closed organization description.
     * @url url for pull the current description
     * @return new description.
     */
    static JSONObject prepareClosingDescriptionValue(String orgUnitDescription) throws Exception{

        //New line to description
        String dateFormatted=Utils.getClosingDateString("dd-MM-yyyy");
        String dateTimestamp=Utils.getClosingDateTimestamp(Utils.getClosingDateString("dd-MM-yyyy")).getTime()+"";
        String description=String.format(DHIS_PATCH_DESCRIPTIONCLOSED_DATE,dateTimestamp, dateFormatted);

        //Previous + New line
        StringBuilder sb = new StringBuilder();
        sb.append(orgUnitDescription);
        sb.append("");//next line
        sb.append("");//next line
        sb.append(description);
        description=sb.toString();
        sb=null;

        //As a JSON
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DESCRIPTIONCLOSEDATE, description);
        return elementObject;
    }

    /**
     * Returns the orgunit data from the given server according to its current version
     * @param url
     * @param orgUnitNameOrCode
     * @return
     */
    static JSONObject getOrgUnitData(String url,String orgUnitNameOrCode){
        //Version is required to choose which field to match
        String serverVersion = getServerVersion(url);

        //No version -> No data
        if(serverVersion==null){
            return null;
        }

        try {
            String urlOrgUnitData  = getOrgUnitDataUrl(url, serverVersion, orgUnitNameOrCode);
            Response response=executeCall(null, urlOrgUnitData, "GET");

            //Error -> null
            if(!response.isSuccessful()){
                Log.e(TAG, "getOrgUnitData (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }

            //{"organisationUnits":[{}]}
            JSONObject jsonResponse=parseResponse(response.body().string());
            JSONArray orgUnitsArray = (JSONArray) jsonResponse.get(TAG_ORGANISATIONUNITS);

            //0| >1 matches -> Error
            if (orgUnitsArray.length()==0 || orgUnitsArray.length() > 1){
                Log.e(TAG,String.format("getOrgUnitData(%s,%s) -> Found %d matches",url,orgUnitNameOrCode,orgUnitsArray.length()));
                return null;
            }
            return (JSONObject)orgUnitsArray.get(0);

        } catch (Exception ex) {
            Log.e(TAG,String.format("getOrgUnitData(%s,%s): %s",url,orgUnitNameOrCode,ex.toString()));
            return null;
        }

    }

    /**
     * Checks if the orgunit is closed (due to too much surveys being pushed)
     * @param orgUnitJSON
     * @return
     */
    static boolean isBanned(JSONObject orgUnitJSON){
        if(orgUnitJSON==null){
            return true;
        }
        Log.d(TAG,String.format("isBanned(%s)",orgUnitJSON.toString()));
        try{
            String closedDateAsString = getClosedDate(orgUnitJSON);
            //No closedDate -> Open
            if(closedDateAsString==null || closedDateAsString.isEmpty()){
                return false;
            }

            //CloseDate -> Check dates
            Calendar calendarClosedDate = Utils.parseStringToCalendar(closedDateAsString);

            //ClosedDate bad format -> Closed
            if(calendarClosedDate==null){
                return true;
            }

            //If closeddate>today -> Closed
            return !Utils.isDateOverSystemDate(calendarClosedDate);

        }catch(Exception ex){
            Log.e(TAG,String.format("isBanned(%s) ->%s",orgUnitJSON.toString(),ex.getMessage()));
            return true;
        }
    }

    /**
     * Returns the closedDate from the given orgUnit (json format) or null if it is not present
     * (which is fine too)
     * @param orgUnitJSON
     * @return
     */
    static String getClosedDate(JSONObject orgUnitJSON){
        try{
            return orgUnitJSON.getString(TAG_CLOSEDDATE);
        }catch (Exception ex){
            return null;
        }
    }

    /**
     *
     * This method returns the valid url for check the program
     * @return url for ask if the program uid exist with the UID_PROGRAM value.
     */
    static String getIsValidProgramUrl(String url) {
        String endpoint = url +DHIS_PULL_PROGRAM+getProgramUID()+DHIS_EXIST_PROGRAM;
        endpoint = encodeBlanks(endpoint);
        Log.d(TAG,String.format("getIsValidProgramUrl(%s)->%s",url,endpoint));
        return endpoint;
    }

    /**
     * Returns the right endpoint depending on the server version
     * @param url
     * @param serverVersion
     * @param orgUnitNameOrCode
     * @return
     */
    static String getOrgUnitDataUrl(String url,String serverVersion,String orgUnitNameOrCode){
        String endpoint=url;
        String programUID = getProgramUID();
        if(Constants.DHIS_API_SERVER.equals(serverVersion)){
            endpoint+=String.format(DHIS_PULL_ORG_UNIT_API,orgUnitNameOrCode,programUID);
        }else{
            endpoint+=String.format(DHIS_PULL_ORG_UNIT_API_BY_NAME,orgUnitNameOrCode,programUID);
        }

        endpoint = encodeBlanks(endpoint);
        Log.d(TAG, String.format("getOrgUnitDataUrl(%s,%s,%s) -> %s", url, serverVersion, orgUnitNameOrCode, endpoint));
        return endpoint;
    }

    /**
     * Returns the ClosedDate that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    static String getPatchClosedDateUrl(String url, String orguid){
        //Get the org_ID
        String endpoint=url+String.format(DHIS_PATCH_URL_CLOSED_DATE,orguid);
        return encodeBlanks(endpoint);
    }

    /**
     * Returns the Description of orgUnit that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    static String getPatchClosedDescriptionUrl(String url, String orguid){
        String endpoint=url+String.format(DHIS_PATCH_URL_DESCRIPTIONCLOSED_DATE,orguid);
        return encodeBlanks(endpoint);
    }

    /**
     * Returns the URL that points to the DHIS server (Pull) API according to preferences.
     * @return
     */
    static String getDhisOrgUnitsURL(String url){
        String endpoint= url +DHIS_PULL_PROGRAM+getProgramUID()+DHIS_PULL_ORG_UNITS_API;
        return encodeBlanks(endpoint);
    }

    static String encodeBlanks(String endpoint){
        return endpoint.replace(" ","%20");
    }

    /**
     * Call to DHIS Server
     * @param data
     * @param url
     */
    static Response executeCall(JSONObject data, String url, String method) throws IOException {
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

    /**
     * Turns a string response into a handy JSONObject.
     * Returns null if its possible
     * @param responseData
     * @return
     * @throws Exception
     */
    static JSONObject parseResponse(String responseData){
        try{
            JSONObject jsonResponse=new JSONObject(responseData);
            Log.d(TAG, "parseResponse: " + jsonResponse);
            return jsonResponse;
        }catch(Exception ex){
            return null;
        }
    }


    @NonNull
    static String getUserPush() {
        return PreferencesState.getInstance().getContext().getResources().getString(R.string.user_push);
    }

    @NonNull
    static String getPassPush() {
        return PreferencesState.getInstance().getContext().getResources().getString(R.string.pass_push);
    }

}

/**
 * Basic authenticator required for calls
 */
class BasicAuthenticator implements Authenticator {

    public final String AUTHORIZATION_HEADER="Authorization";
    private String credentials;

    BasicAuthenticator(){

        credentials = Credentials.basic(ServerAPIController.getUserPush(), ServerAPIController.getPassPush());
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
