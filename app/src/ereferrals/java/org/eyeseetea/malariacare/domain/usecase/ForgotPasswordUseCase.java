package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IForgotPasswordRepository;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

public class ForgotPasswordUseCase implements UseCase {

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IForgotPasswordRepository mForgotPasswordRepository;
    private java.lang.String mUsername;
    private Callback mCallback;

    public ForgotPasswordUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IForgotPasswordRepository forgotPasswordRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mForgotPasswordRepository = forgotPasswordRepository;
    }

    public void execute(java.lang.String username, Callback callback) {
        mUsername = username;
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mForgotPasswordRepository.getForgotPassword(mUsername,
                new IForgotPasswordRepository.Callback() {
            @Override
            public void onSuccess(final String result, final String title) {
                mMainExecutor.run(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onGetForgotPasswordSuccess(result, title);
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
