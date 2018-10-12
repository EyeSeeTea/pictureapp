package org.eyeseetea.malariacare.factories;

import android.content.Context;

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

    public abstract IAuthenticationManager getAuthenticationManager(Context context);

    public LogoutUseCase getLogoutUseCase(Context context) {
        IAuthenticationManager authenticationManager = getAuthenticationManager(context);

        LogoutUseCase logoutUseCase = new LogoutUseCase(authenticationManager);

        return logoutUseCase;
    }
}
