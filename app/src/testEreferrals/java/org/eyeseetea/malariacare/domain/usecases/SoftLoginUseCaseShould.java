package org.eyeseetea.malariacare.domain.usecases;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.eyeseetea.malariacare.common.FileReader;
import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.remote.AuthenticationWSDataSource;
import org.eyeseetea.malariacare.data.remote.IForgotPasswordDataSource;
import org.eyeseetea.malariacare.data.rules.MockWebServerRule;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;
import org.eyeseetea.malariacare.domain.usecase.SoftLoginUseCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class SoftLoginUseCaseShould {
    private static final String AUTH_SUCCESS = "authSuccess.json";
    private static final String AUTH_FAIL = "authFail.json";
    private static final String API_AVAILABLE_OK = "api_available_ok.json";
    private static final long DISABLE_LOGIN_BY_ATTEMPTS = 30000;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Rule
    public MockWebServerRule mockWebServerRule = new MockWebServerRule(new FileReader());

    @Mock
    IConnectivityManager connectivityManager;

    @Mock
    IInvalidLoginAttemptsRepository invalidLoginAttemptsRepository;

    @Mock
    ICredentialsRepository credentialsRepository;

    @Mock
    IAuthenticationDataSource userAccountLocalDataSource;

    @Mock
    IUserRepository userRepository;

    @Mock
    ISettingsRepository settingsRepository;

    @Mock
    eReferralsAPIClient mockEReferralsAPIClient;

    @Mock
    IForgotPasswordDataSource forgotPasswordDataSource;

    @Test
    public void return_on_soft_login_success_if_exists_connection_and_credentials_are_ok()
            throws IOException {

        SoftLoginUseCase softLoginUseCase = givenASoftLoginUseCase(true);

        mockWebServerRule.enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mockWebServerRule.enqueueMockResponseFileName(200, AUTH_SUCCESS);

        softLoginUseCase.execute("1234", new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                Assert.assertTrue(true);
            }

            @Override
            public void onInvalidPin() {
                fail("onInvalidPassword");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                fail("onMaxInvalidLoginAttemptsError");
            }

            @Override
            public void onServerNotAvailable(String message) {
                fail("onServerNotAvailable");
            }
        });
    }

    @Test
    public void return_on_invalid_password_if_exists_connection_and_credentials_are_not_ok()
            throws IOException {

        SoftLoginUseCase softLoginUseCase = givenASoftLoginUseCase(true);

        mockWebServerRule.enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mockWebServerRule.enqueueMockResponseFileName(200, AUTH_FAIL);

        softLoginUseCase.execute("1234", new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                fail("onInvalidPassword");
            }

            @Override
            public void onInvalidPin() {
                Assert.assertTrue(true);
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                fail("onMaxInvalidLoginAttemptsError");
            }

            @Override
            public void onServerNotAvailable(String message) {
                fail("onServerNotAvailable");
            }
        });
    }

    @Test
    public void return_on_soft_login_success_if_does_not_exists_connection_and_credentials_are_ok()
            throws IOException {

        String lastValidPin = "1234";

        SoftLoginUseCase softLoginUseCase = givenASoftLoginUseCase(true, lastValidPin);

        mockWebServerRule.enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mockWebServerRule.enqueueMockResponseFileName(200, AUTH_SUCCESS);

        softLoginUseCase.execute(lastValidPin, new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                Assert.assertTrue(true);
            }

            @Override
            public void onInvalidPin() {
                fail("onInvalidPassword");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                fail("onMaxInvalidLoginAttemptsError");
            }

            @Override
            public void onServerNotAvailable(String message) {
                fail("onMaxInvalidLoginAttemptsError");
            }
        });
    }

    @Test
    public void return_on_soft_login_success_if_remote_throw_timeout_and_credentials_are_ok()
            throws IOException, AvailableApiException {

        String lastValidPin = "1234";

        SoftLoginUseCase softLoginUseCase = givenASoftLoginUseCaseWithTimeout(true, lastValidPin);

        softLoginUseCase.execute(lastValidPin, new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                Assert.assertTrue(true);
            }

            @Override
            public void onInvalidPin() {
                fail("onInvalidPassword");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                fail("onMaxInvalidLoginAttemptsError");
            }

            @Override
            public void onServerNotAvailable(String message) {
                fail("onMaxInvalidLoginAttemptsError");
            }
        });
    }


    @Test
    public void return_on_network_error_if_does_not_exists_connection_and_credentials_are_not_ok()
            throws IOException {

        String lastValidPin = "1234";

        SoftLoginUseCase softLoginUseCase = givenASoftLoginUseCase(true, lastValidPin);

        mockWebServerRule.enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mockWebServerRule.enqueueMockResponseFileName(200, AUTH_FAIL);

        softLoginUseCase.execute("1234567", new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                fail("onInvalidPassword");
            }

            @Override
            public void onInvalidPin() {
                Assert.assertTrue(true);
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                fail("onMaxInvalidLoginAttemptsError");
            }

            @Override
            public void onServerNotAvailable(String message) {
                fail("onMaxInvalidLoginAttemptsError");
            }
        });
    }

    @Test
    public void
    return_on_invalid_password_and_on_max_invalid_login_attempts_error_if_current_attempts_is_2_and_credentials_are_not_ok()
            throws IOException {

        int invalidLoginAttempts = 2;

        SoftLoginUseCase softLoginUseCase =
                givenASoftLoginUseCase(invalidLoginAttempts, 0);

        mockWebServerRule.enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mockWebServerRule.enqueueMockResponseFileName(200, AUTH_FAIL);

        softLoginUseCase.execute("1234567", new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                fail("onInvalidPassword");
            }

            @Override
            public void onInvalidPin() {
                Assert.assertTrue(true);
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                Assert.assertTrue(true);
            }


            @Override
            public void onServerNotAvailable(String message) {
                fail("onMaxInvalidLoginAttemptsError");
            }
        });
    }

    @Test
    public void
    return_on_max_invalid_login_attempts_error_if_current_attempts_is_3_and_it_is_not_over_enable_date()
            throws IOException {

        int invalidLoginAttempts = 3;

        SoftLoginUseCase softLoginUseCase =
                givenASoftLoginUseCase(invalidLoginAttempts,
                        new Date().getTime() + DISABLE_LOGIN_BY_ATTEMPTS);

        softLoginUseCase.execute("1234", new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                fail("onInvalidPassword");
            }

            @Override
            public void onInvalidPin() {
                fail("onInvalidPassword");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                Assert.assertTrue(true);
            }

            @Override
            public void onServerNotAvailable(String message) {
                fail("onMaxInvalidLoginAttemptsError");
            }
        });
    }

    @Test
    public void
    return_on_soft_login_success_if_current_attempts_is_3_and_it_is_over_enable_date()
            throws IOException {

        int invalidLoginAttempts = 3;

        SoftLoginUseCase softLoginUseCase =
                givenASoftLoginUseCase(invalidLoginAttempts,
                        new Date().getTime() - DISABLE_LOGIN_BY_ATTEMPTS);

        mockWebServerRule.enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mockWebServerRule.enqueueMockResponseFileName(200, AUTH_SUCCESS);

        softLoginUseCase.execute("1234", new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                Assert.assertTrue(true);
            }

            @Override
            public void onInvalidPin() {
                fail("onNetworkError");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxInvalidLoginAttemptsError(long enableLoginTime) {
                fail("onMaxLoginAttemptsReachedError");
            }


            @Override
            public void onServerNotAvailable(String message) {
                fail("onMaxInvalidLoginAttemptsError");
            }
        });
    }

    private SoftLoginUseCase givenASoftLoginUseCase(boolean withConnection) {
        return givenASoftLoginUseCase(withConnection, "dummy", 0, new Date().getTime(), null);
    }

    private SoftLoginUseCase givenASoftLoginUseCase(boolean withConnection, String lastValidPin) {
        return givenASoftLoginUseCase(withConnection, lastValidPin, 0, new Date().getTime(), null);
    }

    private SoftLoginUseCase givenASoftLoginUseCaseWithTimeout(boolean withConnection,
            String lastValidPin) throws IOException, AvailableApiException {
        when(mockEReferralsAPIClient.auth(anyString(), anyString())).thenThrow(
                new SocketTimeoutException());

        return givenASoftLoginUseCase(withConnection, lastValidPin, 0, new Date().getTime(),
                mockEReferralsAPIClient);
    }

    private SoftLoginUseCase givenASoftLoginUseCase(int invalidLoginAttempts,
            long timeToEnableLogin) {
        return givenASoftLoginUseCase(true, "dummy",
                invalidLoginAttempts, timeToEnableLogin, null);
    }


    private SoftLoginUseCase givenASoftLoginUseCase(boolean withConnection, String lastValidPin,
            int invalidLoginAttempts, long timeToEnableLogin,
            eReferralsAPIClient mockEReferralsAPIClient) {

        IMainExecutor mainExecutor = new IMainExecutor() {
            @Override
            public void run(Runnable runnable) {
                runnable.run();
            }
        };

        IAsyncExecutor asyncExecutor = new IAsyncExecutor() {
            @Override
            public void run(Runnable runnable) {
                runnable.run();
            }
        };

        eReferralsAPIClient eReferralsAPIClient;

        if (mockEReferralsAPIClient == null) {
            eReferralsAPIClient = new eReferralsAPIClient(mockWebServerRule.getBaseEndpoint());
        } else {
            eReferralsAPIClient = mockEReferralsAPIClient;
        }

        IAuthenticationDataSource userAccountRemoteDataSource =
                new AuthenticationWSDataSource(eReferralsAPIClient);

        IAuthenticationManager authenticationManager = new AuthenticationManager(
                userAccountLocalDataSource, userAccountRemoteDataSource, userRepository,
                forgotPasswordDataSource, settingsRepository);

        when(connectivityManager.isDeviceOnline()).thenReturn(withConnection);

        when(invalidLoginAttemptsRepository.getInvalidLoginAttempts()).thenReturn(
                new InvalidLoginAttempts(invalidLoginAttempts, timeToEnableLogin)
        );

        when(credentialsRepository.getLastValidCredentials()).thenReturn(
                new Credentials(mockWebServerRule.getBaseEndpoint(), "test", lastValidPin));

        return new SoftLoginUseCase(connectivityManager, authenticationManager,
                mainExecutor, asyncExecutor, credentialsRepository, invalidLoginAttemptsRepository);
    }
}
