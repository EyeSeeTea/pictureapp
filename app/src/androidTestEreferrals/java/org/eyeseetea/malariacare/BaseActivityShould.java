package org.eyeseetea.malariacare;

import static android.content.Context.ACTIVITY_SERVICE;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
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
        savePreviousPreferences();
        saveTestCredentialsAndProgram();
        Intent intent = new Intent(PreferencesState.getInstance().getContext(),
                DashboardActivity.class);
        EyeSeeTeaApplication.getInstance().startActivity(intent);
    }

    @After
    public void tearDown() {
        restorePreferences();
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
        previousOrganisationCredentials = credentialsLocalDataSource.getOrganisationCredentials();
        previousCredentials = credentialsLocalDataSource.getCredentials();
        ProgramLocalDataSource programLocalDataSource = new ProgramLocalDataSource();
        ProgramDB databaseProgramDB =
                ProgramDB.getProgram(
                        PreferencesEReferral.getUserProgramId());
        if (databaseProgramDB != null) {
            previousProgram = programLocalDataSource.getUserProgram();
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
        editor.putString(context.getString(R.string.dhis_url), "http:test");
        editor.commit();

        Credentials credentials = new Credentials("http:test", "test", "test");
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveOrganisationCredentials(credentials);
        ProgramDB programDB = new ProgramDB("testProgramId", "testProgram");
        programDB.save();
        ProgramLocalDataSource programLocalDataSource = new ProgramLocalDataSource();
        programLocalDataSource.saveUserProgramId(new Program("testProgram", "testProgramId"));
        PreferencesState.getInstance().setPushInProgress(false);
        UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
        userAccountDataSource.saveLoggedUser(
                new UserAccount("testUsername", "testUserUID", false, true));
        saveCredentials(new Credentials("http:test","test","test"));
    }

    private void restorePreferences() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        if (previousOrganisationCredentials != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(context.getString(R.string.dhis_url),
                    previousOrganisationCredentials.getServerURL());
            editor.commit();
        }
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveOrganisationCredentials(previousOrganisationCredentials);
        ProgramLocalDataSource programLocalDataSource = new ProgramLocalDataSource();
        if (previousProgram != null) {
            programLocalDataSource.saveUserProgramId(previousProgram);
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
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                credentials.getServerURL());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user,
                credentials.getUsername());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password,
                credentials.getPassword());
        PreferencesState.getInstance().reloadPreferences();
    }

}
