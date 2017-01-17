package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;

/**
 * Use case where execute actions related when logout use case in our app not in sdk
 */
public abstract class ALogoutUseCase {
    public interface Callback{
        void onLogoutSuccess();
        void onLogoutError(String message);
    }
    protected Context context;

    public ALogoutUseCase(Context context) {
        this.context = context;
    }

    public abstract void execute(LogoutUseCase.Callback callback);
}
