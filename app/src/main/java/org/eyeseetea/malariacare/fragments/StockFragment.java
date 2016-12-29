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
import org.eyeseetea.malariacare.domain.usecase.HeaderUseCase;
import org.eyeseetea.malariacare.webview.IWebView;
import org.eyeseetea.malariacare.webview.IWebViewBuilder;

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

        View view = inflater.inflate(R.layout.stock_fragment,
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