package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.WSClient;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordPayload;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordResponse;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

public class AuthenticationDhisSDKDataSourceStrategy implements
        IAuthenticationDhisSDKDataSourceStrategy {
    @Override
    public void forgotPassword(String username, boolean isNetworkAvailable,
            final IDataSourceCallback<ForgotPasswordMessage> callback) {
        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        }

        //TODO hardcoded language change when introduce language in all queries to WS
        ForgotPasswordPayload forgotPasswordPayload = new ForgotPasswordPayload(
                PreferencesState.getInstance().getContext().getString(
                        R.string.ws_version), username, "en");

        WSClient wsClient = new WSClient();
        wsClient.getForgotPassword(forgotPasswordPayload,
                new WSClient.WSClientCallBack<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(ForgotPasswordResponse result) {
                        callback.onSuccess(
                                new ForgotPasswordMessage(result.getStatus(), result.getMessage()));
                    }

                    @Override
                    public void onError(Exception e) {

                        callback.onError(e);
                    }
                });
    }
}
