package org.eyeseetea.malariacare.layout.utils;

import org.eyeseetea.malariacare.DashboardActivity;

/**
 * Created by idelcano on 01/11/2016.
 */

public class LayoutUtils extends BaseLayoutUtils {

    public static void setActionBar(android.support.v7.app.ActionBar actionBar) {
        LayoutUtils.setActionBarLogo(actionBar);
    }

    public static void setTabHosts(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabHostsWithText();
    }
}
