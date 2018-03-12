package org.eyeseetea.malariacare.layout.utils;

import android.view.View;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

/**
 * Created by idelcano on 01/11/2016.
 */

public class LayoutUtils extends BaseLayoutUtils {

    public static void setActionBar(android.support.v7.app.ActionBar actionBar) {
        LayoutUtils.setActionBarLogo(actionBar);
        actionBar.setDisplayUseLogoEnabled(false);
        // Uncomment in case of we want the logo out
        // actionBar.setLogo(null);
        // actionBar.setIcon(null);
        actionBar.setTitle(PreferencesState.getInstance().getContext().getResources().getString(
                R.string.malaria_case_based_reporting));

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
    }

}
