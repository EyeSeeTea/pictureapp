package org.eyeseetea.malariacare.test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import android.content.Context;
import android.support.test.runner.AndroidJUnit4;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.repositories.ForgotPasswordRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IForgotPasswordRepository;
import org.eyeseetea.malariacare.domain.usecase.ForgotPasswordUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.surveillance_ref_bb.test.R;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@RunWith(AndroidJUnit4.class)
public class TestWSForgotPassword {
    private MockWebServer server;

    @Before
    public void initMockWebServer() {
        server = new MockWebServer();
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PreferencesEReferral.setWSURL(server.url("eRefWSDev/api/forgotpassword/").toString());

    }

    private String getStringFromFile(int fileId) {
        InputStream inputStream = getInstrumentation().getContext().getResources()
                .openRawResource(
                        fileId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        String text = "";
        try {
            line = reader.readLine();
            while (line != null) {
                text += line;
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    @Test
    public void testForgotPasswordDenied() {
        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(R.raw.forgot_password_denied)));
        Context context = getInstrumentation().getContext();

        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IForgotPasswordRepository forgotPasswordRepository = new ForgotPasswordRepository(
                context);
        ForgotPasswordUseCase forgotPasswordUseCase = new ForgotPasswordUseCase(mainExecutor,
                asyncExecutor, forgotPasswordRepository);
        final CountDownLatch signal = new CountDownLatch(1);
        forgotPasswordUseCase.execute("test",
                new ForgotPasswordUseCase.Callback() {
                    @Override
                    public void onGetForgotPasswordSuccess(String result, String title) {
                        assertThat(result,
                                is("Username or password do not match any register in the server"));
                        assertThat(title, is("Denied"));
                        signal.countDown();
                    }

                    @Override
                    public void onNetworkError() {
                        fail();
                        signal.countDown();
                    }

                    @Override
                    public void onError(String messages) {
                        fail();
                        signal.countDown();
                    }
                });
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void forgotPasswordSuccess() {

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(R.raw.forgot_password_succes)));
        Context context = getInstrumentation().getContext();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IForgotPasswordRepository forgotPasswordRepository = new ForgotPasswordRepository(
                context);
        ForgotPasswordUseCase forgotPasswordUseCase = new ForgotPasswordUseCase(mainExecutor,
                asyncExecutor, forgotPasswordRepository);
        final CountDownLatch signal = new CountDownLatch(1);
        forgotPasswordUseCase.execute("test",
                new ForgotPasswordUseCase.Callback() {
                    @Override
                    public void onGetForgotPasswordSuccess(String result, String title) {
                        assertThat(result,
                                is("SMS sent to stored phone for the given username"));
                        assertThat(title, is("Accept"));
                        signal.countDown();
                    }

                    @Override
                    public void onNetworkError() {
                        fail();
                        signal.countDown();
                    }

                    @Override
                    public void onError(String messages) {
                        fail();
                        signal.countDown();
                    }
                });
        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @After
    public void closeServer() {
        try {
            server.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
