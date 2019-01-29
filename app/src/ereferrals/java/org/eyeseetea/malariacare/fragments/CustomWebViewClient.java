package org.eyeseetea.malariacare.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.utils.Utils;

public class CustomWebViewClient extends WebViewClient {

    private final Context context;
    private final int timeoutMillis;


    boolean timeout = true;

    PageFinishedListener pageFinishedListener;

    public CustomWebViewClient(Context context, int timeoutMillis) {
        this.context = context;
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Runnable run = new Runnable() {
            public void run() {
                if (timeout) {
                    // do what you want
                    showError(R.string.web_view_timeout_error);
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

    public void showError(int message) {
        Toast.makeText(context, translate(message),
                Toast.LENGTH_LONG).show();
    }

    public String translate(@StringRes int id) {
        return Utils.getInternationalizedString(id, context);
    }

    public void setPageFinishedListener(PageFinishedListener listener){
        pageFinishedListener = listener;
    }

    public interface PageFinishedListener{
        void onPageFinished(WebView view, String url);
    }
}