package org.eyeseetea.malariacare.domain.usecases;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import org.eyeseetea.malariacare.common.FileReader;
import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.remote.AuthenticationWSDataSource;
import org.eyeseetea.malariacare.data.rules.MockWebServerRule;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;
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
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class SoftLoginUseCaseShould {
    private static final String AUTH_SUCCESS = "authSuccess.json";
    private static final String AUTH_Fail = "authFail.json";

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

    @Test
    public void return_on_soft_login_success_if_exists_connection_and_credentials_are_ok()
            throws IOException {

        SoftLoginUseCase softLoginUseCase = givenASoftLoginUseCase(true, "1234");

        mockWebServerRule.enqueueMockResponseFileName(200, AUTH_SUCCESS);

        softLoginUseCase.execute("1234", new SoftLoginUseCase.Callback() {
            @Override
            public void onSoftLoginSuccess() {
                Assert.assertTrue(true);
            }

            @Override
            public void onInvalidPassword() {
                fail("onInvalidPassword");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onMaxLoginAttemptsReachedError() {
                fail("onMaxLoginAttemptsReachedError");
            }
        });
    }


    private SoftLoginUseCase givenASoftLoginUseCase(boolean withConnection, String lastValidPin) {
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

        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mockWebServerRule.getBaseEndpoint());

        IAuthenticationDataSource userAccountRemoteDataSource =
                new AuthenticationWSDataSource(eReferralsAPIClient);


        IAuthenticationManager authenticationManager = new AuthenticationManager(
                userAccountLocalDataSource, userAccountRemoteDataSource, userRepository);

        when(connectivityManager.isDeviceOnline()).thenReturn(withConnection);

        when(invalidLoginAttemptsRepository.getInvalidLoginAttempts()).thenReturn(
                new InvalidLoginAttempts(0, new Date().getTime())
        );

        when(credentialsRepository.getLastValidCredentials()).thenReturn(
                new Credentials(mockWebServerRule.getBaseEndpoint(), "test", lastValidPin));
        
        return new SoftLoginUseCase(connectivityManager, authenticationManager,
                mainExecutor, asyncExecutor, credentialsRepository, invalidLoginAttemptsRepository);
    }
}
