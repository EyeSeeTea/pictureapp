package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class AuthenticationDhisSDKDataSourceStrategy extends
        AAuthenticationDhisSDKDataSourceStrategy {

    @Override
    public UserAccount createUserAccount(
            org.hisp.dhis.client.sdk.models.user.UserAccount dhisUserAccount,
            Credentials credentials) {
        UserAccount userAccount = new UserAccount(credentials.getUsername(),
                dhisUserAccount.getUId(),
                credentials.isDemoCredentials());
        if (UserDB.getLoggedUser() != null
                && !UserDB.getLoggedUser().canAddSurveys()) {
            userAccount.setCanAddSurveys(false);
        }
        return userAccount;
    }
}
