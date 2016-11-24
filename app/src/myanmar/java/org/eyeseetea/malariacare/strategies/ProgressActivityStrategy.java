package org.eyeseetea.malariacare.strategies;


import android.content.Intent;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.ProgressActivity;

public class ProgressActivityStrategy extends AProgressActivityStrategy {


    public ProgressActivityStrategy(ProgressActivity progressActivity) {
        super(progressActivity);
    }

    @Override
    public void finishAndGo() {
        Intent intent = new Intent(progressActivity, DashboardActivity.class);

        progressActivity.finish();
        progressActivity.startActivity(intent);
    }
}
