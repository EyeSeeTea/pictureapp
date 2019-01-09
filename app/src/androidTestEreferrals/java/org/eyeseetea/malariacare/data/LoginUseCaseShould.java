package org.eyeseetea.malariacare.data;

import org.eyeseetea.malariacare.data.remote.IForgotPasswordDataSource;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.rules.PreferencesDependenciesRules;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class LoginUseCaseShould {

    @Mock
    IConnectivityManager connectivityManager;
    @Mock
    IAuthenticationDataSource authenticationLocalDataSource;
    @Mock
    IForgotPasswordDataSource forgotPasswordDataSource;
    @Mock
    ISettingsRepository settingsRepository;
    @Mock
    ICredentialsRepository mCredentialsRepository;
    @Mock
    IInvalidLoginAttemptsRepository mInvalidLoginAttemptsRepository;

    @Rule
    public PreferencesDependenciesRules preferencesDependenciesRules = new PreferencesDependenciesRules();

    private static final String AUTH_OK = "auth_ok.json";
    private static final String API_AVAILABLE_OK = "api_available_ok.json";
    private static final String API_AVAILABLE_NO_OK = "api_available_no_ok.json";

    @Test
    public void call_on_login_success_callback_when_server_is_available_during_login() throws IOException {

        LoginUseCase mLoginUseCase = preferencesDependenciesRules.givenALoginUseCase(connectivityManager, authenticationLocalDataSource,forgotPasswordDataSource,
                settingsRepository,  mCredentialsRepository, mInvalidLoginAttemptsRepository);
        preferencesDependenciesRules.getMockServer().enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        preferencesDependenciesRules.getMockServer().enqueueMockResponseFileName(200, AUTH_OK);

        mLoginUseCase.execute(preferencesDependenciesRules.getCredentials(), new LoginUseCase.Callback() {
            @Override
            public void onLoginSuccess() {
                Assert.assertTrue(true);
            }

            @Override
            public void onServerURLNotValid() {
                fail("onLoginSuccess");
            }

            @Override
            public void onInvalidCredentials() {
                fail("onLoginSuccess");
            }

            @Override
            public void onServerPinChanged() {
                fail("onLoginSuccess");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onConfigJsonInvalid() {
                fail("onLoginSuccess");
            }

            @Override
            public void onUnexpectedError() {
                fail("onLoginSuccess");
            }

            @Override
            public void onMaxLoginAttemptsReachedError() {
                fail("onLoginSuccess");
            }

            @Override
            public void onServerNotAvailable(String message) {
                fail("onServerNotAvailable");
            }

        });
    }

    @Test
    public void call_on_server_not_available_callback_when_server_is_not_available_during_login() throws IOException {

        LoginUseCase mLoginUseCase = preferencesDependenciesRules.givenALoginUseCase(connectivityManager, authenticationLocalDataSource,forgotPasswordDataSource,
                settingsRepository,  mCredentialsRepository, mInvalidLoginAttemptsRepository);
        preferencesDependenciesRules.getMockServer().enqueueMockResponseFileName(200, API_AVAILABLE_NO_OK);

        mLoginUseCase.execute(preferencesDependenciesRules.getCredentials(), new LoginUseCase.Callback() {
            @Override
            public void onLoginSuccess() {
                fail("onLoginSuccess");
            }

            @Override
            public void onServerURLNotValid() {
                fail("onLoginSuccess");
            }

            @Override
            public void onInvalidCredentials() {
                fail("onLoginSuccess");
            }

            @Override
            public void onServerPinChanged() {
                fail("onLoginSuccess");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onConfigJsonInvalid() {
                fail("onLoginSuccess");
            }

            @Override
            public void onUnexpectedError() {
                fail("onLoginSuccess");
            }

            @Override
            public void onMaxLoginAttemptsReachedError() {
                fail("onLoginSuccess");
            }

            @Override
            public void onServerNotAvailable(String message) {
                Assert.assertTrue(true);
            }

        });
    }
}
