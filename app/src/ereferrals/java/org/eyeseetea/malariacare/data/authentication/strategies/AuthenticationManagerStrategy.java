package org.eyeseetea.malariacare.data.authentication.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.datasources.CurrentLanguageDataSource;
import org.eyeseetea.malariacare.data.remote.ForgotPasswordDataSource;
import org.eyeseetea.malariacare.data.remote.IForgotPasswordDataSource;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICurrentLanguageRepository;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;


public class AuthenticationManagerStrategy extends AAuthenticationManagerStrategy {
    IForgotPasswordDataSource mForgotPasswordDataSource;
    ICurrentLanguageRepository mCurrentLanguageRepository;

    public AuthenticationManagerStrategy(Context context) {
        mForgotPasswordDataSource = new ForgotPasswordDataSource(context);
        mCurrentLanguageRepository = new CurrentLanguageDataSource();
    }

    @Override
    public void forgotPassword(final String username,
            final IAuthenticationManager.Callback<ForgotPasswordMessage> callback) {
        mCurrentLanguageRepository.getCurrentLanguage(new IDataSourceCallback<String>() {
            @Override
            public void onSuccess(String language) {
                mForgotPasswordDataSource.forgotPassword(username, language,
                        new IDataSourceCallback<ForgotPasswordMessage>() {
                            @Override
                            public void onSuccess(ForgotPasswordMessage result) {
                                callback.onSuccess(result);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                callback.onError(throwable);
                            }
                        });
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });

    }
}
