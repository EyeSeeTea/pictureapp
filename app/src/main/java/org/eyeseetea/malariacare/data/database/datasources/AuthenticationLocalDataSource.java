package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.datasources.strategies
        .AuthenticationLocalDataSourceStrategy;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

import java.util.List;

public class AuthenticationLocalDataSource implements IAuthenticationDataSource {

    Context mContext;
    private AuthenticationLocalDataSourceStrategy

            mAuthenticationLocalDataSourceStrategy;

    public AuthenticationLocalDataSource(Context context) {
        mContext = context;
        mAuthenticationLocalDataSourceStrategy = new AuthenticationLocalDataSourceStrategy(this);
    }

    @Override
    public void logout(IDataSourceCallback<Void> callback) {
        mAuthenticationLocalDataSourceStrategy.logout(callback);
    }

    @Override
    public void forgotPassword(String username,
            IDataSourceCallback<ForgotPasswordMessage> callback) {

    }

    @Override
    public void login(Credentials credentials, IDataSourceCallback<UserAccount> callback) {
        UserDB loggedUserDB = UserDB.getLoggedUser();
        String userUid = loggedUserDB != null ? loggedUserDB.getUid() : null;

        UserDB userDB = new UserDB(userUid, credentials.getUsername());

        UserDB.insertLoggedUser(userDB);

        Session.setUserDB(userDB);

        Session.setCredentials(credentials);

        saveCredentials(credentials);

        callback.onSuccess(null);
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


    public void clearCredentials() {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                mContext.getString(R.string.DHIS_DEFAULT_SERVER));
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, "");
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, "");
        PreferencesState.getInstance().reloadPreferences();
    }


    public void deleteOrgUnitQuestionOptions() {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestionsWithOrgUnitDropdownList();
        //remove older values, but not the especial "other" option
        for (QuestionDB questionDB : questionDBs) {
            if (questionDB.getAnswerDB() != null) {
                List<OptionDB> optionDBs = questionDB.getAnswerDB().getOptionDBs();
                for (OptionDB optionDB : optionDBs) {
                    if (QuestionOptionDB.findByQuestionAndOption(questionDB, optionDB).size() == 0) {
                        optionDB.delete();
                    }
                }
            }
        }
    }
}
