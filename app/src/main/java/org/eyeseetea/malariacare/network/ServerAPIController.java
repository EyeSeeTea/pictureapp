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
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApiStrategy;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private static final String TAG_NAME = "name";

    /**
     * Tag for orgunit description in json request/response
     */
    private static final String TAG_DESCRIPTIONCLOSEDATE = "description";


    /**
     * Tag for organisationUnits in json response
     */
    private static final String TAG_ORGANISATIONUNITS = "organisationUnits";

    /**
     * Date format to the closedDate attribute
     */
    private static final String DATE_CLOSED_DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Endpoint to retrieve server info (including version)
     */
    private static final String DHIS_SERVER_INFO = "/api/system/info";

    /**
     * Endpoint to retrieve orgUnits info filtering by CODE (API)
     */
    private static final String DHIS_PULL_ORG_UNIT_API =
            "/api/organisationUnits.json?paging=false&fields=id,name,closedDate,"
                    + "description&filter=code:eq:%s&filter:programs:id:eq:%s";

    /**
     * Endpoint to retrieve orgUnits info filtering by NAME (SDK)
     */
    private static final String DHIS_PULL_ORG_UNIT_API_BY_NAME =
            "/api/organisationUnits.json?paging=false&fields=id,name,closedDate,"
                    + "description&filter=name:eq:%s&filter:programs:id:eq:%s";

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
    private static final String ANCESTORS = "ancestors";
    private static final String LEVEL = "level";
    private static final String OU_PIN = "OU_PIN";
    private static final int ORG_UNIT_LEVEL = 3;
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

    /**
     * Returns the version of the given server.
     * Null if something went wrong
     */
    public static String getServerVersion(String url)
            throws ApiCallException {
        String serverVersion = null;
        String urlServerInfo = url + DHIS_SERVER_INFO;
        Response response = ServerApiCallExecution.executeCall(null, urlServerInfo, "GET");
        JSONObject body = ServerApiUtils.getApiResponseAsJSONObject(response);
        if (body.has(TAG_VERSION)) {
            try {
                serverVersion = body.getString(TAG_VERSION);
            } catch (JSONException e) {
                new ApiCallException(e);
            }
        }
        if (serverVersion != null) {
            Log.i(TAG, String.format("getServerVersion(%s) -> %s", url, serverVersion));
        }
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
            throw new ApiCallException(ex);
        }
    }

    public static void saveOrganisationUnit(OrganisationUnit organisationUnit) {
        patchClosedDate(organisationUnit);
        patchDescription(organisationUnit);
    }

    static void patchDescription(OrganisationUnit organisationUnit) {
        String url = ServerAPIController.getServerUrl();
        try {
            String urlPathClosedDescription = getPatchClosedDescriptionUrl(url,
                    organisationUnit.getUid());
            JSONObject data = parseOrganisationUnitDescriptionToJson(
                    organisationUnit.getDescription());
            Response response = ServerApiCallExecution.executeCall(data, urlPathClosedDescription, "PATCH");
            ServerApiUtils.checkResponse(response, null);
        } catch (Exception e) {
            Log.e(TAG, String.format("patchDescriptionClosedDate(%s,%s): %s", url,
                    organisationUnit.getUid(),
                    e.getMessage()));
        }
    }

    /**
     * Pull the current description.
     *
     * @return new description.
     * @url url for pull the current description
     */
    static JSONObject parseOrganisationUnitDescriptionToJson(String orgUnitDescription)
            throws Exception {
        //As a JSON
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_DESCRIPTIONCLOSEDATE, orgUnitDescription);
        return elementObject;
    }

    /**
     * Bans the orgUnit for future pushes (too many too quick)
     */
    public static boolean banOrg(String url, String orgUnitNameOrCode) throws ApiCallException, ConfigJsonIOException {
        Log.i(TAG, String.format("banOrg(%s,%s)", url, orgUnitNameOrCode));
        try {
            Log.i(TAG, String.format("banOrg(%s,%s)", url, orgUnitNameOrCode));
            JSONObject orgUnitJSON = getOrgUnitData(url, orgUnitNameOrCode);
            String orgUnitUID = "";
            String orgUnitDescription = "";
            if (orgUnitJSON.has(TAG_ID)) {
                orgUnitUID = orgUnitJSON.getString(TAG_ID);
            }
            if (orgUnitJSON.has(TAG_DESCRIPTIONCLOSEDATE)) {
                orgUnitDescription = orgUnitJSON.getString(TAG_DESCRIPTIONCLOSEDATE);
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
        } catch (JSONException e) {
            throw new ApiCallException(
                    String.format("banOrg(%s,%s): %s", url, orgUnitNameOrCode, e.getMessage()));
        } catch (ApiCallException ex) {
            return false;
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
     * Updates the orgUnit adding a closedDate
     */
    static void patchClosedDate(OrganisationUnit organisationUnit) {
        String url = ServerAPIController.getServerUrl();
        try {
            String urlPathClosedDate = getPatchClosedDateUrl(url, organisationUnit.getUid());
            JSONObject data = prepareCloseDateValue(organisationUnit);
            Response response = ServerApiCallExecution.executeCall(data, urlPathClosedDate, "PATCH");
            ServerApiUtils.checkResponse(response, null);
        } catch (Exception e) {
            Log.e(TAG,
                    String.format("patchClosedDate(%s,%s): %s", url, organisationUnit.getUid(),
                            e.getMessage()));
        }
    }

    static JSONObject prepareCloseDateValue(OrganisationUnit organisationUnit) throws JSONException {
        String dateFormatted = Utils.parseDateToString(organisationUnit.getClosedDate(),
                DATE_CLOSED_DATE_FORMAT);
        JSONObject elementObject = new JSONObject();
        elementObject.put(TAG_CLOSEDDATE, dateFormatted);
        return elementObject;
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

    public static OrganisationUnit getCurrentOrgUnit()
            throws ApiCallException {
        String url = "";
        String orgUnitNameOrCode = "";

        url = ServerAPIController.getServerUrl();
        orgUnitNameOrCode = ServerAPIController.getOrgUnit();

        JSONObject jsonObject = getOrgUnitData(url, orgUnitNameOrCode);

        return parseOrgUnit(jsonObject);
    }

    public static User pullUserAttributes(User loggedUser) throws ApiCallException {
        String lastMessage = loggedUser.getAnnouncement();
        String uid = loggedUser.getUid();
        String url =
                PreferencesState.getInstance().getDhisURL() + "/api/" + TAG_USER + String.format(
                        QUERY_USER_ATTRIBUTES, uid);
        url = ServerApiUtils.encodeBlanks(url);
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

    public static OrganisationUnit getOrganisationUnitsByCode(
            String code)
            throws ApiCallException {
        //Version is required to choose which field to match
        String serverVersion = getServerVersion(PreferencesState.getInstance().getDhisURL());

        //No version -> No data
        if (serverVersion == null) {
            return null;
        }

        try {
            String urlOrgUnitData = getOrganisationUnitsCredentialsUrl(code);
            if (!isNetworkAvailable()) {
                throw new NetworkException();
            }
            Response response = ServerApiCallExecution.executeCall(null, urlOrgUnitData, "GET");

            JSONObject body = ServerApiUtils.getApiResponseAsJSONObject(response);
            //{"organisationUnits":[{}]}
            JSONArray orgUnitsArray = (JSONArray) body.get(TAG_ORGANISATIONUNITS);

            //0| >1 matches -> Error
            if (orgUnitsArray.length() == 0 || orgUnitsArray.length() > 1) {
                Log.e(TAG, String.format("getOrgUnitData(%s) -> Found %d matches", code,
                        orgUnitsArray.length()));
                return null;
            }

            JSONObject orgUnitJO = (JSONObject) orgUnitsArray.get(0);
            return parseOrgUnit(orgUnitJO);
        } catch (NetworkException e) {
            throw new ApiCallException(e);
        } catch (Exception ex) {
            throw new ApiCallException(ex);
        }

    }

    private static OrganisationUnit parseOrgUnit(JSONObject orgUnitJO)
            throws ApiCallException {
        if (orgUnitJO != null) {

            String uid = null;
            try {
                uid = orgUnitJO.getString(TAG_ID);
                String name = orgUnitJO.has(TAG_NAME) ? orgUnitJO.getString(TAG_NAME) : "";
                String code = orgUnitJO.has(CODE) ? orgUnitJO.getString(CODE) : "";
                String description = orgUnitJO.has(TAG_DESCRIPTIONCLOSEDATE) ?
                        orgUnitJO.getString(TAG_DESCRIPTIONCLOSEDATE) : "";
                Date closedDate = orgUnitJO.has(TAG_CLOSEDDATE) ?
                        Utils.parseStringToDate(orgUnitJO.getString(TAG_CLOSEDDATE)) : null;

                JSONArray attributeValues = orgUnitJO.has(ATTRIBUTE_VALUES)
                        ? orgUnitJO.getJSONArray(
                        ATTRIBUTE_VALUES) : null;
                String pin = "";
                for (int i = 0; attributeValues != null && i < attributeValues.length(); i++) {
                    JSONObject attributeValue = attributeValues.getJSONObject(i);
                    JSONObject attribute = attributeValue.has(ATTRIBUTE)
                            ? attributeValue.getJSONObject(ATTRIBUTE) : null;
                    String attributeCode = (attribute != null && attribute.has(CODE))
                            ? attribute.getString(
                            CODE) : "";
                    if (attributeCode.equals(OU_PIN)) {
                        pin = attributeValue.has(VALUE) ? attributeValue.getString(VALUE) : "";
                    }
                }

                org.eyeseetea.malariacare.domain.entity.Program program = new org.eyeseetea
                        .malariacare.domain.entity.Program();

                JSONArray ancestors = orgUnitJO.has(ANCESTORS) ? orgUnitJO.getJSONArray(ANCESTORS)
                        : null;
                for (int i = 0; ancestors != null && i < ancestors.length(); i++) {
                    if (ancestors.getJSONObject(i).has(LEVEL) && ancestors.getJSONObject(i).getInt(
                            LEVEL) == ORG_UNIT_LEVEL) {
                        program.setId(
                                ancestors.getJSONObject(i).has(TAG_ID) ? ancestors.getJSONObject(
                                        i).getString(TAG_ID) : "");
                        program.setCode(
                                ancestors.getJSONObject(i).has(CODE) ? ancestors.getJSONObject(
                                        i).getString(CODE) : "");
                    }
                }

            return new OrganisationUnit(uid, name, code, description,
                    closedDate, pin, program);
            } catch (JSONException e) {
                throw new ApiCallException(e);
            }

        } else {
            return null;
        }
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
        if (orgUnitJSON.has(TAG_CLOSEDDATE)) {
            return orgUnitJSON.getString(TAG_CLOSEDDATE);
        } else {
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

        endpoint = ServerApiUtils.encodeBlanks(endpoint);
        Log.d(TAG, String.format("getOrgUnitDataUrl(%s,%s,%s) -> %s", url, serverVersion,
                orgUnitNameOrCode, endpoint));
        return endpoint;
    }

    static String getOrganisationUnitsCredentialsUrl(String code) {
        String url = PreferencesState.getInstance().getDhisURL()
                + "/api/organisationUnits.json?filter=code:eq:%s&fields=id,code,ancestors[id,"
                + "code,level],attributeValues[value,attribute[code]";
        url = String.format(url, code);
        return url;
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
}

