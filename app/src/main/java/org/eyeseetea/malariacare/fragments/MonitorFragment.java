/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorBuilder;
import org.eyeseetea.malariacare.services.MonitorService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.webview.IWebView;
import org.eyeseetea.malariacare.webview.IWebViewBuilder;


/**
 * Activity that shows summary info related to the surveys that have been sent
 *
 * @author ivan.arrizabalaga
 */
public class MonitorFragment extends Fragment implements IDashboardFragment, IWebView {

    public static final String TAG = ".MonitorFragment";
    /**
     * Local monitor html
     */
    public static final String FILE_ANDROID_ASSET_MONITOR_MONITOR_HTML =
            "file:///android_asset/monitor/monitor.html";

    /**
     * Reference to webview ui
     */
    private WebView webView;

    /**
     * Monitor receiver to reload monitor view after calculation
     */
    private MonitorReceiver monitorReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (container == null) {
            return null;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        //Listen for data
        registerFragmentReceiver();

        //Ask for data
        Intent surveysIntent = new Intent(getActivity().getApplicationContext(),
                MonitorService.class);
        surveysIntent.putExtra(MonitorService.SERVICE_METHOD, MonitorService.PREPARE_MONITOR_DATA);
        getActivity().startService(surveysIntent);

        //Go on with resume
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        unregisterFragmentReceiver();
        stopWebView();
        super.onStop();
    }

    /**
     * Register a monitor receiver to load monitor data into webview
     */
    public void registerFragmentReceiver() {
        Log.d(TAG, "initializeSurvey");

        if (monitorReceiver == null) {
            monitorReceiver = new MonitorReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(monitorReceiver,
                    new IntentFilter(MonitorService.PREPARE_MONITOR_DATA));
        }
    }

    /**
     * Unregisters the monitor receiver.
     * It really important to do this, otherwise each receiver will invoke its code.
     */
    public void unregisterFragmentReceiver() {
        if (monitorReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(monitorReceiver);
            monitorReceiver = null;
        }
    }

    public void reloadWebView(final IWebViewBuilder iWebViewBuilder) {
        initWebView();
        //onPageFinish load data
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                iWebViewBuilder.addDataToView(webView);
            }
        });
        //Load html
        webView.loadUrl(FILE_ANDROID_ASSET_MONITOR_MONITOR_HTML);
    }

    public WebView initWebView() {
        webView = (WebView) getActivity().findViewById(R.id.dashboard_monitor);
        //Init webView settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        webView.getSettings().setJavaScriptEnabled(true);

        return webView;
    }

    /**
     * Stops webView gracefully
     */
    public void stopWebView() {
        if (webView != null) {
            webView.stopLoading();
            webView = null;
        }
    }

    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().init(activity, R.string.tab_tag_monitor);
    }

    /**
     * load and reload sent surveys
     */
    public void reloadData() {
        //Reload data using service
        Intent surveysIntent = new Intent(
                PreferencesState.getInstance().getContext().getApplicationContext(),
                MonitorService.class);
        surveysIntent.putExtra(MonitorService.SERVICE_METHOD, MonitorService.PREPARE_MONITOR_DATA);
        PreferencesState.getInstance().getContext().getApplicationContext().startService(
                surveysIntent);
    }

    /**
     * Inner private class that receives the result from the service
     */
    private class MonitorReceiver extends BroadcastReceiver {
        private MonitorReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            //Listening only intents from this method
            if (MonitorService.PREPARE_MONITOR_DATA.equals(intent.getAction())) {
                MonitorBuilder monitorBuilder;
                Session.valuesLock.readLock().lock();
                try {
                    monitorBuilder = (MonitorBuilder) Session.popServiceValue(
                            MonitorService.PREPARE_MONITOR_DATA);
                } finally {
                    Session.valuesLock.readLock().unlock();
                }
                reloadWebView(monitorBuilder);
            }
        }
    }
}
