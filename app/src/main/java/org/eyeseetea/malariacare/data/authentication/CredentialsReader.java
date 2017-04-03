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

    private static CredentialsReader credentialsInstance;

    public static CredentialsReader getInstance() {
        if (credentialsInstance == null) {
            credentialsInstance = new CredentialsReader();
        }
        return credentialsInstance;
    }

    public CredentialsReader() {
        readJson();
        try {
            name = mJSONObject.getString(NAME_KEY);
            password = mJSONObject.getString(PASS_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUser() {
        if (name != null) {
            readJson();
        }
        return name;
    }

    public String getPassword() {
        if (password != null) {
            readJson();
        }
        return password;
    }

    private void readJson() {
        if (mJSONObject != null) {
            return;
        }
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
