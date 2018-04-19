package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class AAuthenticationDhisSDKDataSourceStrategy {

    public UserAccount createUserAccount(
            org.hisp.dhis.client.sdk.models.user.UserAccount dhisUserAccount,
            Credentials credentials) {
        UserAccount userAccount = new UserAccount(credentials.getUsername(),
                dhisUserAccount.getUId(),
                credentials.isDemoCredentials());
        return userAccount;
    }
}
