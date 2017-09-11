package org.eyeseetea.malariacare.strategies;


import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.ProgressActivity;

public class ProgressActivityStrategy extends AProgressActivityStrategy {


    public ProgressActivityStrategy(ProgressActivity progressActivity) {
        super(progressActivity);
    }

    @Override
    public void finishAndGo() {
        progressActivity.finishAndGo(DashboardActivity.class);
    }
}
