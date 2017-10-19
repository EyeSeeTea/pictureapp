package org.eyeseetea.malariacare.layout.utils;

import android.view.View;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;

/**
 * Created by idelcano on 01/11/2016.
 */

public class LayoutUtils extends BaseLayoutUtils {

    public static void setActionBar(android.support.v7.app.ActionBar actionBar) {
        LayoutUtils.setActionBarLogo(actionBar);
    }

    public static void setTabHosts(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabHostsWithImages();
    }

    public static void setDivider(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabDivider();
    }

    public static void setTabDivider(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabDivider();
    }


    public static void fixRowViewBackground(View row, int position) {
        row.setBackgroundColor(
                row.getContext().getResources().getColor(R.color.tab_pressed_background));
    }


}
