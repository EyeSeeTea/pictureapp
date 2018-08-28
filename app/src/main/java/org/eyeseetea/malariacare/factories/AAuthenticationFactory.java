package org.eyeseetea.malariacare.factories;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.datasources.AuthenticationLocalDataSource;
import org.eyeseetea.malariacare.data.remote.AuthenticationDhisSDKDataSource;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public abstract class AAuthenticationFactory {
    protected IMainExecutor mainExecutor = new UIThreadExecutor();
    protected IAsyncExecutor asyncExecutor = new AsyncExecutor();

    public abstract LoginUseCase getLoginUseCase(Context context);

    public LogoutUseCase getLogoutUseCase(Context context) {
        IAuthenticationManager authenticationManager = getAuthenticationManager(context);

        LogoutUseCase logoutUseCase = new LogoutUseCase(authenticationManager);

        return logoutUseCase;
    }

    @NonNull
    public IAuthenticationManager getAuthenticationManager(Context context) {
        IAuthenticationDataSource userAccountLocalDataSource =
                new AuthenticationLocalDataSource(context);
        IAuthenticationDataSource userAccountRemoteDataSource =
                new AuthenticationDhisSDKDataSource(context);

        return new AuthenticationManager(context,
                userAccountLocalDataSource, userAccountRemoteDataSource);
    }
}
