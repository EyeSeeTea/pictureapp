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
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.Toast;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.ConnectivityStatus;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;


public class WebViewFragment extends Fragment implements IDashboardFragment {
    public static final String TAG = ".WebViewFragment";
    public static final String WEB_VIEW_URL = "webViewUrl";
    public static final String TITLE = "title";

    private String url;
    private int title;
    private WebView mWebView;
    private CustomTextView mErrorDemoText;
    private boolean loadedFirstTime;

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
        mErrorDemoText = (CustomTextView) view.findViewById(R.id.error_demo_text);

        loadUrlInWebView();
        view.findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUrlInWebView();
            }
        });
    }

    private void manageBundle(Bundle savedInstanceState) {
        Bundle bundle = (savedInstanceState == null) ? getArguments() : savedInstanceState;
        url = bundle.getString(WEB_VIEW_URL);
        title = bundle.getInt(TITLE);
    }

    @Override
    public void reloadData() {
    }


    @Override
    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().hideHeader(activity);
    }

    public void reloadHeader(Activity activity, int id) {
        DashboardHeaderStrategy.getInstance().hideHeader(activity);
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
                showHideWebView(true);
                    loadValidUrl();
            } else {
                showHideWebView(false);

                mErrorDemoText.setTextTranslation(R.string.erro_page_text);
            }
        }
    }

    private void loadValidUrl() {
        if (mWebView != null) {
            if (PreferencesState.getCredentialsFromPreferences().isDemoCredentials()) {
                showHideWebView(false);
                mErrorDemoText.setTextTranslation(R.string.demo_page_text);
                ;
            } else {
                showHideWebView(true);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.getSettings().setDomStorageEnabled(true);
                clearCookies(getActivity());
                mWebView.loadUrl(url);

                int timeoutMillis = Integer.parseInt(getString(R.string.web_view_timeout_millis));

                CustomWebViewClient customWebViewClient =
                        new CustomWebViewClient(timeoutMillis);

                customWebViewClient.setErrorListener(new CustomWebViewClient.ErrorListener() {
                    @Override
                    public void onTimeoutError() {
                        // do what you want
                        showError(R.string.web_view_network_error);
                    }
                });

                customWebViewClient.setPageFinishedListener(
                        new CustomWebViewClient.PageFinishedListener() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                loadedFirstTime = true;
                            }
                        });

                mWebView.setWebViewClient(customWebViewClient);
            }

        }
    }

    private void showHideWebView(boolean show) {
        mWebView.setVisibility(show ? View.VISIBLE : View.GONE);
        mErrorDemoText.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityStatus.isConnected(getActivity())) {
                if (!loadedFirstTime) {
                    loadValidUrl();
                }
            }
        }
    };


    public void hideHeader() {
        DashboardHeaderStrategy.getInstance().hideHeader(getActivity());
    }

    public boolean onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    public void showError(int message) {
        Toast.makeText(getActivity(), translate(message),
                Toast.LENGTH_LONG).show();
    }

    private String translate(@StringRes int id){
        return Utils.getInternationalizedString(id, getActivity());
    }
}
