package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.remote.CredentialsDataSource;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

public class LoginUseCase extends ALoginUseCase {
    private IAuthenticationManager mAuthenticationManager;
    private Credentials insertedCredentials;

    public LoginUseCase(IAuthenticationManager authenticationManager) {
        mAuthenticationManager = authenticationManager;
    }

    @Override
    public void execute(final Credentials credentials, final Callback callback) {
        insertedCredentials = credentials;
        mAuthenticationManager.hardcodedLogin(ServerAPIController.getServerUrl(),
                new IAuthenticationManager.Callback<UserAccount>() {
                    @Override
                    public void onSuccess(UserAccount userAccount) {
                        pullOrganisationCredentials(credentials, callback);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof MalformedURLException
                                || throwable instanceof UnknownHostException) {
                            callback.onServerURLNotValid();
                        } else if (throwable instanceof InvalidCredentialsException) {
                            callback.onInvalidCredentials();
                        } else if (throwable instanceof NetworkException) {
                            ICredentialsRepository
                                    credentialsLocalDataSource = new CredentialsLocalDataSource();
                            checkUserCredentialsWithOrgUnit(
                                    credentialsLocalDataSource.getCredentials(), callback);
                        }
                    }
                });

    }


    private void pullOrganisationCredentials(Credentials credentials, final Callback callback) {
        ICredentialsRepository credentialDataSource = new CredentialsDataSource();
        ICredentialsRepository
                credentialsLocalDataSource = new CredentialsLocalDataSource();
        Credentials orgUnitCredentials = null;
        try {
            orgUnitCredentials = credentialDataSource.getOrganisationCredentials(credentials);
        } catch (PullConversionException e) {
            e.printStackTrace();
            callback.onConfigJsonNotPresent();
        } catch (NetworkException e) {
            e.printStackTrace();
            callback.onNetworkError();
            checkUserCredentialsWithOrgUnit(credentialsLocalDataSource.getCredentials(), callback);
        }

        credentialsLocalDataSource.saveOrganisationCredentials(orgUnitCredentials);

        checkUserCredentialsWithOrgUnit(credentials, callback);
    }


    private void checkUserCredentialsWithOrgUnit(Credentials credentials, Callback callback) {
        if (insertedCredentials.getUsername().equals(credentials.getUsername())
                && insertedCredentials.getPassword().equals(credentials.getPassword())) {
            callback.onLoginSuccess();
        } else {
            callback.onInvalidCredentials();
        }
    }


}
