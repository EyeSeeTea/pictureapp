package org.eyeseetea.malariacare.views;

import android.content.res.Resources;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.sdk.presentation.styles.FontStyle;

public class FontUtils {

    public static void applyFontStyleByPreference(Resources resources, Resources.Theme theme) {
        FontStyle fontStyle = PreferencesState.getInstance().getFontStyle();

        theme.applyStyle(fontStyle.getResId(), true);
    }
}
