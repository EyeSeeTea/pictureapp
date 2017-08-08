package org.eyeseetea.malariacare.data.repositories;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.data.sync.exporter.WSClient;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordResponse;
import org.eyeseetea.malariacare.domain.boundary.repositories.IForgotPasswordRepository;
import org.eyeseetea.malariacare.domain.exception.NetworkException;


public class ForgotPasswordRepository implements IForgotPasswordRepository {
    private Context mContext;

    public ForgotPasswordRepository(Context context) {
        mContext = context;
    }

    @Override
    public void getForgotPassword(java.lang.String username,
            final Callback dataSourceCallback) {
        if (!isNetworkAvailable()) {
            dataSourceCallback.onError(new NetworkException());
        }

        WSClient wsClient = new WSClient();
        wsClient.getForgotPassword(username,
                new WSClient.WSClientCallBack<ForgotPasswordResponse>() {
            @Override
            public void onSuccess(ForgotPasswordResponse result) {
                dataSourceCallback.onSuccess(result.getMessage(), result.getStatus());
            }

            @Override
            public void onError(Exception e) {

                dataSourceCallback.onError(e);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
