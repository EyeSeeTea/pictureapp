package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Credentials;

import java.io.IOException;
import java.util.List;

public class LoginUseCase extends ALoginUseCase{

    public LoginUseCase(Context context) {
        super(context);
    }

    @Override
    protected void executeActions(Credentials credentials) {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, credentials.getServerURL());
    }
}
