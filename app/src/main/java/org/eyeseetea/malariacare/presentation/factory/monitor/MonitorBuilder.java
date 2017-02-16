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
package org.eyeseetea.malariacare.presentation.factory.monitor;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.monitor.tables.ConsumptionTableBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.tables.SuspectedPositiveTableBuilder;
import org.eyeseetea.malariacare.webview.IWebViewBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arrizabalaga on 25/02/16.
 */
public class MonitorBuilder implements IWebViewBuilder {
    private static final String TAG = ".MonitorBuilder";

    /**
     * Inject javascript to update i18n messages:
     *
     * monitor.updateMessages({
     * "LBL_TIMEUNIT":"Select last 6",
     * "OPT_MONTHS":" months",
     * "OPT_WEEKS":" lalala",
     * "OPT_DAYS":" days"
     * })
     */
    private static final String JAVASCRIPT_UPDATE_MESSAGES =
            "javascript:statistics.updateMessages(%s)";

    /**
     * Inyect javascript to reload tables
     */
    private static final String JAVASCRIPT_RELOAD_TABLES = "javascript:statistics.drawTables()";

    /**
     * JSON map with pairs to translate webview interface
     */
    private static final String UPDATE_MESSAGES_JSON =
            "{\"LBL_TIMEUNIT\":\"%s\",\"OPT_MONTHS\":\"%s\",\"OPT_WEEKS\":\"%s\",\"OPT_DAYS\":\" "
                    + "%s\"}";

    /**
     * List of tables that make the monitor
     */
    List<MonitorTableBuilder> tableBuilders;

    /**
     * Context to resolve strings whenever required
     */
    Context context;

    public MonitorBuilder(Context context) {
        this.context = context;
        tableBuilders = new ArrayList<>();
        tableBuilders.add(new SuspectedPositiveTableBuilder(this.context));
        tableBuilders.add(new ConsumptionTableBuilder(this.context));
    }

    /**
     * Adds surveys info into its tables
     */
    public void addSurveys(List<Survey> surveys) {
        //Each survey updates...
        for (Survey survey : surveys) {
            //Each table
            for (MonitorTableBuilder tableBuilder : tableBuilders) {
                tableBuilder.addSurvey(survey);
            }
        }
    }

    /**
     * Updates the given webview with the data provided by its tables
     */
    public void addDataToView(WebView webView) {
        //add i18n messages to webview interface
        addMessagesToView(webView);

        //add each table
        for (MonitorTableBuilder tableBuilder : tableBuilders) {
            tableBuilder.addDataToView(webView);
        }

        //reload tables
        addReloadTablesToView(webView);
    }

    /**
     * Updates webview with i18N messages
     */
    public void addMessagesToView(WebView webView) {
        String json = String.format(UPDATE_MESSAGES_JSON,
                context.getString(R.string.monitoring_title_dropdown),
                context.getString(R.string.monitoring_label_option_months),
                context.getString(R.string.monitoring_label_option_weeks),
                context.getString(R.string.monitoring_label_option_days));

        //Inyect in browser
        String updateMessagesJS = String.format(JAVASCRIPT_UPDATE_MESSAGES, json);
        Log.d(TAG, updateMessagesJS);
        webView.loadUrl(updateMessagesJS);
    }

    /**
     * Reloads tables
     */
    public void addReloadTablesToView(WebView webView) {
        Log.d(TAG, JAVASCRIPT_RELOAD_TABLES);
        webView.loadUrl(JAVASCRIPT_RELOAD_TABLES);
    }

}
