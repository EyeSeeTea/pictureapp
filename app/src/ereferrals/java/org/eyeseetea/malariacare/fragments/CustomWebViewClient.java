package org.eyeseetea.malariacare.fragments;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient {

    private final int timeoutMillis;

    boolean timeout = true;

    PageFinishedListener pageFinishedListener;
    ErrorListener errorListener;

    public CustomWebViewClient(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public void onPageStarted(final WebView view, String url, Bitmap favicon) {
        Runnable run = new Runnable() {
            public void run() {
                if (timeout && errorListener != null) {
                    view.stopLoading();
                    errorListener.onTimeoutError();
                }
            }
        };
        Handler myHandler = new Handler(Looper.myLooper());
        myHandler.postDelayed(run, timeoutMillis);

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        timeout = false;

        if (pageFinishedListener != null){
            pageFinishedListener.onPageFinished(view,url);
        }
    }


    public void setPageFinishedListener(PageFinishedListener listener){
        pageFinishedListener = listener;
    }

    public void setErrorListener(ErrorListener listener) {
        errorListener = listener;
    }

    public interface PageFinishedListener{
        void onPageFinished(WebView view, String url);
    }

    public interface ErrorListener {
        void onTimeoutError();
    }
}