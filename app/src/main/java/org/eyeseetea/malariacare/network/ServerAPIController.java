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
import android.support.annotation.NonNull;
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
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.Calendar;
import java.util.Date;

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
     * Returns the version of the default server
     * Null if something went wrong
     */
    public static String getServerVersion() throws ApiCallException {
        return getServerVersion(PreferencesState.getInstance().getDhisURL());
    }

    /**
     * Returns the version of the given server.
     * Null if something went wrong
     */
    public static String getServerVersion(String url) throws ApiCallException {
        String urlServerInfo = url + DHIS_SERVER_INFO;
        Response response = ServerApiCallExecution.executeCall(null, urlServerInfo, "GET");
        JSONObject data = ServerApiUtils.getApiResponseAsJSONObject(response);
        try {
            return data.getString(TAG_VERSION);
        } catch (JSONException e) {
            throw new ApiCallException(e);
            }
    }

    /**
     * Checks if the given url corresponds to a 2.20 server (uses API for some ops)
     */
    public static boolean isAPIServer() throws ApiCallException {
        return isAPIServer(getServerUrl());
    }

    /**
     * Checks if the given url corresponds to a 2.20 server (uses API for some ops)
     */
    public static boolean isAPIServer(String url) throws ApiCallException {
        String serverVersion = getServerVersion(url);
        return isAPIVersion(serverVersion);
    }

    /**
     * Checks if the given version corresponds to 2.20
     */
    public static boolean isAPIVersion(String serverVersion) {
        return Constants.DHIS_API_SERVER.equals(serverVersion);
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
     * Checks if data can be pushed into the server
     */
    public static boolean isReadyForPush() throws ApiCallException {
        String serverUrl = getServerUrl();
        String orgUnit = getOrgUnit();
        return isReadyForPush(serverUrl, orgUnit);
    }

    /**
     * Checks if data can be pushed into the server
     */
    public static boolean isReadyForPush(String url, String orgUnitCodeOrName)
            throws ApiCallException {
        if (checkIfNetworkIsAvailable(url, orgUnitCodeOrName)) return false;

        if (checkIfIsValidProgram(url, orgUnitCodeOrName)) return false;

        if (checkifOrgUnitExists(url, orgUnitCodeOrName)) return false;

        if (checkIfOrgUnitIsOpen(url, orgUnitCodeOrName)) return false;

        return true;
    }

    public static boolean checkIfNetworkIsAvailable(String url, String orgUnitCodeOrName) {
        if (!isNetworkAvailable()) {
            Log.w(TAG, String.format("isReadyForPush(%s,%s) -> Network not available", url,
                    orgUnitCodeOrName));
            return true;
        }
        return false;
    }

    public static boolean checkIfIsValidProgram(String url, String orgUnitCodeOrName)
            throws ApiCallException {
        if (!isValidProgram(url)) {
            Log.w(TAG, String.format("isReadyForPush(%s,%s) -> Program not found in server", url,
                    orgUnitCodeOrName));
            return true;
        }
        return false;
    }

    public static boolean checkifOrgUnitExists(String url, String orgUnitCodeOrName)
            throws ApiCallException {
        if (orgUnitCodeOrName == null || orgUnitCodeOrName.equals("") || !isValidOrgUnit(url,
                orgUnitCodeOrName)) {
            Log.w(TAG, String.format("isReadyForPush(%s,%s) -> OrgUnit not found in server", url,
                    orgUnitCodeOrName));
            return true;
        }
        return false;
    }

    public static boolean checkIfOrgUnitIsOpen(String url, String orgUnitCodeOrName)
            throws ApiCallException {
        if (!isOrgUnitOpen(url, orgUnitCodeOrName)) {
            Log.w(TAG, String.format("isOrgUnitOpen(%s,%s) -> OrgUnit closed, push is not enabled",
                    url, orgUnitCodeOrName));
            return true;
        }
        return false;
    }

    /**
     * Returns if the given url contains the current program
     */
    public static boolean isValidProgram(String url) throws ApiCallException {
        Log.d(TAG, String.format("isValidProgram(%s) ...", url));
        String programUIDInServer;
        try {
            String urlValidProgram = getIsValidProgramUrl(url);
            Response response = ServerApiCallExecution.executeCall(null, urlValidProgram, "GET");
            JSONObject data = ServerApiUtils.getApiResponseAsJSONObject(response);
            programUIDInServer = String.valueOf(data.get(TAG_ID));
        } catch (JSONException ex) {
            throw new ApiCallException(ex);
        }
        boolean valid = getProgramUID() != null && getProgramUID().equals(programUIDInServer);
        Log.d(TAG, String.format("isValidProgram(%s) -> %b (Thread: %d)", url, valid,
                Thread.currentThread().getId()));
        return valid;
    }

    /**
     * Checks if the given orgUnit is present in the server.
     * XXX The endpoint changes from using code|name field in API|SDK servers.
     *
     * @param orgUnitNameOrCode OrgUnit code if server is 2.20, OrgUnit name if server is 2.21,2.22
     * @return true|false
     */
    public static boolean isValidOrgUnit(String url, String orgUnitNameOrCode)
            throws ApiCallException {
        JSONObject orgUnitJSON = getOrgUnitData(url, orgUnitNameOrCode);
        return orgUnitJSON != null;
    }

    /**
     * Checks if the given orgUnit is open in the server.
     *
     * @param orgUnitNameOrCode OrgUnit code if server is 2.20, OrgUnit name if server is 2.21,2.22
     * @return true|false
     */
    public static boolean isOrgUnitOpen(String url, String orgUnitNameOrCode)
            throws ApiCallException {
        JSONObject orgUnitJSON = getOrgUnitData(url, orgUnitNameOrCode);
        if (orgUnitJSON == null) {
            return false;
        }

        return !isBanned(orgUnitJSON);
    }

    /**
     * Returns the orgUnit UID for the current server + orgunit
     */
    public static String getOrgUnitUID() throws ApiCallException {
        String serverUrl = getServerUrl();
        String orgUnit = getOrgUnit();
        return getOrgUnitUID(serverUrl, orgUnit);
    }

    /**
     * Returns the orgUnit UID for the given url and orgUnit (code or name)
     */
    public static String getOrgUnitUID(String url, String orgUnitNameOrCode)
            throws ApiCallException {
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
    public static boolean banOrg(String url, String orgUnitNameOrCode) {
        Log.i(TAG, String.format("banOrg(%s,%s)", url, orgUnitNameOrCode));
        try {
            JSONObject orgUnitJSON = getOrgUnitData(url, orgUnitNameOrCode);
            String orgUnitUID = "";
            String orgUnitDescription = "";
            try {
                orgUnitUID = orgUnitJSON.getString(TAG_ID);
                orgUnitDescription = orgUnitJSON.getString(TAG_DESCRIPTIONCLOSEDATE);
            } catch (JSONException e) {
                new ApiCallException(e);
                return false;
            }
            //NO OrgUnitUID -> Non blocking error, go on
            if (orgUnitUID == null) {
                new ApiCallException(
                        String.format("banOrg(%s,%s) -> No UID", url, orgUnitNameOrCode));
                return false;
            }
            //Update date and description in the orgunit
            patchClosedDate(url, orgUnitUID);
            patchDescriptionClosedDate(url, orgUnitUID, orgUnitDescription);
            return true;
        } catch (ApiCallException ex) {
            return false;
        }
    }


    /**
     * This method returns a String[] whit the Organitation codes
     */
    public static String[] pullOrgUnitsCodes(String url) {

        try {
            String orgUnitsURL = getDhisOrgUnitsURL(url);
            Response response = ServerApiCallExecution.executeCall(null, orgUnitsURL, "GET");

            //{"organisationUnits":[{}]}
            JSONArray orgUnitsArray = ServerApiUtils.getApiResponseAsJSONObject(response).getJSONArray(
                    TAG_ORGANISATIONUNITS);

            //0 matches -> Error
            if (orgUnitsArray.length() == 0) {
                throw new Exception("Found 0 matches");
            }
            return Utils.jsonArrayToStringArray(orgUnitsArray, TAG_CODE);

        } catch (Exception ex) {
            Log.e(TAG, String.format("pullOrgUnitsCodes(%url): %s", url, ex.getMessage()));
            String[] value = new String[1];
            value[0] = "";
            return value;
        }

    }

    /**
     * This method returns a String[] whit the Organitation codes
     */
    public static String[] pullOrgUnitsNotBannedCodes(String url) {

        try {
            String orgUnitsURL = getDhisOrgUnitsURL(url);
            Response response = ServerApiCallExecution.executeCall(null, orgUnitsURL, "GET");

            //{"organisationUnits":[{}]}
            JSONArray orgUnitsArray = ServerApiUtils.getApiResponseAsJSONObject(response).getJSONArray(
                    TAG_ORGANISATIONUNITS);
            //fixme loop removing the orgunits banned (orgUnitsArray)
            //mehtod isBanned()
            //0 matches -> Error
            if (orgUnitsArray.length() == 0) {
                throw new Exception("Found 0 matches");
            }
            return Utils.jsonArrayToStringArray(orgUnitsArray, TAG_ID);

        } catch (Exception ex) {
            Log.e(TAG, String.format("pullOrgUnitsCodes(%url): %s", url, ex.getMessage()));
            String[] value = new String[1];
            value[0] = "";
            return value;
        }

    }

    /**
     * Updates the orgUnit adding a closedDate
     */
    static void patchClosedDate(String url, String orgUnitUID) throws ApiCallException {
        //https://malariacare.psi.org/api/organisationUnits/u5jlxuod8xQ/closedDate
        try {
            String urlPathClosedDate = getPatchClosedDateUrl(url, orgUnitUID);
            JSONObject data = prepareTodayDateValue();
            Response response = ServerApiCallExecution.executeCall(data, urlPathClosedDate, "PATCH");
            ServerApiUtils.checkResponse(response, null);
        } catch (JSONException e) {
            throw new ApiCallException(e);
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
            String orgUnitDescription) throws ApiCallException {
        //https://malariacare.psi.org/api/organisationUnits/u5jlxuod8xQ/closedDate
        try {
            String urlPathClosedDescription = getPatchClosedDescriptionUrl(url, orgUnitUID);
            JSONObject data = prepareClosingDescriptionValue(orgUnitDescription);
            Response response = ServerApiCallExecution.executeCall(data, urlPathClosedDescription, "PATCH");
            ServerApiUtils.checkResponse(response,null);
        } catch (JSONException e) {
            throw new ApiCallException(e);
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
        if (orgUnitDescription == null) {
            orgUnitDescription = "";
        }
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
    static JSONObject getOrgUnitData(String url, String orgUnitNameOrCode) throws ApiCallException {
        //Version is required to choose which field to match
        String serverVersion = getServerVersion(url);

        //No version -> No data
        if (serverVersion == null) {
            return null;
        }

        String urlOrgUnitData = getOrgUnitDataUrl(url, serverVersion, orgUnitNameOrCode);
        Response response = ServerApiCallExecution.executeCall(null, urlOrgUnitData, "GET");
        //{"organisationUnits":[{}]}
        JSONObject jsonResponse = ServerApiUtils.getApiResponseAsJSONObject(response);
        JSONArray orgUnitsArray = null;
        try {
            orgUnitsArray = (JSONArray) jsonResponse.get(TAG_ORGANISATIONUNITS);
        } catch (JSONException e) {
            throw new ApiCallException(e);
        }

        //0| >1 matches -> Error
        if (orgUnitsArray.length() == 0 || orgUnitsArray.length() > 1) {
            Log.e(TAG, String.format("getOrgUnitData(%s,%s) -> Found %d matches", url,
                    orgUnitNameOrCode, orgUnitsArray.length()));
            return null;
        }
        try {
            return (JSONObject) orgUnitsArray.get(0);
        } catch (JSONException e) {
            throw new ApiCallException(e);
        }
    }

    public static User pullUserAttributes(User loggedUser) throws ApiCallException {
        String lastMessage = loggedUser.getAnnouncement();
        String uid = loggedUser.getUid();
        String url =
                PreferencesState.getInstance().getDhisURL() + "/api/" + TAG_USER + String.format(
                        QUERY_USER_ATTRIBUTES, uid);
        url = ServerApiUtils.encodeBlanks(url);
        try {
            Response response = ServerApiCallExecution.executeCall(null, url, "GET");
            JSONObject body = ServerApiUtils.getApiResponseAsJSONObject(response);
            JsonNode jsonNode = ServerApiUtils.getJsonNodeMappedResponse(body);
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
            loggedUser.save();
            return loggedUser;
        } catch (ApiCallException ex) {
            Log.e(TAG, "Cannot read user last updated from server with");
            return null;
        }
    }

    public static boolean isUserClosed(String userUid) throws ApiCallException {
        if (Session.getCredentials().isDemoCredentials()) {
            return false;
        }

        //Lets for a last event with that orgunit/program
        String url =
                PreferencesState.getInstance().getDhisURL() + "/api/" + TAG_USER + String.format(
                        QUERY_USER_ATTRIBUTES, userUid);

        url = ServerApiUtils.encodeBlanks(url);
        Date closedDate = null;
        Response response = ServerApiCallExecution.executeCall(null, url, "GET");
        JsonNode jsonNode = ServerApiUtils.getJsonNodeMappedResponse(ServerApiUtils.getApiResponseAsJSONObject(response));

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
        if(closedDate == null) {
            return false;
        }
        return closedDate.before(new Date());
    }

    /**
     * Checks if the orgunit is closed (due to too much surveys being pushed)
     */
    static boolean isBanned(JSONObject orgUnitJSON) throws ApiCallException {
        if (orgUnitJSON == null) {
            return true;
        }
        Log.d(TAG, String.format("isBanned(%s)", orgUnitJSON.toString()));
        try {
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

        } catch (NullPointerException ex) {
            new ApiCallException(ex);
            return false;
        }
    }

    /**
     * Returns the closedDate from the given orgUnit (json format) or null if it is not present
     * (which is fine too)
     */
    static String getClosedDate(JSONObject orgUnitJSON) {
        try {
            return orgUnitJSON.getString(TAG_CLOSEDDATE);
        } catch (JSONException ex) {
            return null;
        }
    }

    /**
     * This method returns the valid url for check the program
     *
     * @return url for ask if the program uid exist with the UID_PROGRAM value.
     */
    static String getIsValidProgramUrl(String url) {
        String endpoint = url + DHIS_PULL_PROGRAM + getProgramUID() + DHIS_EXIST_PROGRAM;
        endpoint = ServerApiUtils.encodeBlanks(endpoint);
        Log.d(TAG, String.format("getIsValidProgramUrl(%s)->%s", url, endpoint));
        return endpoint;
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

        endpoint = ServerApiUtils.encodeBlanks(endpoint);
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
        return ServerApiUtils.encodeBlanks(endpoint);
    }

    /**
     * Returns the Description of orgUnit that points to the DHIS server (Pull) API according to
     * preferences.
     */
    static String getPatchClosedDescriptionUrl(String url, String orguid) {
        String endpoint = url + String.format(DHIS_PATCH_URL_DESCRIPTIONCLOSED_DATE, orguid);
        return ServerApiUtils.encodeBlanks(endpoint);
    }

    /**
     * Returns the URL that points to the DHIS server (Pull) API according to preferences.
     */
    static String getDhisOrgUnitsURL(String url) {
        String endpoint = url + DHIS_PULL_PROGRAM + getProgramUID() + DHIS_PULL_ORG_UNITS_API;
        return ServerApiUtils.encodeBlanks(endpoint);
    }
}

