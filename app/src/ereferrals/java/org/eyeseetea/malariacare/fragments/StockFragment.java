package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.presentation.factory.stock.StockBuilder;
import org.eyeseetea.malariacare.services.StockService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.webview.IWebView;
import org.eyeseetea.malariacare.webview.IWebViewBuilder;

/**
 * Created by idelcano on 29/11/2016.
 */

public class StockFragment extends Fragment implements IDashboardFragment, IWebView {

    public static final String TAG = ".StockFragment";
    /**
     * Local stock html
     */
    public static final String FILE_ANDROID_ASSET_STOCK_HTML =
            "file:///android_asset/stock/stock.html";
    /**
     * Reference to webview ui
     */
    private WebView table;

    private StockReceiver mStockReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_stock,
                container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        table = (WebView) view.findViewById(R.id.fragment_stock_table);

        view.findViewById(R.id.fragment_stock_new_receipt).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewReceiptBalanceFragment(Constants.SURVEY_RECEIPT);
            }
        });
        view.findViewById(R.id.fragment_stock_new_balance).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNewReceiptBalanceFragment(Constants.SURVEY_RESET);
                    }
                });

        view.findViewById(R.id.fragment_stock_see_balance).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showHistoricReceiptBalanceFragment(Constants.SURVEY_RESET);
                    }
                });
        view.findViewById(R.id.fragment_stock_see_receipt).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showHistoricReceiptBalanceFragment(Constants.SURVEY_RECEIPT);
                    }
                });
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
                StockService.class);
        surveysIntent.putExtra(StockService.SERVICE_METHOD, StockService.PREPARE_STOCK_DATA);
        getActivity().startService(surveysIntent);

        super.onResume();

    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        unregisterFragmentReceiver();
        stopWebView();
        super.onStop();
    }


    @Override
    public void registerFragmentReceiver() {
        Log.d(TAG, "initializeSurvey");

        if (mStockReceiver == null) {
            mStockReceiver = new StockReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mStockReceiver,
                    new IntentFilter(StockService.PREPARE_STOCK_DATA));
        }

    }

    @Override
    public void unregisterFragmentReceiver() {
        if (mStockReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mStockReceiver);
            mStockReceiver = null;
        }
    }


    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().init(activity, R.string.tab_tag_stock);
    }

    /**
     * load and reload sent surveys
     */
    public void reloadData() {
        //Reload data using service
        Intent surveysIntent = new Intent(
                PreferencesState.getInstance().getContext().getApplicationContext(),
                StockService.class);
        surveysIntent.putExtra(StockService.SERVICE_METHOD, StockService.PREPARE_STOCK_DATA);
        PreferencesState.getInstance().getContext().getApplicationContext().startService(
                surveysIntent);
    }


    @Override
    public void reloadWebView(final IWebViewBuilder iWebViewBuilder) {
        initWebView();
        //onPageFinish load data
        table.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                iWebViewBuilder.addDataToView(table);
            }
        });
        //Load html
        table.loadUrl(FILE_ANDROID_ASSET_STOCK_HTML);
    }

    @Override
    public WebView initWebView() {
        //Init webView settings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            table.getSettings().setAllowUniversalAccessFromFileURLs(true);
            table.getSettings().setAllowFileAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        table.getSettings().setJavaScriptEnabled(true);

        return table;
    }

    @Override
    public void stopWebView() {
        try {
            if (table != null) {
                table.stopLoading();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner private class that receives the result from the service
     */
    private class StockReceiver extends BroadcastReceiver {
        private StockReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            //Listening only intents from this method
            if (StockService.PREPARE_STOCK_DATA.equals(intent.getAction())) {
                StockBuilder stockBuilder;
                Session.valuesLock.readLock().lock();
                try {
                    stockBuilder = (StockBuilder) Session.popServiceValue(
                            StockService.PREPARE_STOCK_DATA);
                } finally {
                    Session.valuesLock.readLock().unlock();
                }
                reloadWebView(stockBuilder);
            }
        }
    }


    private void showNewReceiptBalanceFragment(int type) {
        Activity activity = getActivity();
        if (activity != null) {
            NewReceiptBalanceFragment newReceiptBalanceFragment = new NewReceiptBalanceFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(NewReceiptBalanceFragment.TYPE, type);
            newReceiptBalanceFragment.setArguments(bundle);
            replaceFragment(R.id.dashboard_stock_container, newReceiptBalanceFragment);

            int headerString=R.string.fragment_new_receipt;
            if (type == Constants.SURVEY_RESET) {
                headerString=R.string.fragment_new_reset;
            }
            DashboardHeaderStrategy.getInstance().init(activity, headerString);
            if (activity instanceof DashboardActivity) {
                ((DashboardActivity) activity).initNewReceiptFragment();
            }
        }
    }

    private void showHistoricReceiptBalanceFragment(int type) {
        Activity activity = getActivity();
        if (activity != null) {
            HistoricReceiptBalanceFragment historicReceiptBalanceFragment =
                    new HistoricReceiptBalanceFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(HistoricReceiptBalanceFragment.TYPE, type);
            historicReceiptBalanceFragment.setArguments(bundle);
            replaceFragment(R.id.dashboard_stock_container, historicReceiptBalanceFragment);

            int headerString=R.string.fragment_historic_receipt_balance;
            if (type == Constants.SURVEY_RESET) {
                headerString=R.string.fragment_historic_reset;
            }
            DashboardHeaderStrategy.getInstance().init(activity,headerString);
            if (activity instanceof DashboardActivity) {
                ((DashboardActivity) activity).initNewReceiptFragment();
            }
        }
    }


    private void replaceFragment(int layout, Fragment fragment) {
        Activity activity = getActivity();
        if (activity != null) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.replace(layout, fragment);
            ft.commit();
        }
    }

}