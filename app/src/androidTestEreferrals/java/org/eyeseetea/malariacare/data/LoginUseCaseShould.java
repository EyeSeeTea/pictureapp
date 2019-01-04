package org.eyeseetea.malariacare.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import org.eyeseetea.malariacare.AssetsFileReader;
import org.eyeseetea.malariacare.CommonTestResourcesCalls;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.exporter.ConvertToWSVisitor;
import org.eyeseetea.malariacare.data.sync.exporter.WSPushController;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class LoginUseCaseShould extends CommonTestResourcesCalls {

    private static final String AUTH_OK = "auth_ok.json";
    private static final String API_AVAILABLE_OK = "api_available_ok.json";
    private static final String API_AVAILABLE_NO_OK = "api_available_no_ok.json";
    private CustomMockServer mCustomMockServer;
    private LoginUseCase mLoginUseCase;
    private int countSync;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Before
    public void cleanUp() throws IOException {
        mCustomMockServer = new CustomMockServer(new AssetsFileReader());
        savePreviousPreferences();
        saveTestCredentialsAndProgram();
        Context context = InstrumentationRegistry.getTargetContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.web_service_url),
                mCustomMockServer.getBaseEndpoint());
        editor.commit();
        mLoginUseCase = new AuthenticationFactoryStrategy()
                .getLoginUseCase(mActivityRule.getActivity());
    }

    @Test
    public void call_on_login_success_callback_when_server_is_available_during_login() throws IOException, InterruptedException {
        final Object syncObject = new Object();
        mCustomMockServer.enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mCustomMockServer.enqueueMockResponseFileName(200, AUTH_OK);
        mLoginUseCase.execute(new Credentials(mCustomMockServer.getBaseEndpoint(), "test", "test"), new LoginUseCase.Callback() {
            @Override
            public void onLoginSuccess() {
                synchronized (syncObject) {
                    if (countSync == 0) {
                        syncObject.notify();
                    }
                    countSync++;
                }
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
            public void onServerNotAvailable() {
                fail("onServerNotAvailable");
            }

        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    @Test
    public void call_on_server_not_available_callback_when_server_is_not_available_during_login() throws IOException, InterruptedException {
        final Object syncObject = new Object();
        mCustomMockServer.enqueueMockResponseFileName(200, API_AVAILABLE_NO_OK);
        mLoginUseCase.execute(new Credentials(mCustomMockServer.getBaseEndpoint(), "test", "test"), new LoginUseCase.Callback() {
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
            public void onServerNotAvailable() {
                synchronized (syncObject) {
                    if (countSync == 0) {
                        syncObject.notify();
                    }
                    countSync++;
                }
            }

        });

        synchronized (syncObject) {
            syncObject.wait();
        }
    }
}
