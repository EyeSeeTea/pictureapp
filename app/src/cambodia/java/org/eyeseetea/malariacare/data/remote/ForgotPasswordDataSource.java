package org.eyeseetea.malariacare.data.remote;

import android.content.Context;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;

public class ForgotPasswordDataSource implements IForgotPasswordDataSource {
    Context mContext;

    public ForgotPasswordDataSource(Context context) {
        mContext = context;
    }

    @Override
    public void forgotPassword(String username, String language,
            IDataSourceCallback<ForgotPasswordMessage> callback) {

    }
}