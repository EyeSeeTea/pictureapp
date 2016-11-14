package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;

/**
 * Use case where execute actions related when login use case in our app not in sdk
 */
public abstract class ALoginUseCase {

    protected Context context;

    public ALoginUseCase(Context context){
        this.context = context;
    }

    protected abstract void executeActions(Credentials credentials);

    public void execute(Credentials credentials){
        executeActions(credentials);
    }
}
