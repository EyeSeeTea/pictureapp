package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;

public interface IForgotPasswordRepository {

    void getForgotPassword(String username, IDataSourceCallback<String> dataSourceCallback);
}
