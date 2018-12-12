package org.eyeseetea.malariacare.utils;

import android.content.Context;

import org.eyeseetea.sdk.common.Transaltor;

public class DBTranslator implements Transaltor {
    @Override
    public String getTranslation(String stringKey,Context context) {
        return Utils.getInternationalizedString(stringKey,context);
    }
}
