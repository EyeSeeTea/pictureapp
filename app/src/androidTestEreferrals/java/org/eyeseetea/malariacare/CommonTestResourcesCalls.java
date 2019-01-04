package org.eyeseetea.malariacare;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.junit.After;

import java.io.IOException;

public class CommonTestResourcesCalls {


    private Credentials previousOrganisationCredentials;
    private Credentials previousCredentials;
    private Program previousProgram;
    private boolean previousPushInProgress;
    private UserAccount previousUserAccount;

    public void savePreviousPreferences() {
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

    public void saveTestCredentialsAndProgram() {
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

    public void restorePreferences() {
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

    @After
    public void tearDown() throws IOException {
        restorePreferences();
    }

}
