package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;


public class WebViewFragment extends Fragment implements IDashboardFragment {
    public static final String TAG = ".WebViewFragment";
    public static final String WEB_VIEW_URL = "webViewUrl";
    public static final String TITLE = "title";

    private String url;
    private int title;
    private WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_web_view,
                container, false);
        manageBundle(savedInstanceState);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mWebView = (WebView) view.findViewById(R.id.web_view);
        loadUrlInWebView();
    }

    private void manageBundle(Bundle savedInstanceState) {
        Bundle bundle = (savedInstanceState == null) ? getArguments() : savedInstanceState;
        url = bundle.getString(WEB_VIEW_URL);
        title = bundle.getInt(TITLE);
    }

    @Override
    public void reloadData() {
        loadUrlInWebView();
    }

    private void loadUrlInWebView() {
        if (mWebView != null) {
            mWebView.loadUrl(url);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
        }
    }

    @Override
    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().init(activity, title);
    }

    @Override
    public void registerFragmentReceiver() {

    }

    @Override
    public void unregisterFragmentReceiver() {

    }
}
