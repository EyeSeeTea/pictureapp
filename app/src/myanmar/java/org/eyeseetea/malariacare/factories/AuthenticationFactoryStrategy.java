package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.datasources.AuthenticationLocalDataSource;
import org.eyeseetea.malariacare.data.remote.AuthenticationDhisSDKDataSource;
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

    @Override
    public IAuthenticationManager getAuthenticationManager(Context context) {
        IAuthenticationDataSource userAccountLocalDataSource =
                new AuthenticationLocalDataSource(context);
        IAuthenticationDataSource userAccountRemoteDataSource =
                new AuthenticationDhisSDKDataSource(context);

        return new AuthenticationManager(context,
                userAccountLocalDataSource, userAccountRemoteDataSource);
    }
}
