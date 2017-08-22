package org.eyeseetea.malariacare.data.authentication.strategies;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;

public abstract class AAuthenticationManagerStrategy {

    public abstract void forgotPassword(String username,
            final IAuthenticationManager.Callback<ForgotPasswordMessage> callback);
}
