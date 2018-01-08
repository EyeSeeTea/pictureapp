package org.eyeseetea.malariacare.data.sync.exporter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.data.file.ResourcesFileReader;
import org.eyeseetea.malariacare.data.server.Dhis2MockServer;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordPayload;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ForgotPasswordAPIClientTest {


    private eReferralsAPIClient apiClient;

    private static final String FORGOT_PASSWORD_DENIED =
            "forgot_password_denied.json";

    private static final String FORGOT_PASSWORD_SUCCESS =
            "forgot_password_success.json";

    private Dhis2MockServer dhis2MockServer;

    @Before
    public void setUp() throws Exception {
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        apiClient = initializeApiClient();
    }

    @After
    public void teardown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    public void shouldParseForgotPasswordSuccessResponse()
            throws IOException, InterruptedException {

        dhis2MockServer.enqueueMockResponse(FORGOT_PASSWORD_SUCCESS);

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
        dhis2MockServer.enqueueMockResponse(FORGOT_PASSWORD_DENIED);

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
        return new eReferralsAPIClient(dhis2MockServer.getBaseEndpoint());
    }

    private ForgotPasswordPayload givenAForgotPasswordRequest() {
        ForgotPasswordPayload forgotPasswordPayload = new ForgotPasswordPayload(
                "1.0", "manu", "en");

        return forgotPasswordPayload;
    }
}
