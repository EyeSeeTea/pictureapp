package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Organisation;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

import java.util.List;

public class AuthenticationLocalDataSource implements IAuthenticationDataSource {

    Context mContext;

    public AuthenticationLocalDataSource(Context context) {
        mContext = context;
    }

    @Override
    public void logout(IDataSourceCallback<Void> callback) {
        User user = User.getLoggedUser();

        if (user != null) {
            user.delete();
        }

        clearCredentials();

        Session.logout();

        PopulateDB.wipeDataBase();

        deleteOrgUnitQuestionOptions();

        callback.onSuccess(null);
    }

    @Override
    public void login(Credentials credentials, IDataSourceCallback<UserAccount> callback) {

        User user = new User(credentials.getUsername(), credentials.getUsername());

        User.insertLoggedUser(user);

        Session.setUser(user);
        Session.setCredentials(credentials);

        saveCredentials(credentials);
        if (credentials.isDemoCredentials()) {
            addTestOrganisation();
        }

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


    private void clearCredentials() {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url,
                mContext.getString(R.string.DHIS_DEFAULT_SERVER));
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, "");
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, "");
        PreferencesState.getInstance().reloadPreferences();
    }

    private void addTestOrganisation() {
        Organisation testOrganisation = new Organisation();
        testOrganisation.setName(mContext.getString(R.string.test_organisation_name));
        testOrganisation.setUid(mContext.getString(R.string.test_organisation_uid));
        testOrganisation.insert();
    }

    private void deleteOrgUnitQuestionOptions() {
        List<Question> questions = Question.getAllQuestionsWithOrgUnitDropdownList();
        //remove older values, but not the especial "other" option
        for (Question question : questions) {
            List<Option> options = question.getAnswer().getOptions();
            for (Option option : options) {
                if (QuestionOption.findByQuestionAndOption(question, option).size() == 0) {
                    option.delete();
                }
            }
        }
    }
}
