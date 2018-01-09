package org.eyeseetea.malariacare.data.sync.exporter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eyeseetea.malariacare.common.FileReader;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.domain.exception.ConfigFileObsoleteException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class eReferralsAPIClientShould {

    private CustomMockServer mCustomMockServer;


    @Before
    public void cleanUp() throws IOException {
        mCustomMockServer = new CustomMockServer(new FileReader());
    }

    @Test
    public void throw_exception_configFileObsoleteException_on_403_response_code()
            throws IOException {
        mCustomMockServer.enqueueMockResponse(403);
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mCustomMockServer.getBaseEndpoint());
        eReferralsAPIClient.pushSurveys(new SurveyContainerWSObject("", "",
                        "", "", "", "", ""),
                new eReferralsAPIClient.WSClientCallBack() {
                    @Override
                    public void onSuccess(Object result) {
                    }

                    @Override
                    public void onError(Exception e) {
                        boolean isConfigFileObsoleteException = false;
                        if (e instanceof ConfigFileObsoleteException) {
                            isConfigFileObsoleteException = true;
                        }
                        assertThat(isConfigFileObsoleteException, is(true));
                    }
                });


    }

    @After
    public void tearDown() throws IOException {
        mCustomMockServer.shutdown();
    }
}
