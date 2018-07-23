package org.eyeseetea.malariacare.data.authentication.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;

public class AuthenticationManagerStrategy extends AAuthenticationManagerStrategy {

    public AuthenticationManagerStrategy(Context context) {
    }

    @Override
    public void forgotPassword(final String username,
            final IAuthenticationManager.Callback<ForgotPasswordMessage> callback) {
    }
}
