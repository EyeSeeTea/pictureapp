package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by ina on 20/12/2016.
 */


public abstract class ADashboardHeaderStrategy {

    /**
     * Singleton reference
     */
    private static DashboardHeaderStrategy instance;

    public static DashboardHeaderStrategy getInstance() {
        if (instance == null) {
            instance = new DashboardHeaderStrategy();
        }
        return instance;
    }

    abstract void init(Activity activity, int tabTag);

    abstract void hideHeader(Activity activity);

    abstract View loadHeader(Integer headerLayout, LayoutInflater inflater);

}
