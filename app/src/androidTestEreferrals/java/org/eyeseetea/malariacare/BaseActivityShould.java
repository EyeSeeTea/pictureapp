package org.eyeseetea.malariacare;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class BaseActivityShould extends CommonTestResourcesCalls {

    private final Object syncObject = new Object();

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

        Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        String activityName = componentInfo.getClassName();
        assertThat(activityName, is(LoginActivity.class.getName()));
    }

    @Before
    public void cleanUp() {
            grantPermission();
            savePreviousPreferences();
            saveTestCredentialsAndProgram();
            Intent intent = new Intent(PreferencesState.getInstance().getContext(),
                    DashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            EyeSeeTeaApplication.getInstance().startActivity(intent);
        try {
            synchronized (syncObject) {
                syncObject.wait(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @After
    public void tearDown() {
        restorePreferences();

    }

    public void grantPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + PreferencesState.getInstance().getContext().getPackageName()
                            + " android.permission.READ_PHONE_STATE");
            synchronized (syncObject) {
                try {
                    syncObject.wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + PreferencesState.getInstance().getContext().getPackageName()
                            + " android.permission.ACCESS_FINE_LOCATION");
        }
    }


    private void sendIntentShowLogin() {
        Intent surveysIntent = new Intent(PushService.class.getName());
        surveysIntent.putExtra(PushServiceStrategy.SERVICE_METHOD,
                PushServiceStrategy.PUSH_MESSAGE);
        surveysIntent.putExtra(PushServiceStrategy.SHOW_LOGIN, true);
        LocalBroadcastManager.getInstance(
                PreferencesState.getInstance().getContext()).sendBroadcast(surveysIntent);
    }

}
