package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.entity.Credentials;

/**
 * Use case where execute actions related when login use case in our app not in sdk
 */
public abstract class ALoginUseCase {
    public interface Callback {
        void onLoginSuccess();

        void onServerURLNotValid();

        void onInvalidCredentials();

        void onNetworkError();

        void onConfigJsonInvalid();

        void onUnexpectedError();

        void onMaxLoginAttemptsReachedError();
    }

    public abstract void execute(Credentials credentials, Callback callback);
}
