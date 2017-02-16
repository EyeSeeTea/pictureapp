package org.eyeseetea.malariacare.webview;

import android.webkit.WebView;

import org.eyeseetea.malariacare.data.database.model.Survey;

import java.util.List;

/**
 * Created by idelcano on 06/12/2016.
 */

public interface IWebViewBuilder {

    /**
     * Adds surveys info into its tables
     */
    void addSurveys(List<Survey> surveys);

    /**
     * Updates the given webview with the data provided by its tables
     */
    void addDataToView(WebView webView);

    /**
     * Updates webview with i18N messages
     */
    void addMessagesToView(WebView webView);

    /**
     * Reloads tables
     */
    void addReloadTablesToView(WebView webView);
}
