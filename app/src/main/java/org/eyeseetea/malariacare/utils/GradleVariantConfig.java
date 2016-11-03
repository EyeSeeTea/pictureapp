package org.eyeseetea.malariacare.utils;

import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.listeners.SwipeTouchListener;

/**
 * Created by idelcano on 31/10/2016.
 */

public class GradleVariantConfig {
    public static boolean isSwipActionActive(){
        if(BuildConfig.activeSwipeNavigator)
            return true;
        return false;
    }
    public static boolean isButtonNavigationActive(){
        if(BuildConfig.activeButtonNavigator)
            return true;
        return false;
    }
}
