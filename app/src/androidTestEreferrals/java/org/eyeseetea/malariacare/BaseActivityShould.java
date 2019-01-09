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

public class BaseActivityShould {

    private Credentials previousOrganisationCredentials;
    private Credentials previousCredentials;
    private Program previousProgram;
    private boolean previousPushInProgress;
    private UserAccount previousUserAccount;

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

    private void savePreviousPreferences() {
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        previousOrganisationCredentials = credentialsLocalDataSource.getLastValidCredentials();
        previousCredentials = credentialsLocalDataSource.getCredentials();
        ProgramRepository programRepository = new ProgramRepository();
        ProgramDB databaseProgramDB =
                ProgramDB.getProgram(
                        PreferencesEReferral.getUserProgramId());
        if (databaseProgramDB != null) {
            previousProgram = programRepository.getUserProgram();
        }
        previousPushInProgress = PreferencesState.getInstance().isPushInProgress();
        UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
        previousUserAccount = userAccountDataSource.getLoggedUser();
    }

    private void saveTestCredentialsAndProgram() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.web_service_url),
                context.getString(R.string.ws_base_url));
        editor.commit();

        Credentials credentials = new Credentials(context.getString(R.string.ws_base_url),
                "test", "test");
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveLastValidCredentials(credentials);
        ProgramDB programDB = new ProgramDB("testProgramId", "testProgram");
        programDB.save();
        ProgramRepository programRepository = new ProgramRepository();
        programRepository.saveUserProgramId(new Program("testProgram", "testProgramId"));
        PreferencesState.getInstance().setPushInProgress(false);
        UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
        userAccountDataSource.saveLoggedUser(
                new UserAccount("testUsername", "testUserUID", false, true));
        saveCredentials(credentials);
    }

    private void restorePreferences() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        if (previousOrganisationCredentials != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(context.getString(R.string.web_service_url),
                    previousOrganisationCredentials.getServerURL());
            editor.commit();
        }
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveLastValidCredentials(previousOrganisationCredentials);
        ProgramRepository programRepository = new ProgramRepository();
        if (previousProgram != null) {
            programRepository.saveUserProgramId(previousProgram);
        } else {
            PreferencesEReferral.saveUserProgramId(-1l);
        }
        PreferencesState.getInstance().setPushInProgress(previousPushInProgress);
        if (previousUserAccount != null) {
            UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
            userAccountDataSource.saveLoggedUser(previousUserAccount);
        }
        if (previousCredentials != null) {
            saveCredentials(previousCredentials);
        }
    }

    private void saveCredentials(Credentials credentials) {
        PreferencesState.getInstance().saveStringPreference(R.string.server_url_key,
                credentials.getServerURL());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user,
                credentials.getUsername());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password,
                credentials.getPassword());
        PreferencesState.getInstance().reloadPreferences();
    }

}
