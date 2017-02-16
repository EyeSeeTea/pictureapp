package org.eyeseetea.malariacare.data;

import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public interface IAuthenticationDataSource {
    void login(Credentials credentials, IDataSourceCallback<UserAccount> callback);
    void logout(IDataSourceCallback<Void> callback);
}
