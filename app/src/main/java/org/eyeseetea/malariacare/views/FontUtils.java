package org.eyeseetea.malariacare.views;

import android.content.res.Resources;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

public class FontUtils {

    public static void applyFontStyleByPreference(Resources resources, Resources.Theme theme) {
        String scale = PreferencesState.getInstance().getScale();
        if (scale != null && theme != null) {
            if (scale.equals(resources.getString(R.string.font_size_level2))) {
                theme.applyStyle(R.style.FontStyle_Medium, true);
            } else if (scale.equals(resources.getString(R.string.font_size_level3))) {
                theme.applyStyle(R.style.FontStyle_Large, true);
            } else if (scale.equals(resources.getString(R.string.font_size_level4))) {
                theme.applyStyle(R.style.FontStyle_XLarge, true);
            } else {
                theme.applyStyle(R.style.FontStyle_Default, true);
            }
        }
    }
}
