package org.eyeseetea.malariacare.data.sync.exporter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.common.FileReader;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordPayload;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class ForgotPasswordAPIClientTest {

    private MockWebServer server;
    private eReferralsAPIClient apiClient;

    public static final String FORGOT_PASSWORD_DENIED =
            "forgot_password_denied.json";

    public static final String FORGOT_PASSWORD_SUCCESS =
            "forgot_password_success.json";

    @Before
    public void setUp() throws Exception {
        this.server = new MockWebServer();
        this.server.start();
        apiClient = initializeApiClient();
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void shouldParseForgotPasswordSuccessResponse()
            throws IOException, InterruptedException {
        enqueueResponse(FORGOT_PASSWORD_SUCCESS);

        final CountDownLatch signal = new CountDownLatch(1);

        apiClient.getForgotPassword(givenAForgotPasswordRequest(),
                new eReferralsAPIClient.WSClientCallBack<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(ForgotPasswordResponse result) {
                        assertThat(result.getMessage(),
                                is("SMS sent to stored phone for the given username"));
                        assertThat(result.getStatus(), is("Accept"));

                        signal.countDown();
                    }

                    @Override
                    public void onError(Exception e) {
                        Assert.fail();
                        signal.countDown();

                    }
                });


        signal.await();
    }

    @Test
    public void shouldParseForgotPasswordDeniedResponse() throws IOException, InterruptedException {
        enqueueResponse(FORGOT_PASSWORD_DENIED);

        final CountDownLatch signal = new CountDownLatch(1);

        apiClient.getForgotPassword(givenAForgotPasswordRequest(),
                new eReferralsAPIClient.WSClientCallBack<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(ForgotPasswordResponse result) {
                        assertThat(result.getMessage(),
                                is("Username or password do not match any register in the server"));
                        assertThat(result.getStatus(), is("Denied"));
                        signal.countDown();
                    }

                    @Override
                    public void onError(Exception e) {
                        Assert.fail();
                        signal.countDown();

                    }
                });


        signal.await();
    }

    private eReferralsAPIClient initializeApiClient() {
        return new eReferralsAPIClient(server.url("/").toString());
    }

    private ForgotPasswordPayload givenAForgotPasswordRequest() {
        ForgotPasswordPayload forgotPasswordPayload = new ForgotPasswordPayload(
                "1.0", "manu", "en");

        return forgotPasswordPayload;
    }

    private void enqueueResponse(String fileName) throws IOException {
        MockResponse mockResponse = new MockResponse();
        String fileContent = new FileReader().getStringFromFile(getClass(), fileName);
        mockResponse.setBody(fileContent);
        server.enqueue(mockResponse);
    }
}
