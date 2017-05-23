package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
        if(BuildConfig.translations) {
            PreferencesState.getInstance().loadsLanguageInActivity();
        }
    }

    @Override
    public void finishAndGo() {
        super.finishAndGo(LoginActivity.class);
    }
}
