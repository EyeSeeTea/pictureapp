package org.eyeseetea.malariacare.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.WSClient;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordPayload;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordResponse;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

public class ForgotPasswordDataSource implements IForgotPasswordDataSource {
    Context mContext;

    public ForgotPasswordDataSource(Context context) {
        mContext = context;
    }

    @Override
    public void forgotPassword(String username, String language,
            final IDataSourceCallback<ForgotPasswordMessage> callback) {
        {
            if (!isNetworkAvailable()) {
                callback.onError(new NetworkException());
            }

            ForgotPasswordPayload forgotPasswordPayload = new ForgotPasswordPayload(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.ws_version), username, language);

            WSClient wsClient = new WSClient();
            wsClient.getForgotPassword(forgotPasswordPayload,
                    new WSClient.WSClientCallBack<ForgotPasswordResponse>() {
                        @Override
                        public void onSuccess(ForgotPasswordResponse result) {
                            callback.onSuccess(
                                    new ForgotPasswordMessage(result.getStatus(),
                                            result.getMessage()));
                        }

                        @Override
                        public void onError(Exception e) {

                            callback.onError(e);
                        }
                    });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
