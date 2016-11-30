package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.R;

/**
 * Created by idelcano on 29/11/2016.
 */

public class StockFragment extends Fragment {

    public static final String TAG = ".StockFragment";
    LayoutInflater lInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        this.lInflater = LayoutInflater.from(getActivity().getApplicationContext());
        View view = lInflater.inflate(R.layout.stock_fragment,
                container, false);
        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    public void reloadData() {

    }
}