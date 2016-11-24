package org.eyeseetea.malariacare.views.listeners;

import android.view.View;

/**
 * Created by idelcano on 14/11/2016.
 */

public class OnCustomFocusChangeListener implements View.OnFocusChangeListener{


    private long mTextLostFocusTimestamp;

    private void reclaimFocus(View v, long timestamp) {
        if (timestamp == -1)
            return;
        if ((System.currentTimeMillis() - timestamp) < 250)
            v.requestFocus();
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus)
            mTextLostFocusTimestamp = System.currentTimeMillis();
    }
}
