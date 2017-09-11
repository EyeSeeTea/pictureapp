package org.eyeseetea.malariacare.webview;

import android.webkit.WebView;

/**
 * Created by idelcano on 06/12/2016.
 */

public interface IWebView {

    void reloadWebView(final IWebViewBuilder iWebViewBuilder);

    WebView initWebView();

    void stopWebView();
}
