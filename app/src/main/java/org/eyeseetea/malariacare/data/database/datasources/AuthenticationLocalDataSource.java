package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.datasources.strategies
        .AuthenticationLocalDataSourceStrategy;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
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
    public void login(Credentials credentials, IDataSourceCallback<UserAccount> callback) {
        User loggedUser = User.getLoggedUser();
        String userUid = loggedUser != null ? loggedUser.getUid() : null;

        User user = new User(userUid, credentials.getUsername());

        User.insertLoggedUser(user);

        Session.setUser(user);

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
        List<Question> questions = Question.getAllQuestionsWithOrgUnitDropdownList();
        //remove older values, but not the especial "other" option
        for (Question question : questions) {
            if (question.getAnswer() != null) {
                List<Option> options = question.getAnswer().getOptions();
                for (Option option : options) {
                    if (QuestionOption.findByQuestionAndOption(question, option).size() == 0) {
                        option.delete();
                    }
                }
            }
        }
    }
}
