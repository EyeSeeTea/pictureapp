package org.eyeseetea.malariacare.fragments;

import android.app.Activity;

/**
 * Created by idelcano on 06/12/2016.
 */

public interface IDashboardFragment {
    void reloadData();

    void reloadHeader(Activity activity);

    void registerFragmentReceiver();

    void unregisterFragmentReceiver();

}
