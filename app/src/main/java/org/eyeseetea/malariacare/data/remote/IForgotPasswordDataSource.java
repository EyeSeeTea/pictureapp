package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.entity.ForgotPasswordMessage;

public interface IForgotPasswordDataSource {
    void forgotPassword(String username, String language,
            IDataSourceCallback<ForgotPasswordMessage> callback);
}
