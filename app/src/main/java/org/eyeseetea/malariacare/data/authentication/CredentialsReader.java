package org.eyeseetea.malariacare.data.authentication;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CredentialsReader {
    public static final String NAME_KEY = "name";
    public static final String PASS_KEY = "password";
    public static JSONObject mJSONObject;
    public static String name;
    public static String password;


    public static String getUser() {
        if (name != null) {
            return name;
        }
        init();
        try {
            name = mJSONObject.getString(NAME_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    public static String getPassword() {
        if (password != null) {
            return password;
        }
        init();
        try {
            password = mJSONObject.getString(PASS_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return password;
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
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
