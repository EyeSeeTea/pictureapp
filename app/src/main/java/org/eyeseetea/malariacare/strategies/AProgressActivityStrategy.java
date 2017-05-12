package org.eyeseetea.malariacare.strategies;


import org.eyeseetea.malariacare.ProgressActivity;

public abstract class AProgressActivityStrategy {

    protected ProgressActivity progressActivity;

    public AProgressActivityStrategy(ProgressActivity progressActivity) {
        this.progressActivity = progressActivity;
    }


    public abstract void finishAndGo();
}
