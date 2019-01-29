package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.remote.model.AuthResponse;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;

import java.io.IOException;

public class AuthenticationWSDataSource implements IAuthenticationDataSource {
    eReferralsAPIClient mEReferralsAPIClient;

    public AuthenticationWSDataSource(eReferralsAPIClient eReferralsAPIClient) {
        mEReferralsAPIClient = eReferralsAPIClient;
    }

    @Override
    public void login(Credentials credentials, IDataSourceCallback<UserAccount> callback) {
        try {
            AuthResponse response = mEReferralsAPIClient.auth(credentials.getUsername(),
                    credentials.getPassword());

            if (response.isAuthorized()) {
                UserAccount userAccount = new UserAccount(
                        credentials.getUsername(), credentials.getPassword(),
                        false, true);

                callback.onSuccess(userAccount);
            } else {
                callback.onError(new InvalidCredentialsException());
            }
        } catch (IOException | AvailableApiException e) {
            callback.onError(e);
        }
    }

    @Override
    public void logout(IDataSourceCallback<Void> callback) {
        //not necessary for this moment
        callback.onSuccess(null);
    }
}
