package org.eyeseetea.malariacare.domain.usecase;

import com.google.android.gms.common.api.Api;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ActionNotAllowed;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

public class LoginUseCase extends ALoginUseCase implements UseCase {
    private IAuthenticationManager mAuthenticationManager;
    private Credentials insertedCredentials;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IOrganisationUnitRepository mOrgUnitDataSource;
    private ICredentialsRepository mCredentialsLocalDataSource;
    private IInvalidLoginAttemptsRepository mInvalidLoginAttemptsLocalDataSource;
    private Callback mCallback;

    public LoginUseCase(IAuthenticationManager authenticationManager, IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor, IOrganisationUnitRepository orgUnitDataSource,
            ICredentialsRepository credentialsLocalDataSource,
            IInvalidLoginAttemptsRepository iInvalidLoginAttemptsRepository) {
        mAuthenticationManager = authenticationManager;
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mOrgUnitDataSource = orgUnitDataSource;
        mCredentialsLocalDataSource = credentialsLocalDataSource;
        mInvalidLoginAttemptsLocalDataSource = iInvalidLoginAttemptsRepository;
    }

    @Override
    public void execute(final Credentials credentials, final Callback callback) {
        mCallback = callback;
        insertedCredentials = credentials;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {

        if (isLoginEnable()) {
            mAuthenticationManager.hardcodedLogin(insertedCredentials.getServerURL(),
                    new IAuthenticationManager.Callback<UserAccount>() {
                        @Override
                        public void onSuccess(UserAccount userAccount) {
                            mAsyncExecutor.run(new Runnable() {
                                @Override
                                public void run() {
                                    pullOrganisationCredentials();
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
                                checkUserCredentialsWithOrgUnit(
                                        mCredentialsLocalDataSource.getOrganisationCredentials(),
                                        true);
                    }
                        }
                    });
        } else {
            notifyMaxLoginAttemptsReached();
        }
    }

    private boolean isLoginEnable() {
        InvalidLoginAttempts invalidLoginAttempts =
                mInvalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();
        return invalidLoginAttempts.isLoginEnabled();
    }

    private void pullOrganisationCredentials() {
        Credentials orgUnitCredentials = null;
        try {
            OrganisationUnit orgUnit = mOrgUnitDataSource.getUserOrgUnit(insertedCredentials);
            if (orgUnit == null) {
                notifyInvalidCredentials();
                return;
            }
            orgUnitCredentials =
                    new Credentials(insertedCredentials.getServerURL(), orgUnit.getCode(),
                            orgUnit.getPin());

        } catch (ApiCallException e) {
            if(e.getCause() instanceof  IOException){
                notifyUnexpectedError();
            }else {
                e.printStackTrace();
                notifyConfigJsonNotPresent();
            }
        } catch (NetworkException e) {
            e.printStackTrace();
            checkUserCredentialsWithOrgUnit(
                    mCredentialsLocalDataSource.getOrganisationCredentials(),
                    true);
        }

        mCredentialsLocalDataSource.saveOrganisationCredentials(orgUnitCredentials);

        checkUserCredentialsWithOrgUnit(orgUnitCredentials, false);
    }


    private void checkUserCredentialsWithOrgUnit(Credentials credentials,
            boolean fromNetWorkError) {
        if (insertedCredentials.getUsername().equals(credentials.getUsername())
                && insertedCredentials.getPassword().equals(credentials.getPassword())
                && (fromNetWorkError || insertedCredentials.getServerURL().equals(
                credentials.getServerURL()))) {
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
        InvalidLoginAttempts invalidLoginAttempts =
                mInvalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();

        try {
            invalidLoginAttempts.addFailedAttempts();
        } catch (ActionNotAllowed actionNotAllowed) {
            actionNotAllowed.printStackTrace();
            notifyMaxLoginAttemptsReached();
        }

        mInvalidLoginAttemptsLocalDataSource.saveInvalidLoginAttempts(invalidLoginAttempts);

        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onInvalidCredentials();
            }
        });

        if (!invalidLoginAttempts.isLoginEnabled()) {
            notifyMaxLoginAttemptsReached();
        }
    }

    private void notifyMaxLoginAttemptsReached() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onMaxLoginAttemptsReachedError();
            }
        });
    }

    public void notifyConfigJsonNotPresent() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onConfigJsonInvalid();
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
