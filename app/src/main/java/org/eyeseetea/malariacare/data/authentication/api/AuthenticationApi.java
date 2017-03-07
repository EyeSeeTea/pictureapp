package org.eyeseetea.malariacare.data.authentication.api;

import android.support.annotation.NonNull;

import com.squareup.okhttp.Credentials;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;

public class AuthenticationApi {


    public static String getHardcodedApiCredentials() {
        return
                Credentials.basic(getHardcodedApiUser(),
                        getHardcodedApiPass());
    }

    public static String getApiCredentialsFromLogin() {
        return
                Credentials.basic(Session.getCredentials().getUsername(),
                        Session.getCredentials().getPassword());
    }

    @NonNull
    static String getHardcodedApiUser() {
        return PreferencesState.getInstance().getContext().getResources().getString(
                R.string.user_push);
    }

    @NonNull
    static String getHardcodedApiPass() {
        return PreferencesState.getInstance().getContext().getResources().getString(
                R.string.pass_push);
    }
}
