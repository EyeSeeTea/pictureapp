package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;

public class AuthenticationDhisSDKDataSourceStrategy implements
        IAuthenticationDhisSDKDataSourceStrategy {
    @Override
    public void forgotPassword(String username,
            IDataSourceCallback<ForgotPasswordMessage> callback) {

    }
}
