package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.R;


public class NewReceiptFragment extends Fragment {
    public static final String TAG = ".NewReceiptFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_new_receipt,
                container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {

    }
}
