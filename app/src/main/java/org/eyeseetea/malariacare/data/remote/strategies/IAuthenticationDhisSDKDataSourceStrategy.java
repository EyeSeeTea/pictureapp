package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;

public interface IAuthenticationDhisSDKDataSourceStrategy {
    void forgotPassword(String username, boolean isNetworkAvailable,
            IDataSourceCallback<ForgotPasswordMessage> callback);
}
