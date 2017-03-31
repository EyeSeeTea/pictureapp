package org.eyeseetea.malariacare.data.authentication;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadConfig {
    public static JSONObject mJSONObject;
    public static String name;
    public static String credential;


    public static String getUser() {
        if (name != null) {
            return name;
        }
        init();
        try {
            name = mJSONObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getPassword() {
        if (credential != null) {
            return credential;
        }
        init();
        try {
            credential = mJSONObject.getString("credentials");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return credential;
    }


    private static void init() {
        if (mJSONObject == null) {
            readJson();
        }
    }

    private static void readJson() {
        InputStream inputStream =
                PreferencesState.getInstance().getContext().getResources().openRawResource(
                        R.raw.config);
        try {
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),
                    8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            mJSONObject = new JSONObject(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
