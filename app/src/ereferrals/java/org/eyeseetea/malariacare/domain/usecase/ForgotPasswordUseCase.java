package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IForgotPasswordRepository;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

public class ForgotPasswordUseCase implements UseCase {

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IForgotPasswordRepository mForgotPasswordRepository;
    private String mUsername;
    private Callback mCallback;

    public ForgotPasswordUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IForgotPasswordRepository forgotPasswordRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mForgotPasswordRepository = forgotPasswordRepository;
    }

    public void execute(String username, Callback callback) {
        mUsername = username;
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mForgotPasswordRepository.getForgotPassword(mUsername, new IDataSourceCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mMainExecutor.run(new Runnable() {
                    @Override
                    public void run() {
                        mCallback.onGetForgotPasswordSuccess();
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
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
                            mCallback.onInvalidUsername();
                        }
                    });
                }
            }
        });
    }

    public interface Callback {
        void onGetForgotPasswordSuccess();

        void onInvalidUsername();

        void onNetworkError();
    }
}
