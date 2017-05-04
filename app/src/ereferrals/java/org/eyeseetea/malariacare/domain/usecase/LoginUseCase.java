package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.remote.OrgUnitDataSource;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrgUnitRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.OrgUnit;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

import java.net.MalformedURLException;
import java.net.UnknownHostException;

public class LoginUseCase extends ALoginUseCase {
    private IAuthenticationManager mAuthenticationManager;
    private Credentials insertedCredentials;
    private IMainExecutor mMainExecutor = new UIThreadExecutor();
    private IAsyncExecutor mAsyncExecutor = new AsyncExecutor();
    private Callback mCallback;

    public LoginUseCase(IAuthenticationManager authenticationManager, IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor) {
        mAuthenticationManager = authenticationManager;
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
    }

    @Override
    public void execute(final Credentials credentials, final Callback callback) {
        mCallback = callback;
        insertedCredentials = credentials;
        mAuthenticationManager.hardcodedLogin(ServerAPIController.getServerUrl(),
                new IAuthenticationManager.Callback<UserAccount>() {
                    @Override
                    public void onSuccess(UserAccount userAccount) {
                        mAsyncExecutor.run(new Runnable() {
                            @Override
                            public void run() {
                                pullOrganisationCredentials(credentials, callback);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof MalformedURLException
                                || throwable instanceof UnknownHostException) {
                            notifyServerURLNotValid();
                        } else if (throwable instanceof InvalidCredentialsException) {
                            notifyInvalidCredentials();
                        } else if (throwable instanceof NetworkException) {
                            ICredentialsRepository
                                    credentialsLocalDataSource = new CredentialsLocalDataSource();
                            checkUserCredentialsWithOrgUnit(
                                    credentialsLocalDataSource.getOrganisationCredentials(), false);
                        }
                    }
                });

    }


    private void pullOrganisationCredentials(Credentials credentials, final Callback callback) {
        IOrgUnitRepository orgUnitDataSource = new OrgUnitDataSource();
        ICredentialsRepository
                credentialsLocalDataSource = new CredentialsLocalDataSource();
        Credentials orgUnitCredentials = null;
        try {
            OrgUnit orgUnit = orgUnitDataSource.getUserOrgUnit(credentials);
            if (orgUnit == null) {
                notifyInvalidCredentials();
                return;
            }
            orgUnitCredentials = new Credentials("", orgUnit.getCode(), orgUnit.getPin());

        } catch (PullConversionException e) {
            e.printStackTrace();
            notifyConfigJsonNotPresent();
        } catch (NetworkException e) {
            e.printStackTrace();
            checkUserCredentialsWithOrgUnit(credentialsLocalDataSource.getOrganisationCredentials(),
                    true);
        }

        credentialsLocalDataSource.saveOrganisationCredentials(orgUnitCredentials);

        checkUserCredentialsWithOrgUnit(orgUnitCredentials, false);
    }


    private void checkUserCredentialsWithOrgUnit(Credentials credentials,
            boolean fromNetWorkError) {
        if (insertedCredentials.getUsername().equals(credentials.getUsername())
                && insertedCredentials.getPassword().equals(credentials.getPassword())) {
            notifyLoginSucces();
        } else {
            if (fromNetWorkError) {
                notifyNetworkError();
            } else {
                notifyInvalidCredentials();
            }
        }
    }

    public void notifyLoginSucces() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onLoginSuccess();
            }
        });
    }

    public void notifyInvalidCredentials() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onInvalidCredentials();
            }
        });
    }

    public void notifyConfigJsonNotPresent() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onConfigJsonNotPresent();
            }
        });
    }

    public void notifyNetworkError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onNetworkError();
            }
        });
    }

    public void notifyServerURLNotValid() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onServerURLNotValid();
            }
        });
    }

    public void notifyUnexpectedError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onUnexpectedError();
            }
        });
    }


}
