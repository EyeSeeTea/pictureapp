package org.eyeseetea.malariacare.data.authentication;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonInvalidException;
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
        try {
            if (credentialsInstance == null) {
                credentialsInstance = new CredentialsReader();
            }
        } catch (JSONException e) {
            new ConfigJsonInvalidException(e.getMessage());
        } catch (ConfigJsonInvalidException e) {
            new ConfigJsonInvalidException(e.getMessage());
        }
        return credentialsInstance;
    }

    public CredentialsReader() throws JSONException, ConfigJsonInvalidException {
        readJson();
        name = mJSONObject.getString(NAME_KEY);
        password = mJSONObject.getString(PASS_KEY);
    }

    public String getUser() throws ConfigJsonInvalidException {
        if (name == null) {
            readJson();
        }
        if (name == null) {
            new ConfigJsonInvalidException("name not valid");
        }
        return name;
    }

    public String getPassword() throws ConfigJsonInvalidException {
        if (password == null) {
            readJson();
        }
        if (password == null) {
            new ConfigJsonInvalidException("password not valid");
        }
        return password;
    }

    private void readJson() throws ConfigJsonInvalidException {
        if (mJSONObject != null) {
            return;
        }
        InputStream inputStream = null;
        try {
            inputStream =
                    PreferencesState.getInstance().getContext().getResources().openRawResource(
                            R.raw.config);
            // json is UTF-8 by default
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, "UTF-8"),
                    8);
            StringBuilder sb = new StringBuilder();

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            mJSONObject = new JSONObject(sb.toString());
        } catch (IOException e) {
            new ConfigJsonInvalidException(e.getMessage());
        } catch (JSONException e) {
            new ConfigJsonInvalidException(e.getMessage());
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                new ConfigJsonInvalidException(e.getMessage());
            }
        }
    }
}
