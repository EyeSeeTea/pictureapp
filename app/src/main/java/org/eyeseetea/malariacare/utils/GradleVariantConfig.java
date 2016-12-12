package org.eyeseetea.malariacare.utils;

import org.eyeseetea.malariacare.BuildConfig;

/**
 * Created by idelcano on 31/10/2016.
 */

public class GradleVariantConfig {
    public static boolean isSwipeActionActive() {
        if (BuildConfig.activeSwipeNavigator) {
            return true;
        }
        return false;
    }

    public static boolean isButtonNavigationActive() {
        if (BuildConfig.activeButtonNavigator) {
            return true;
        }
        return false;
    }

    public static boolean isStockFragmentActive() {
        if (BuildConfig.activeStockFragment) {
            return true;
        }
        return false;
    }
}
