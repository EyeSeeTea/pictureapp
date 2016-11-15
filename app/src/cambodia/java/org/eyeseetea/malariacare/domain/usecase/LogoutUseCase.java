package org.eyeseetea.malariacare.domain.usecase;

import static com.google.android.gms.analytics.internal.zzy.p;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;

public class LogoutUseCase extends ALogoutUseCase{

    public LogoutUseCase(Context context) {
        super(context);
    }

    @Override
    public void execute() {
        PreferencesState.getInstance().saveStringPreference(R.string.dhis_url, context.getString(R.string.DHIS_DEFAULT_SERVER));
        PreferencesState.getInstance().reloadPreferences();
    }

}
