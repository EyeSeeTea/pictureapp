package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;

public class AuthenticationFactoryStrategy extends AAuthenticationFactory {

    @Override
    public LoginUseCase getLoginUseCase(Context context){
        IAuthenticationManager authenticationManager = getAuthenticationManager(context);

        LoginUseCase loginUseCase =
                new LoginUseCase(authenticationManager);

        return loginUseCase;
    }
}
