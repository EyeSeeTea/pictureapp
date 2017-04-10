package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;


public class OfflineFragment extends Fragment implements IDashboardFragment{
    @Override
    public void reloadData() {

    }

    @Override
    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().init(activity, R.string.tab_tag_stock);
    }

    @Override
    public void registerFragmentReceiver() {

    }

    @Override
    public void unregisterFragmentReceiver() {

    }
}
