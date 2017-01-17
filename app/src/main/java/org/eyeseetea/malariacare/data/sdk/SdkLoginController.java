package org.eyeseetea.malariacare.data.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.network.UserCredentials;

/**
 * Created by idelcano on 15/11/2016.
 */

public class SdkLoginController extends SdkController {

    private static UserCredentials sdkCredentials;

    //BaseActivity
    public static void logOutUser() {
        D2.me().signOut();
    }

    public static void logOutUser(Activity activity) {
        logOutUser();
        Intent intent = new Intent(activity, LoginActivity.class);
        ActivityCompat.startActivity(activity, intent, null);
    }

    public static void logInUser(String url, UserCredentials userCredentials) {
        //// FIXME: 28/12/16  Add server configuration
        D2.me().signIn(userCredentials.getUsername(), userCredentials.getPassword());
    }
    public static void login(String username, String password) {
        D2.me().signIn(username, password);
    }


    public static UserCredentials getCredentials(String username, String password) {
        if (sdkCredentials == null) {
            sdkCredentials = new UserCredentials(username, password);
        }
        return sdkCredentials;
    }

    public static void logOutAndMove(Context context) {
        LogoutUseCase logoutUseCase = new LogoutUseCase(context);
        logoutUseCase.execute();
    }
}
