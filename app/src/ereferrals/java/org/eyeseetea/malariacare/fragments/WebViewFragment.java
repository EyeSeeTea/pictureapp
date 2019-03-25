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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.executors.IDelayedMainExecutor;
import org.eyeseetea.malariacare.domain.entity.ApiStatus;
import org.eyeseetea.malariacare.domain.usecase.GetWebAvailableUseCase;
import org.eyeseetea.malariacare.network.ConnectivityStatus;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.factory.ApiAvailabilityFactory;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;


public class WebViewFragment extends Fragment implements IDashboardFragment {
    public static final String TAG = ".WebViewFragment";
    public static final String WEB_VIEW_URL = "webViewUrl";
    public static final String TITLE = "title";
    public static final int COOL_DOWN_TIME = 60;

    private enum WebViewStatus {NOT_LOADED, SUCCESS, TIMEOUT}

    private String url;
    private int title;
    private WebView mWebView;
    private CustomTextView mErrorDemoText;
    private WebViewStatus webViewStatus;

    private IDelayedMainExecutor delayedMainExecutor;

    private FloatingActionButton refreshButton;
    private TextView countdownTextView;
    private int activateRefreshTimeLeft = COOL_DOWN_TIME;

    private boolean isVisible = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {

        webViewStatus = WebViewStatus.NOT_LOADED;

        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_web_view,
                container, false);
        delayedMainExecutor = new UIThreadExecutor();
        manageBundle(savedInstanceState);
        initViews(view);

        return view;
    }

    @Override
    public void onStart() {
        getActivity().registerReceiver(connectionReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onStart();

        Log.d(TAG, "onStart url: " + url);
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume url: " + url);
    }

    public void changeVisibility(boolean isVisible) {
        Log.d(TAG, "changeVisibility to " + isVisible + " onResume url: " + url);
        this.isVisible = isVisible;
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(connectionReceiver);
        super.onStop();
    }

    private void initViews(View view) {
        mWebView = (WebView) view.findViewById(R.id.web_view);
        mErrorDemoText = (CustomTextView) view.findViewById(R.id.error_demo_text);
        loadUrlInWebView(false);

        countdownTextView =  view.findViewById(R.id.countdown_text_view);
        refreshButton = view.findViewById(R.id.refresh_button);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshWebView();
            }
        });
    }

    private void refreshWebView() {
        webViewStatus = WebViewStatus.NOT_LOADED;
        mWebView.loadUrl("about:blank");
        loadUrlInWebView(true);

        disableRefreshWebView();
    }

    private void disableRefreshWebView() {

        refreshButton.setEnabled(false);
        refreshButton.setImageDrawable(null);

        activateRefreshTimeLeft = COOL_DOWN_TIME;
        countdownTextView.setText(String.valueOf(activateRefreshTimeLeft));
        countdownTextView.setVisibility(View.VISIBLE);

        executeCountdown();
    }

    private void executeCountdown() {
        delayedMainExecutor.postDelayed(new Runnable() {
            @Override
            public void run() {

                activateRefreshTimeLeft -= 1;
                countdownTextView.setText(String.valueOf(activateRefreshTimeLeft));

                if (activateRefreshTimeLeft <= 0) {
                    countdownTextView.setVisibility(View.GONE);
                    refreshButton.setEnabled(true);
                    refreshButton.setImageDrawable(
                            ContextCompat.getDrawable(getActivity(), R.drawable.ic_refresh_webview));
                } else {
                    executeCountdown();
                }
            }
        }, 1000);
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

    public String getUrl() {
        return url;
    }

    private void loadUrlInWebView(boolean shouldShowError) {
        if (mWebView != null) {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(
                    Activity.CONNECTIVITY_SERVICE);
            if (cm != null && cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isConnected()) {
                showHideWebView(true);
                    loadValidUrl(shouldShowError);
            } else {
                showHideWebView(false);

                mErrorDemoText.setTextTranslation(R.string.erro_page_text);
            }
        }
    }

    private void loadValidUrl(final boolean shouldShowError) {
        if (mWebView != null) {
            if (PreferencesState.getCredentialsFromPreferences().isDemoCredentials()) {
                showHideWebView(false);
                mErrorDemoText.setTextTranslation(R.string.demo_page_text);
            } else {
                new ApiAvailabilityFactory().getGetWebViewAvailableUseCase().execute(
                        new GetWebAvailableUseCase.Callback() {
                            @Override
                            public void onSuccess() {
                                executeOnWebAvailable();
                            }

                            @Override
                            public void onError(ApiStatus apiStatus) {
                                if(shouldShowError) {
                                    showApiNotAvailableError(apiStatus.getMessage());
                                }
                            }
                        });

            }

        }
    }

    private void showApiNotAvailableError(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void executeOnWebAvailable() {
        Log.d(TAG, "loading url: " + url);
        showHideWebView(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        clearCookies(getActivity());
        mWebView.loadUrl(url);

        int timeoutMillis = Integer.parseInt(getString(R.string.web_view_timeout_millis));

        CustomWebViewClient customWebViewClient =
                new CustomWebViewClient(timeoutMillis) {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.d(TAG, "onPageFinished url: " + url);

                        if (webViewStatus == WebViewStatus.NOT_LOADED) {
                            webViewStatus = WebViewStatus.SUCCESS;
                        }

                        super.onPageFinished(view, url);
                    }
                };

        customWebViewClient.setErrorListener(new CustomWebViewClient.ErrorListener() {
            @Override
            public void onTimeoutError() {
                webViewStatus = WebViewStatus.TIMEOUT;
                Log.d(TAG, "onTimeoutError url: " + url);
                if (isVisible) {
                    // do what you want
                    showError(R.string.web_view_network_error);
                    Log.d(TAG, "showing onTimeoutError url: " + url);
                } else {
                    Log.d(TAG, "does not show onTimeoutError because fragment is not visible url: " + url);
                }
            }
        });

        mWebView.setWebViewClient(customWebViewClient);
    }

    private void showHideWebView(boolean show) {
        mWebView.setVisibility(show ? View.VISIBLE : View.GONE);
        mErrorDemoText.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityStatus.isConnected(getActivity())) {
                if (webViewStatus != WebViewStatus.SUCCESS ) {
                    loadValidUrl(true);
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
