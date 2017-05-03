package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.remote.CredentilasDataSource;
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

    public LoginUseCase(IAuthenticationManager authenticationManager) {
        mAuthenticationManager = authenticationManager;
    }

    @Override
    public void execute(final Credentials credentials, final Callback callback) {
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
                            CheckCredentialsWithOrgUnitUseCase checkCredentialsWithOrgUnitUseCase =
                                    new CheckCredentialsWithOrgUnitUseCase();
                            checkCredentialsWithOrgUnitUseCase.execute(
                                    PreferencesEReferral.getUserCredentialsFromPreferences(),
                                    new CheckCredentialsWithOrgUnitUseCase.Callback() {
                                        @Override
                                        public void onCorrectCredentials() {
                                            callback.onLoginSuccess();
                                        }

                                        @Override
                                        public void onBadCredentials() {
                                            callback.onNetworkError();
                                        }
                                    });
                        }
                    }
                });

    }


    private void pullOrganisationCredentials(Credentials credentials, final Callback callback) {
        ICredentialsRepository credentialDataSource = new CredentilasDataSource();
        Credentials orgUnitCredentials = null;
        try {
            orgUnitCredentials = credentialDataSource.getOrganisationCredentials(credentials);
        } catch (PullConversionException e) {
            e.printStackTrace();
            callback.onConfigJsonNotPresent();
        } catch (NetworkException e) {
            e.printStackTrace();
            callback.onNetworkError();
            //TODO check credentials in local
        }
        ICredentialsRepository credentialsLocalDataSource = new CredentialsLocalDataSource();
        credentialsLocalDataSource.saveOrganisationCredentials(orgUnitCredentials);

        callback.onLoginSuccess();
    }
}
