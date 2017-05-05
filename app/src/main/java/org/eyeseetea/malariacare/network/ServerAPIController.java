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
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApiStrategy;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Utility class that shows specific operations to check server status with the given config
 * Created by arrizabalaga on 28/01/16.
 */
public class ServerAPIController {


    /**
     * Tag for version data in json response
     */
    public static final String TAG_VERSION = "version";
    private static final String TAG = "ServerAPIController";
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
    private static final String TAG_DESCRIPTIONCLOSEDATE = "description";

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
    private static final String DHIS_SERVER_INFO = "/api/system/info";

    /**
     * Endpoint to retrieve program info
     */
    private static final String DHIS_PULL_PROGRAM = "/api/programs/";

    /**
     * Endpoint suffix to retrieve program info
     */
    private static final String DHIS_EXIST_PROGRAM = ".json?fields=id";

    /**
     * Endpoint to retrieve orgUnits info filtering by CODE (API)
     */
    private static final String DHIS_PULL_ORG_UNIT_API =
            "/api/organisationUnits.json?paging=false&fields=id,closedDate,"
                    + "description&filter=code:eq:%s&filter:programs:id:eq:%s";

    /**
     * Endpoint to retrieve orgUnits info filtering by NAME (SDK)
     */
    private static final String DHIS_PULL_ORG_UNIT_API_BY_NAME =
            "/api/organisationUnits.json?paging=false&fields=id,closedDate,"
                    + "description&filter=name:eq:%s&filter:programs:id:eq:%s";

    /**
     * Endpoint suffix to retrieve orgUnits
     */
    private static final String DHIS_PULL_ORG_UNITS_API = ".json?fields=organisationUnits[*]";
    /**
     * Endpoint to patch closeDate to an OrgUnit
     */
    private static final String DHIS_PATCH_URL_CLOSED_DATE = "/api/organisationUnits/%s/closedDate";

    /**
     * Endpoint to patch description to an OrgUnit
     */
    private static final String DHIS_PATCH_URL_DESCRIPTIONCLOSED_DATE =
            "/api/organisationUnits/%s/description";

    /**
     * New Description to a closed OrgUnit
     */
    private static final String DHIS_PATCH_DESCRIPTIONCLOSED_DATE =
            "[%s] - Android Surveillance App set the closing date to %s because over 30 surveys "
                    + "were pushed within 1 hour.";

    /**
     * MediaType always json + utf8
     */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static final String ATTRIBUTEVALUES = "attributeValues";
    private static String ATTRIBUTE_VALUES = "attributeValues";
    private static String CODE = "code";
    private static String ATTRIBUTE = "attribute";
    private static String VALUE = "value";
    private static String DHIS2_GMT_NEW_DATE_FORMAT = "yyyy-MM-dd";
    private static String TAG_USER = "users";
    private static String QUERY_USER_ATTRIBUTES =
            "/%s?fields=attributeValues[value,attribute[code]]id&paging=false";

    /**
     * Current program UID (once is calculated never changes)
     */
    private static String programUID;


    /**
     * Returns current serverUrl
     */
    public static String getServerUrl() {
        return PreferencesState.getInstance().getDhisURL();
    }

    /**
     * Returns current orgUnit
     */
    public static String getOrgUnit() {
        return PreferencesState.getInstance().getOrgUnit();
    }

    /**
     * Returns the UID of the pictureapp program (from db)
     */
    public static String getProgramUID() {
        if (programUID == null) {
            programUID = Program.getFirstProgram().getUid();
        }
        return programUID;
    }

    //TODO jsanchez necessary for lao and cambodia
    /**
     * Returns hardcoded credentials for its use in sdk
     */
 /*   public static UserCredentials getSDKCredentials() {
        return SdkLoginController.getCredentials(getUserPush(), getPassPush());
    }*/

