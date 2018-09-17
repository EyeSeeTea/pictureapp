package org.eyeseetea.malariacare.data.sync.exporter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eyeseetea.malariacare.common.FileReader;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.exporter.model.SettingsSummary;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResponseAction;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.domain.exception.ConfigFileObsoleteException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class eReferralsAPIClientShould {

    private static final String PUSH_RESPONSE_OK_ONE_SURVEY = "push_response_ok_one_survey.json";
    private static final String PUSH_RESPONSE_OK_EXTRA_KEYS = "push_response_ok_extra_keys.json";
    private CustomMockServer mCustomMockServer;


    @Before
    public void cleanUp() throws IOException {
        mCustomMockServer = new CustomMockServer(new FileReader());
    }

    @Test
    public void
    throw_exception_configFileObsoleteException_on_209_response_code_and_return_surveys()
            throws IOException {
        mCustomMockServer.enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mCustomMockServer.getBaseEndpoint());
        eReferralsAPIClient.pushSurveys(new SurveyContainerWSObject("", "",
                        "", "", "", 2, "", "", new SettingsSummary("", "", "", false, "", false), ""),
                new eReferralsAPIClient.WSClientCallBack() {
                    @Override
                    public void onSuccess(Object result) {
                        List<SurveyWSResponseAction> surveyWSResponseActions =
                                ((SurveyWSResult) result).getActions();
                        assertThat(surveyWSResponseActions.size(), is(1));
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

    @Test
    public void return_success_result_when_api_response_contains_extra_keys()
            throws IOException {
        mCustomMockServer.enqueueMockResponseFileName(200, PUSH_RESPONSE_OK_EXTRA_KEYS);
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mCustomMockServer.getBaseEndpoint());
        eReferralsAPIClient.pushSurveys(new SurveyContainerWSObject("", "",
                        "", "", "", 2, "", "", new SettingsSummary("", "", "", false, "", false), ""),
                new eReferralsAPIClient.WSClientCallBack() {
                    @Override
                    public void onSuccess(Object result) {
                        assertThat(result, is(notNullValue()));
                    }

                    @Override
                    public void onError(Exception e) {
                        Assert.fail();
                    }
                });
    }

    @After
    public void tearDown() throws IOException {
        mCustomMockServer.shutdown();
    }
}
