package org.eyeseetea.malariacare;

import static android.content.Context.ACTIVITY_SERVICE;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SettingsActivityShould {

    private final Object syncObject = new Object();

    @Before
    public void grantPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getContext().getPackageName()
                            + " android.permission.READ_PHONE_STATE");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getContext().getPackageName()
                            + " android.permission.ACCESS_FINE_LOCATION");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getContext().getPackageName()
                            + " android.permission.INTERNET");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getContext().getPackageName()
                            + " android.permission.ACCESS_NETWORK_STATE");
        }
    }

    @Test
    public void onLoginIntentShowLoginActivity() throws InterruptedException {
        synchronized (syncObject) {
            syncObject.wait(2000);
        }
        sendIntentShowLogin();
        synchronized (syncObject) {
            syncObject.wait(1000);
        }
        isLoginActivityShowing();
    }

    private void isLoginActivityShowing() {
        ActivityManager am =
                (ActivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        ACTIVITY_SERVICE);
        // get the info from the currently running task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        Log.d("topActivity", BuildConfig.FLAVOR+ " " + BuildConfig.APPLICATION_ID +"CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String activityName = componentInfo.getClassName();
        assertThat(activityName, is(LoginActivity.class.getName()));
    }

    @Before
    public void cleanUp() {
        Intent intent = new Intent(PreferencesState.getInstance().getContext(),
                SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        EyeSeeTeaApplication.getInstance().startActivity(intent);
    }


    private void sendIntentShowLogin() {
        Intent surveysIntent = new Intent(PushService.class.getName());
        surveysIntent.putExtra(PushServiceStrategy.SERVICE_METHOD,
                PushServiceStrategy.PUSH_MESSAGE);
        surveysIntent.putExtra(PushServiceStrategy.SHOW_LOGIN, true);
        LocalBroadcastManager.getInstance(
                InstrumentationRegistry.getContext()).sendBroadcast(surveysIntent);
    }


}
