package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by ina on 20/12/2016.
 */

public class DashboardHeaderStrategy  extends  ADashboardHeaderStrategy{

    @Override
    public View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        return inflater.inflate(headerLayout, null, false);
    }

    @Override
    public void init(Activity activity, int tabTag) {

    }

    @Override
    public void hideHeader(Activity activity) {
    }
}
