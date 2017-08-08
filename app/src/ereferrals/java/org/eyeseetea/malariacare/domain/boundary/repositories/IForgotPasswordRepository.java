package org.eyeseetea.malariacare.domain.boundary.repositories;

public interface IForgotPasswordRepository {

    void getForgotPassword(String username, Callback dataSourceCallback);

    interface Callback {
        void onSuccess(String result, String title);

        void onError(Throwable throwable);
    }
}
