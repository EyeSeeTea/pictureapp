package org.eyeseetea.malariacare.layout;

import android.text.Spanned;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;

/**
 * Created by idelcano on 29/10/2016.
 */

public class StyleUtils {

    public static void setActionBar(android.support.v7.app.ActionBar actionBar){
        LayoutUtils.setActionBarLogo(actionBar);
    }
}
