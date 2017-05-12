package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;
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
    private ISurveyRepository mSurveyLocalDataSource;
    private Callback mCallback;

    public LoginUseCase(IAuthenticationManager authenticationManager, IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor, IOrganisationUnitRepository orgUnitDataSource,
            ICredentialsRepository credentialsLocalDataSource,
            IInvalidLoginAttemptsRepository iInvalidLoginAttemptsRepository,
            ISurveyRepository surveyLocalDataSource) {
        mAuthenticationManager = authenticationManager;
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mOrgUnitDataSource = orgUnitDataSource;
        mCredentialsLocalDataSource = credentialsLocalDataSource;
        mInvalidLoginAttemptsLocalDataSource = iInvalidLoginAttemptsRepository;
        mSurveyLocalDataSource = surveyLocalDataSource;
    }

    @Override
    public void execute(final Credentials credentials, final Callback callback) {
        mCallback = callback;
        insertedCredentials = credentials;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mAuthenticationManager.hardcodedLogin(ServerAPIController.getServerUrl(),
                new IAuthenticationManager.Callback<UserAccount>() {
                    @Override
                    public void onSuccess(UserAccount userAccount) {
                        mAsyncExecutor.run(new Runnable() {
                            @Override
                            public void run() {
                                deleteSurveys();
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
                                    false);
                        }
                    }
                });
    }


    private void deleteSurveys() {
        Credentials oldCredentials = mCredentialsLocalDataSource.getOrganisationCredentials();
        if (!insertedCredentials.getUsername().equals(oldCredentials.getUsername())) {
            mSurveyLocalDataSource.deleteSurveys();
        }
        pullOrganisationCredentials();
    }


    private void pullOrganisationCredentials() {
        Credentials orgUnitCredentials = null;
        try {
            OrganisationUnit orgUnit = mOrgUnitDataSource.getUserOrgUnit(insertedCredentials);
            if (orgUnit == null) {
                notifyInvalidCredentials();
                return;
            }
            orgUnitCredentials = new Credentials("", orgUnit.getCode(), orgUnit.getPin());

        } catch (PullConversionException | JSONException | ConfigJsonIOException e) {
            e.printStackTrace();
            notifyConfigJsonNotPresent();
        } catch (NetworkException e) {
            e.printStackTrace();
            checkUserCredentialsWithOrgUnit(
                    mCredentialsLocalDataSource.getOrganisationCredentials(),
                    true);
        } catch (IOException e) {
            e.printStackTrace();
            notifyUnexpectedError();
        }

        mCredentialsLocalDataSource.saveOrganisationCredentials(orgUnitCredentials);

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
        InvalidLoginAttempts invalidLoginAttempts =
                mInvalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();

        invalidLoginAttempts.addFailedAttempts();

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
