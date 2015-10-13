package org.eyeseetea.malariacare.network;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by ignac on 13/10/2015.
 * This class send have a method for call to the server with a Asyntask and get the response.
 */
public class GetResponse{

    private static String TAG=".GetResponse";

    private static String DHIS_DEFAULT_SERVER="https://malariacare.psi.org";
    private static String DHIS_GET_PROGRAM_API="/api/programs/";
    private static String DHIS_GET_ORG_UNITS_FILTER_API=".json?fields=organisationUnits";
    private static String DHIS_USERNAME="testing";
    private static String DHIS_PASSWORD="Testing2015";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    //TAG_ORGUNIT is the filter for the server
    private static String TAG_ORGUNIT="organisationUnits";
    //TAG_ORGUNITVALUE is the name of the key of the values
    private static String TAG_ORGUNITVALUE="code";

    Activity activity;

    public GetResponse(Activity activity) {
        this.activity = activity;
    }

    //Get a JSONArray and returns a String array
    public String[] jsonArrayToStringArray(JSONArray json,String value) {
        int size=0;
        for (int i = 0; i < json.length(); ++i) {
            JSONObject row = null;
            try {
                row = json.getJSONObject(i);
                if(row.getString(value)!=null)
                    size++;
            } catch (JSONException e) {
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }
        return strings;
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

    //getOrgantiation_units call the server with a asynctask anonime class(named background) and return the organitation_units
    public String[] getOrganization_units() {
        ArrayList<String> opcionesGet = new ArrayList<String>();
        opcionesGet.add(DHIS_GET_PROGRAM_API +activity.getResources().getString(R.string.UID_PROGRAM)+ DHIS_GET_ORG_UNITS_FILTER_API);
        opcionesGet.add("GET");
        String[] org_unit = null;

        try {
            org_unit=new  getBackground().execute(opcionesGet).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return org_unit;
    }

    class getBackground extends AsyncTask <ArrayList<String>, Void, String[]> {
        String url;
        String method;

        @Override
        protected void onPreExecute() {
        }

        protected String[] doInBackground(ArrayList<String>... passing) {
            ArrayList<String> parametros = passing[0]; //get passed arraylist
            url=parametros.get(0);
            method=parametros.get(1);

            String[] result = null;
            try {
                result = getOrganization_units();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result; //return result
        }

        protected void onPostExecute(Response result) {
        }

        /**
         * Get organtization unit from the server
         * @return Array of Strings with the organitation units
         * @throws Exception
         */

        public String[] getOrganization_units() throws Exception{
            //Get control data elements for existing surveys
            Response response = executeCall(null, url, method);
            if(!response.isSuccessful()){
                Log.e(TAG, "getAnalytics (" + response.code()+"): "+response.body().string());
                throw new IOException(response.message());
            }
            JSONObject responseBody = parseResponse(response.body().string());

            JSONArray jsonOrgUnits = responseBody.getJSONArray(TAG_ORGUNIT);

            String[] org_units=jsonArrayToStringArray(jsonOrgUnits, TAG_ORGUNITVALUE);
            return org_units;
        }

        /**
         * Call to DHIS Server
         * @param data
         * @param url
         */
        private Response executeCall(JSONObject data, String url, String method) throws IOException {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            final String DHIS_URL= DHIS_DEFAULT_SERVER+ url;

            OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient();

            BasicAuthenticator basicAuthenticator=new BasicAuthenticator();
            client.setAuthenticator(basicAuthenticator);

            Request.Builder builder = new Request.Builder()
                    .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                    .url(DHIS_URL);

            switch (method){
                case "POST":
                    RequestBody postBody = RequestBody.create(JSON, data.toString());
                    builder.post(postBody);
                    break;
                case "PUT":
                    RequestBody putBody = RequestBody.create(JSON, data.toString());
                    builder.put(putBody);
                    break;
                case "GET":
                    builder.get();
                    break;
            }
            Request request = builder.build();
            return client.newCall(request).execute();
        }


        /**
         * Basic
         */
        class BasicAuthenticator implements Authenticator {

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
}
