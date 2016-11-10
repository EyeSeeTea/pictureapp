package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;

public abstract class ALoginUseCase {

    Context context;

    protected abstract void executeCustomActions(Credentials credentials, Context context);

    public void execute(Credentials credentials, Context context ){

        this.context = context;

        User user = new User(credentials.getUsername(), credentials.getPassword());

        saveUser(user);

        saveCommonCredentials(credentials);

        Session.setUser(user);

        executeCustomActions(credentials,context);
    }

    private void saveUser(User user){
        User userdb=User.existUser(user);

        if(userdb!=null)
            user=userdb;
        else
            user.save();
    }

    private void saveCommonCredentials(Credentials credentials){
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_user, credentials.getUsername());
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_password, credentials.getPassword());
    }
}
