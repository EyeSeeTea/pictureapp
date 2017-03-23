package org.eyeseetea.malariacare.presentation.factory.stock;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.stock.tables.StockTableBuilder;
import org.eyeseetea.malariacare.webview.IWebViewBuilder;

import java.util.List;

public class StockBuilder implements IWebViewBuilder {
    private static final String TAG = ".StockBuilder";

    private static final String JAVASCRIPT_UPDATE_MESSAGES =
            "javascript:statistics.updateMessages(%s)";
    /**
     * Inject javascript to reload tables
     */
    private static final String JAVASCRIPT_RELOAD_TABLES = "javascript:statistics.drawTables()";
    /**
     * The table of the stock tab
     */
    private StockTableBuilder mStockTableBuilder;
    /**
     * The context of the app
     */
    private Context mContext;

    public StockBuilder(Context context) {
        mContext = context;
        //TODO add table title
        mStockTableBuilder = new StockTableBuilder(context, "");
    }

    /**
     * Adds surveys info into its tables
     */
    @Override
    public void addSurveys(List<Survey> surveys) {
        for (Survey survey : surveys) {
            mStockTableBuilder.addSurvey(survey);
        }

    }

    /**
     * Updates the given webview with the data provided by its tables
     */
    @Override
    public void addDataToView(WebView webView) {
        //add i18n messages to webview interface
        addMessagesToView(webView);

        mStockTableBuilder.addDataToView(webView);

        //reload tables
        addReloadTablesToView(webView);
    }

    @Override
    public void addMessagesToView(WebView webView) {

    }

    @Override
    public void addReloadTablesToView(WebView webView) {
        Log.d(TAG, JAVASCRIPT_RELOAD_TABLES);
        webView.loadUrl(JAVASCRIPT_RELOAD_TABLES);
    }
}
