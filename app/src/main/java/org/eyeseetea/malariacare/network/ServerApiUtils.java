package org.eyeseetea.malariacare.network;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ServerApiUtils {

    static String encodeBlanks(String endpoint) {
        return endpoint.replace(" ", "%20");
    }


    public static JsonNode getJsonNodeMappedResponse(JSONObject body) throws ApiCallException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.convertValue(mapper.readTree(body.toString()),
                    JsonNode.class);
        } catch (IOException e) {
            throw new ApiCallException(e);
        } catch (IllegalArgumentException e) {
            throw new ApiCallException(e);
        }
        return jsonNode;
    }


    @NonNull
    protected static JSONObject getApiResponseAsJSONObject(Response response) throws ApiCallException {
        //Error -> null
        String readableBodyResponse = getReadableBodyResponse(response);
        checkResponse(response, readableBodyResponse);
        return getJSONObjectFromString(readableBodyResponse);
    }

    protected static JSONObject getJSONObjectFromString(String readableBodyResponse)
            throws ApiCallException {
        try {
            return new JSONObject(readableBodyResponse);
        }catch (JSONException e){
            throw  new ApiCallException(e);
        }
    }

    protected static void checkResponse(Response response, String readableBodyResponse)
            throws ApiCallException {
        if (readableBodyResponse == null) {
            readableBodyResponse = getReadableBodyResponse(response);
        }
        if (!response.isSuccessful()) {
            throw new ApiCallException(
                    "Server error. Code: " + response.code() + " Body: " + readableBodyResponse);
        }
    }

    @NonNull
    protected static String getReadableBodyResponse(Response response) {
        String readableBodyResponse = "";
        try {
            readableBodyResponse = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return readableBodyResponse;
    }
}
