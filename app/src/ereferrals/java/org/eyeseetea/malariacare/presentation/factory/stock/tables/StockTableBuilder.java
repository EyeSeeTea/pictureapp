package org.eyeseetea.malariacare.presentation.factory.stock.tables;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.stock.StockRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.rows.AvailableRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.rows.BalanceRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.rows.DrugsRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.rows.ReceiptsRowBuildder;
import org.eyeseetea.malariacare.presentation.factory.stock.rows.UsedTodayRowBuilder;

import java.util.ArrayList;
import java.util.List;

public class StockTableBuilder {

    private static final String TAG = ".StockTableBuilder";

    /**
     * Javascript that adds a new table to the stock
     */
    private static final String JAVASCRIPT_ADD_TABLE = "javascript:statistics.addTable(\"%s\")";

    /**
     * Javascript that adds a new row to the table
     */
    private static final String JAVASCRIPT_ADD_ROW = "javascript:statistics.addRow(\"%s\",%s)";

    /**
     * Reference to the context to translate strings
     */
    protected Context context;
    /**
     * List of rows that make the stock
     */
    List<StockRowBuilder> rowBuilders;
    /**
     * Title of this table
     */
    private String tableTitle;

    public StockTableBuilder(Context context, String tableTitle) {
        this.context = context;
        this.tableTitle = tableTitle;
        rowBuilders = defineRowBuilders();
    }

    protected List<StockRowBuilder> defineRowBuilders() {
        List<StockRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new DrugsRowBuilder("", context));
        rowBuilders.add(new BalanceRowBuilder(context));
        rowBuilders.add(new ReceiptsRowBuildder(context));
        rowBuilders.add(new UsedTodayRowBuilder(context));
        rowBuilders.add(new AvailableRowBuilder(context));
        return rowBuilders;

    }

    /**
     * Updates table info with the survey
     */
    public void addSurvey(Survey survey) {
        for (StockRowBuilder rowBuilder : rowBuilders) {
            rowBuilder.addSurvey(survey);
        }
    }

    /**
     * Updates the given webview with the data provided by its tables
     */
    public void addDataToView(WebView webView) {
        addTableToView(webView);
        for (StockRowBuilder rowBuilder : rowBuilders) {
            addRowToView(webView, rowBuilder);
        }
    }

    /**
     * Adds a new table to the view
     */
    private void addTableToView(WebView webView) {
        String addTableJS = String.format(JAVASCRIPT_ADD_TABLE, this.tableTitle);
        Log.d(TAG, addTableJS);
        webView.loadUrl(addTableJS);
    }


    /**
     * Adds a new row to the table
     */
    private void addRowToView(WebView webView, StockRowBuilder rowBuilder) {
        String rowJSON = rowBuilder.getRowAsJSON();
        String addRowJS = String.format(JAVASCRIPT_ADD_ROW, this.tableTitle, rowJSON);
        Log.d(TAG, addRowJS);
        webView.loadUrl(addRowJS);
    }


}
