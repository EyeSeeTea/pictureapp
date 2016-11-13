package org.eyeseetea.malariacare.layout.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

/**
 * Created by idelcano on 01/11/2016.
 */

public class LayoutUtils extends BaseLayoutUtils {

    public static void setActionBar(android.support.v7.app.ActionBar actionBar){
        LayoutUtils.setActionBarLogo(actionBar);
    }
    public static void setTabHosts(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabHostsWithText();
    }
}
