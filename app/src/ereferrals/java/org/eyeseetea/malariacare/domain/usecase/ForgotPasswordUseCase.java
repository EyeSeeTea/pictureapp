package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

public class ForgotPasswordUseCase implements UseCase {

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IAuthenticationManager mAuthenticationManager;
    private java.lang.String mUsername;
    private Callback mCallback;

    public ForgotPasswordUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IAuthenticationManager authenticationManager) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mAuthenticationManager = authenticationManager;
    }

    public void execute(java.lang.String username, Callback callback) {
        mUsername = username;
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mAuthenticationManager.forgotPassword(mUsername,
                new IAuthenticationManager.Callback<ForgotPasswordMessage>() {
                    @Override
                    public void onSuccess(final ForgotPasswordMessage forgotPasswordMessage) {
                        mMainExecutor.run(new Runnable() {
                            @Override
                            public void run() {
                                mCallback.onGetForgotPasswordSuccess(
                                        forgotPasswordMessage.getMessage(),
                                        forgotPasswordMessage.getTitle());
                            }
                        });
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        if (throwable instanceof NetworkException) {
                            mMainExecutor.run(new Runnable() {
                                @Override
                                public void run() {
                                    mCallback.onNetworkError();
                                }
                            });
                        } else {
                            mMainExecutor.run(new Runnable() {
                                @Override
                                public void run() {
                                    mCallback.onError(throwable.getMessage());
                                }
                            });
                        }
                    }
                });
    }

    public interface Callback {
        void onGetForgotPasswordSuccess(String result, String title);

        void onNetworkError();

        void onError(String messages);
    }
}
