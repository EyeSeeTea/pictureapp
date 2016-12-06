package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.layout.adapters.dashboard.strategies.HeaderUseCase;
import org.eyeseetea.malariacare.webview.IWebViewBuilder;
import org.eyeseetea.malariacare.webview.IWebView;

/**
 * Created by idelcano on 29/11/2016.
 */

public class StockFragment extends Fragment implements IDashboardFragment, IWebView {

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

    public void reloadHeader(Activity activity) {
        HeaderUseCase.getInstance().init(activity, R.string.tab_tag_stock);
    }

    @Override
    public void registerFragmentReceiver() {

    }

    @Override
    public void unregisterFragmentReceiver() {

    }

    public void reloadData() {
    }

    @Override
    public void reloadWebView(IWebViewBuilder monitorBuilder) {

    }

    @Override
    public WebView initWebView() {
        return null;
    }

    @Override
    public void stopWebView() {

    }
}