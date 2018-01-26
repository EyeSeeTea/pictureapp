package org.eyeseetea.malariacare.data.authentication;

import com.raizlabs.android.dbflow.annotation.NotNull;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.LanguagesDownloadException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CredentialsReader {
    public static final String NAME_KEY = "name";
    public static final String PASS_KEY = "password";
    private static final String JSON_KEY_POEDITOR_TOKEN = "POEditorToken";
    private static final String JSON_KEY_POEDITOR_PROJECT_ID = "POEditorProjectID";
    private static JSONObject mJSONObject;
    public static String name;
    public static String password;
    private static String poEditorToken;
    private static String poEditorProjectID;

    private static CredentialsReader credentialsInstance;

    public static CredentialsReader getInstance() throws ConfigJsonIOException,
            LanguagesDownloadException {
        if (credentialsInstance == null) {
            credentialsInstance = new CredentialsReader();
        }
        return credentialsInstance;
    }

    private CredentialsReader() throws ConfigJsonIOException, LanguagesDownloadException {
        readJson();
        try {
            name = mJSONObject.getString(NAME_KEY);
            password = mJSONObject.getString(PASS_KEY);

            if (BuildConfig.downloadLanguagesFromServer) {
                if (!mJSONObject.has(JSON_KEY_POEDITOR_TOKEN) || !mJSONObject.has(
                        JSON_KEY_POEDITOR_PROJECT_ID)) {
                    throw new LanguagesDownloadException();
                }
                poEditorToken = mJSONObject.getString(JSON_KEY_POEDITOR_TOKEN);
                poEditorProjectID = mJSONObject.getString(JSON_KEY_POEDITOR_PROJECT_ID);
            }
        } catch (JSONException e) {
            throw new ConfigJsonIOException(e);
        }
    }

    public String getUser() throws ConfigJsonIOException {
        if (name == null) {
            readJson();
        }
        if (name == null) {
            throw new ConfigJsonIOException("name not valid");
        }
        return name;
    }

    @NotNull
    public String getPOEditorToken() throws ConfigJsonIOException {
        if (poEditorToken == null) {
            throw new ConfigJsonIOException("POEditor Token not valid");
        }
        return poEditorToken;
    }

    @NotNull
    public String getPOEditorProjectID() throws ConfigJsonIOException {
        if (poEditorProjectID == null) {
            throw new ConfigJsonIOException("POEditor project id not valid");
        }
        return poEditorProjectID;
    }


    public String getPassword() throws ConfigJsonIOException {
        if (password == null) {
            readJson();
        }
        if (password == null) {
            throw new ConfigJsonIOException("password not valid");
        }
        return password;
    }

    private void readJson() throws ConfigJsonIOException {
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
            throw new ConfigJsonIOException(e);
        } catch (JSONException e) {
            throw new ConfigJsonIOException(e);
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ConfigJsonIOException(e);
            }
        }
    }
}
