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
package org.eyeseetea.malariacare.monitor;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import org.eyeseetea.malariacare.database.model.Survey;

import java.util.List;

/**
 * Calculates and holds info related to a table in the monitor section
 * Created by arrizabalaga on 25/02/16.
 */
public abstract class MonitorTableBuilder {
    private static final String TAG=".MonitorTableBuilder";

    /**
     * Javascript that adds a new table to the monitor
     */
    private static final String JAVASCRIPT_ADD_TABLE="javascript:monitor.addTable(\"%s\")";

    /**
     * Javascript that adds a new row to the table
     */
    private static final String JAVASCRIPT_ADD_ROW="javascript:monitor.addRow(\"%s\",%s)";

    /**
     * Reference to the context to translate strings
     */
    protected Context context;

    /**
     * Title of this table
     */
    private String tableTitle;

    /**
     * List of rows that make the monitor
     */
    List<MonitorRowBuilder> rowBuilders;

    protected MonitorTableBuilder(Context context, String tableTitle){
        this.context=context;
        this.tableTitle=tableTitle;
        rowBuilders=defineRowBuilders();
    }

    /**
     * Each table defines its own rows with their own politics
     * @return A List of rowBuilders
     */
    protected abstract List<MonitorRowBuilder> defineRowBuilders();

    /**
     * Updates table info with the survey
     * @param survey
     */
    public void addSurvey(Survey survey){
        for(MonitorRowBuilder rowBuilder:rowBuilders){
            rowBuilder.addSurvey(survey);
        }
    }

    /**
     * Updates the given webview with the data provided by its tables
     * @param webView
     */
    public void addDataToView(WebView webView){
        addTableToView(webView);
        for(MonitorRowBuilder rowBuilder:rowBuilders){
            addRowToView(webView,rowBuilder);
        }
    }

    /**
     * Adds a new table to the view
     * @param webView
     */
    private void addTableToView(WebView webView){
        String addTableJS=String.format(JAVASCRIPT_ADD_TABLE,this.tableTitle);
        Log.d(TAG, addTableJS);
        webView.loadUrl(addTableJS);
    }


    /**
     * Adds a new row to the table
     * @param webView
     */
    private void addRowToView(WebView webView,MonitorRowBuilder rowBuilder){
        String rowJSON=rowBuilder.getRowAsJSON();
        String addRowJS=String.format(JAVASCRIPT_ADD_ROW, this.tableTitle, rowJSON);
        Log.d(TAG, addRowJS);
        webView.loadUrl(addRowJS);
    }

}
