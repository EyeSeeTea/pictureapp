package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;

import org.eyeseetea.malariacare.domain.entity.Credentials;

/**
 * Use case where execute actions related when login use case in our app not in sdk
 */
public abstract class ALoginUseCase {
    public interface Callback {
        void onLoginSuccess();
        void onLoginError(String message);
    }

    protected Context context;

    public ALoginUseCase(Context context) {
        this.context = context;
    }

    public abstract void execute(Credentials credentials,Callback callback);

    public abstract boolean isLogoutNeeded(Credentials credentials);
}