    /**
     * Returns the version of the given server.
     * Null if something went wrong
     */
    public static String getServerVersion(String url) throws IOException, JSONException {
        String serverVersion;
            String urlServerInfo = url + DHIS_SERVER_INFO;
            Response response = executeCall(null, urlServerInfo, "GET");

            //Error -> null
            if (!response.isSuccessful()) {
                Log.e(TAG,
                        "getServerVersion (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
            JSONObject data = parseResponse(response.body().string());
            serverVersion = data.getString(TAG_VERSION);
        Log.i(TAG, String.format("getServerVersion(%s) -> %s", url, serverVersion));
        return serverVersion;
    }

    /**
     * Returns true|false depending of the network connectivity.
     */
    public static boolean isNetworkAvailable() {
        Context ctx = PreferencesState.getInstance().getContext();
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            return false;
        }
        return activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Checks if the given orgUnit is open in the server.
     *
     * @param orgUnitNameOrCode OrgUnit code if server is 2.20, OrgUnit name if server is 2.21,2.22
     * @return true|false
     */
    public static boolean isOrgUnitOpen(String url, String orgUnitNameOrCode)
            throws IOException, JSONException {
        JSONObject orgUnitJSON = getOrgUnitData(url, orgUnitNameOrCode);
        if (orgUnitJSON == null) {
            return false;
        }

        return !isBanned(orgUnitJSON);
    }

    /**
     * Returns the orgUnit UID for the current server + orgunit
     */
    public static String getOrgUnitUID() throws IOException, JSONException {
        String serverUrl = getServerUrl();
        String orgUnit = getOrgUnit();
        return getOrgUnitUID(serverUrl, orgUnit);
    }

    /**
     * Returns the orgUnit UID for the given url and orgUnit (code or name)
     */
    public static String getOrgUnitUID(String url, String orgUnitNameOrCode)
            throws IOException, JSONException {
        JSONObject orgUnitJSON = getOrgUnitData(url, orgUnitNameOrCode);
        if (orgUnitJSON == null) {
            return null;
        }
        try {
            return orgUnitJSON.getString(TAG_ID);
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * Bans the orgUnit for future pushes (too many too quick)
     */
    public static void banOrg(String url, String orgUnitNameOrCode)
            throws ApiCallException {
        try {
            Log.i(TAG, String.format("banOrg(%s,%s)", url, orgUnitNameOrCode));
            JSONObject orgUnitJSON = getOrgUnitData(url, orgUnitNameOrCode);
            String orgUnitUID = null;
            if(orgUnitJSON.has(TAG_ID)) {
                orgUnitUID = orgUnitJSON.getString(TAG_ID);
            }
            String orgUnitDescription = "";
            if(orgUnitJSON.has(TAG_DESCRIPTIONCLOSEDATE)) {
                orgUnitDescription = orgUnitJSON.getString(TAG_DESCRIPTIONCLOSEDATE);
            }
            //NO OrgUnitUID -> Non blocking error, go on
            if (orgUnitUID == null) {
                Log.e(TAG, String.format("banOrg(%s,%s) -> No UID", url, orgUnitNameOrCode));
                return;
            }
            //Update date and description in the orgunit
            patchClosedDate(url, orgUnitUID);
            patchDescriptionClosedDate(url, orgUnitUID, orgUnitDescription);
        } catch (JSONException e) {
            throw new ApiCallException(
                    String.format("banOrg(%s,%s): %s", url, orgUnitNameOrCode, e.getMessage()));
        } catch (IOException e) {
            throw new ApiCallException(
                    String.format("banOrg(%s,%s): %s", url, orgUnitNameOrCode, e.getMessage()));
        } catch (NullPointerException e) {
            throw new ApiCallException(
                    String.format("banOrg(%s,%s): %s", url, orgUnitNameOrCode, e.getMessage()));
        }
    }

    /**
     * Updates the orgUnit adding a closedDate
     */
    static void patchClosedDate(String url, String orgUnitUID) throws IOException, JSONException {
        //https://malariacare.psi.org/api/organisationUnits/u5jlxuod8xQ/closedDate
            String urlPathClosedDate = getPatchClosedDateUrl(url, orgUnitUID);
            JSONObject data = prepareTodayDateValue();
            Response response = executeCall(data, urlPathClosedDate, "PATCH");
            if (!response.isSuccessful()) {
                Log.e(TAG,
                        "closingDatePatch (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
    }

    /**
     * Prepare the closing value.
     *
     * @return Closing value as Json.
     */
    static JSONObject prepareTodayDateValue() throws JSONException {
        String dateFormatted = Utils.geTodayDataString(DATE_CLOSED_DATE_FORMAT);
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_CLOSEDDATE, dateFormatted);
        return elementObject;
    }

    static void patchDescriptionClosedDate(String url, String orgUnitUID,
            String orgUnitDescription) throws IOException, JSONException {
        //https://malariacare.psi.org/api/organisationUnits/u5jlxuod8xQ/closedDate
            String urlPathClosedDescription = getPatchClosedDescriptionUrl(url, orgUnitUID);
            JSONObject data = prepareClosingDescriptionValue(orgUnitDescription);
            Response response = executeCall(data, urlPathClosedDescription, "PATCH");
            if (!response.isSuccessful()) {
                Log.e(TAG, "patchDescriptionClosedDate (" + response.code() + "): "
                        + response.body().string());
                throw new IOException(response.message());
            }
    }

    /**
     * Pull the current description and adds new closed organization description.
     *
     * @return new description.
     * @url url for pull the current description
     */
    static JSONObject prepareClosingDescriptionValue(String orgUnitDescription)
            throws JSONException {

        //New line to description
        String dateFormatted = Utils.getClosingDateString("dd-MM-yyyy");
        String dateTimestamp = Utils.getClosingDateTimestamp(
                Utils.getClosingDateString("dd-MM-yyyy")).getTime() + "";
        String description = String.format(DHIS_PATCH_DESCRIPTIONCLOSED_DATE, dateTimestamp,
                dateFormatted);

        //Previous + New line
        StringBuilder sb = new StringBuilder();
        sb.append(orgUnitDescription);
        sb.append("");//next line
        sb.append("");//next line
        sb.append(description);
        description = sb.toString();
        sb = null;

        //As a JSON
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DESCRIPTIONCLOSEDATE, description);
        return elementObject;
    }

    /**
     * Returns the orgunit data from the given server according to its current version
     */
    static JSONObject getOrgUnitData(String url, String orgUnitNameOrCode)
            throws IOException, JSONException {
        //Version is required to choose which field to match
        String serverVersion = getServerVersion(url);

        //No version -> No data
        if (serverVersion == null) {
            return null;
        }
        String urlOrgUnitData = getOrgUnitDataUrl(url, serverVersion, orgUnitNameOrCode);
        Response response = executeCall(null, urlOrgUnitData, "GET");


        //Error -> null
        if (response == null || !response.isSuccessful()) {
            Log.e(TAG, "getOrgUnitData (" + response.code() + "): " + response.body().string());
            throw new IOException(response.message());
        }
        //{"organisationUnits":[{}]}
        JSONObject jsonResponse = parseResponse(response.body().string());
        JSONArray orgUnitsArray = new JSONArray();
        if(jsonResponse.has(TAG_ORGANISATIONUNITS)) {
             orgUnitsArray = (JSONArray) jsonResponse.get(TAG_ORGANISATIONUNITS);
        }
        //0| >1 matches -> Error
        if (orgUnitsArray.length() == 0 || orgUnitsArray.length() > 1) {
            Log.e(TAG, String.format("getOrgUnitData(%s,%s) -> Found %d matches", url,
                    orgUnitNameOrCode, orgUnitsArray.length()));
            return null;
        }
        return (JSONObject) orgUnitsArray.get(0);
    }

    public static User pullUserAttributes(User loggedUser) {
        String lastMessage = loggedUser.getAnnouncement();
        String uid = loggedUser.getUid();
        String url =
                PreferencesState.getInstance().getDhisURL() + "/api/" + TAG_USER + String.format(
                        QUERY_USER_ATTRIBUTES, uid);
        url = encodeBlanks(url);
        try {
            Response response = ServerAPIController.executeCall(null, url, "GET");
            if (!response.isSuccessful()) {
                Log.e(TAG, "pushData (" + response.code() + "): " + response.body().string());
                throw new IOException(response.message());
            }
            JSONObject body = new JSONObject(response.body().string());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.convertValue(mapper.readTree(body.toString()),
                    JsonNode.class);
            JsonNode jsonNodeArray = jsonNode.get(ATTRIBUTE_VALUES);
            String newMessage = "";
            String closeDate = "";
            for (int i = 0; i < jsonNodeArray.size(); i++) {
                if (jsonNodeArray.get(i).get(ATTRIBUTE).get(CODE).textValue().equals(
                        User.ATTRIBUTE_USER_ANNOUNCEMENT)) {
                    newMessage = jsonNodeArray.get(i).get(VALUE).textValue();
                }
                if (jsonNodeArray.get(i).get(ATTRIBUTE).get(CODE).textValue().equals(
                        User.ATTRIBUTE_USER_CLOSE_DATE)) {
                    closeDate = jsonNodeArray.get(i).get(VALUE).textValue();
                }
            }
            if ((lastMessage == null && newMessage != null) || (newMessage != null
                    && !newMessage.equals("") && !lastMessage.equals(newMessage))) {
                loggedUser.setAnnouncement(newMessage);
                PreferencesState.getInstance().setUserAccept(false);
            }
            if (closeDate == null || closeDate.equals("")) {
                loggedUser.setCloseDate(null);
            } else {
                loggedUser.setCloseDate(Utils.parseStringToCalendar(closeDate,
                        DHIS2_GMT_NEW_DATE_FORMAT).getTime());
            }

        } catch (Exception ex) {
            Log.e(TAG, "Cannot read user last updated from server with");
            ex.printStackTrace();
        }
        loggedUser.save();
        return loggedUser;
    }

    public static Boolean isUserClosed(String userUid) {
        if (Session.getCredentials().isDemoCredentials()) {
            return false;
        }

        //Lets for a last event with that orgunit/program
        String url =
                PreferencesState.getInstance().getDhisURL() + "/api/" + TAG_USER + String.format(
                        QUERY_USER_ATTRIBUTES, userUid);

        url = encodeBlanks(url);
        Date closedDate = null;
        try {
            Response response = ServerAPIController.executeCall(null, url, "GET");
            if (!response.isSuccessful()) {
                Log.e(TAG, "pushData (" + response.code() + "): " + response.body().string());
                return null;
            }
            JSONObject body = new JSONObject(response.body().string());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.convertValue(mapper.readTree(body.toString()),
                    JsonNode.class);
            JsonNode jsonNodeArray = jsonNode.get(ATTRIBUTEVALUES);
            String closeDateAsString = "";
            for (int i = 0; i < jsonNodeArray.size(); i++) {
                if (jsonNodeArray.get(i).get(ATTRIBUTE).get(CODE).textValue().equals(
                        User.ATTRIBUTE_USER_CLOSE_DATE)) {
                    closeDateAsString = jsonNodeArray.get(i).get(VALUE).textValue();
                }
            }
            if (closeDateAsString == null || closeDateAsString.equals("")) {
                return false;
            }
            closedDate = Utils.parseStringToCalendar(closeDateAsString,
                    DHIS2_GMT_NEW_DATE_FORMAT).getTime();
        } catch (Exception ex) {
            Log.e(TAG, "Cannot read user last updated from server with");
            ex.printStackTrace();
            return null;
        }
        if(closedDate == null) {
            return false;
        }
        return closedDate.before(new Date());
    }

    /**
     * Checks if the orgunit is closed (due to too much surveys being pushed)
     */
    static boolean isBanned(JSONObject orgUnitJSON) throws JSONException {
        if (orgUnitJSON == null) {
            return true;
        }
        Log.d(TAG, String.format("isBanned(%s)", orgUnitJSON.toString()));
        String closedDateAsString = getClosedDate(orgUnitJSON);
        //No closedDate -> Open
        if (closedDateAsString == null || closedDateAsString.isEmpty()) {
            return false;
        }

        //CloseDate -> Check dates
        Calendar calendarClosedDate = Utils.parseStringToCalendar(closedDateAsString);

        //ClosedDate bad format -> Closed
        if (calendarClosedDate == null) {
            return true;
        }

        //If closeddate>today -> Closed
        return !Utils.isDateOverSystemDate(calendarClosedDate);
    }

    /**
     * Returns the closedDate from the given orgUnit (json format) or null if it is not present
     * (which is fine too)
     */
    static String getClosedDate(JSONObject orgUnitJSON) throws JSONException {
            if(orgUnitJSON.has(TAG_CLOSEDDATE)) {
                return orgUnitJSON.getString(TAG_CLOSEDDATE);
            }
            else{
                return null;
            }
    }

    /**
     * Returns the right endpoint depending on the server version
     */
    static String getOrgUnitDataUrl(String url, String serverVersion, String orgUnitNameOrCode) {
        String endpoint = url;
        String programUID = getProgramUID();
        if (Constants.DHIS_API_SERVER.equals(serverVersion)) {
            endpoint += String.format(DHIS_PULL_ORG_UNIT_API, orgUnitNameOrCode, programUID);
        } else {
            endpoint += String.format(DHIS_PULL_ORG_UNIT_API_BY_NAME, orgUnitNameOrCode,
                    programUID);
        }

        endpoint = encodeBlanks(endpoint);
        Log.d(TAG, String.format("getOrgUnitDataUrl(%s,%s,%s) -> %s", url, serverVersion,
                orgUnitNameOrCode, endpoint));
        return endpoint;
    }

    /**
     * Returns the ClosedDate that points to the DHIS server (Pull) API according to preferences.
     */
    static String getPatchClosedDateUrl(String url, String orguid) {
        //Get the org_ID
        String endpoint = url + String.format(DHIS_PATCH_URL_CLOSED_DATE, orguid);
        return encodeBlanks(endpoint);
    }

    /**
     * Returns the Description of orgUnit that points to the DHIS server (Pull) API according to
     * preferences.
     */
    static String getPatchClosedDescriptionUrl(String url, String orguid) {
        String endpoint = url + String.format(DHIS_PATCH_URL_DESCRIPTIONCLOSED_DATE, orguid);
        return encodeBlanks(endpoint);
    }

    static String encodeBlanks(String endpoint) {
        return endpoint.replace(" ", "%20");
    }

    /**
     * Call to DHIS Server
     */
    static Response executeCall(JSONObject data, String url, String method) throws IOException {
        final String DHIS_URL = url;

        OkHttpClient client = UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

        client.setConnectTimeout(30, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(30, TimeUnit.SECONDS);    // socket timeout
        client.setWriteTimeout(30, TimeUnit.SECONDS);    // write timeout
        client.setRetryOnConnectionFailure(false);    // Cancel retry on failure

        BasicAuthenticator basicAuthenticator = new BasicAuthenticator();
        client.setAuthenticator(basicAuthenticator);

        Request.Builder builder = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER,
                        basicAuthenticator.getCredentials())
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
     */
    static JSONObject parseResponse(String responseData) throws JSONException {
            JSONObject jsonResponse = new JSONObject(responseData);
            Log.d(TAG, "parseResponse: " + jsonResponse);
            return jsonResponse;
    }


}

/**
 * Basic authenticator required for calls
 */
class BasicAuthenticator implements Authenticator {

    public final String AUTHORIZATION_HEADER = "Authorization";
    private String credentials;

    BasicAuthenticator() {
        credentials = AuthenticationApiStrategy.getApiCredentials();
    }

    @Override
    public Request authenticate(Proxy proxy, Response response) throws IOException {
        return response.request().newBuilder().header(AUTHORIZATION_HEADER, credentials).build();
    }

    @Override
    public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
        return null;
    }

    public String getCredentials() {
        return credentials;
    }
}
