package org.eyeseetea.malariacare.variantadapter;


import android.content.Intent;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.SettingsActivity;

public  class ProgressVariantAdapter extends AProgressVariantAdapter{


    public ProgressVariantAdapter(ProgressActivity progressActivity) {
        super(progressActivity);
    }

    @Override
    public void finishAndGo() {
        Intent intent = new Intent(progressActivity,SettingsActivity.class);

        progressActivity.finish();
        progressActivity.startActivity(intent);
    }
}
