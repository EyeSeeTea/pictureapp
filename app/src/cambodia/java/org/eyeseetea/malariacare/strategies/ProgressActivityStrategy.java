package org.eyeseetea.malariacare.strategies;


import android.content.Intent;

import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.SettingsActivity;

public class ProgressActivityStrategy extends AProgressActivityStrategy {


    public ProgressActivityStrategy(ProgressActivity progressActivity) {
        super(progressActivity);
    }

    @Override
    public void finishAndGo() {
        Intent intent = new Intent(progressActivity, SettingsActivity.class);

        intent.putExtra(SettingsActivity.IS_LOGIN_DONE, true);

        progressActivity.finish();
        progressActivity.startActivity(intent);
    }
}
