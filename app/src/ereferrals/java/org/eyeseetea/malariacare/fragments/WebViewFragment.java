package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;


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

    @Override
    public void onStart() {
        getActivity().registerReceiver(connectionReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(connectionReceiver);
        super.onStop();
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



    @Override
    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().init(activity, title);
    }

    public void reloadHeader(Activity activity, int id) {
        DashboardHeaderStrategy.getInstance().init(activity, id);
    }

    @Override
    public void registerFragmentReceiver() {

    }

    @Override
    public void unregisterFragmentReceiver() {

    }

    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(
                    Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(
                    Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private void loadUrlInWebView() {
        if (mWebView != null) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
                    Activity.CONNECTIVITY_SERVICE);
            if (cm != null && cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isConnected()) {
                loadValidUrl();
            } else {
                mWebView.loadUrl(String.format(getString(R.string.error_web_resource),
                        getString(R.string.error_web)));
            }
        }
    }

    private void loadValidUrl() {
        if (mWebView != null) {
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            clearCookies(getActivity());
            mWebView.loadUrl(url);
        }
    }

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityStatus.isConnected(getActivity())) {
                loadValidUrl();
            }
        }
    };


}
