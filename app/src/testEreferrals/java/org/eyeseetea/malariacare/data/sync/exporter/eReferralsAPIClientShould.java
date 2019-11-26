package org.eyeseetea.malariacare.data.sync.exporter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eyeseetea.malariacare.common.FileReader;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.exporter.model.ApiAvailable;
import org.eyeseetea.malariacare.data.sync.exporter.model.SettingsSummary;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResponseAction;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;
import org.eyeseetea.malariacare.domain.exception.ConfigFileObsoleteException;
import org.eyeseetea.malariacare.rules.MockWebServerRule;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.List;

public class eReferralsAPIClientShould {

    private static final String PUSH_RESPONSE_OK_ONE_SURVEY = "push_response_ok_one_survey.json";
    private static final String PUSH_RESPONSE_OK_EXTRA_KEYS = "push_response_ok_extra_keys.json";
    private static final String API_AVAILABLE_OK = "api_available_ok.json";
    private static final String API_AVAILABLE_NO_OK = "api_available_no_ok.json";
    private static final String API_AVAILABLE_OK_EXTRA_KEYS = "api_available_ok_extra_keys.json";

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public MockWebServerRule mockWebServerRule = new MockWebServerRule(new FileReader());

    @Test
    public void
    throw_exception_configFileObsoleteException_on_209_response_code_and_return_surveys()
            throws IOException {
        mockWebServerRule.getMockServer().enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        mockWebServerRule.getMockServer().enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mockWebServerRule.getMockServer().getBaseEndpoint());
        eReferralsAPIClient.pushSurveys(new SurveyContainerWSObject("", "",
                        "", "", "", 2, "", "",
                        new SettingsSummary("", "", false, "", false, ""),
                        "", ""),
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
        mockWebServerRule.getMockServer().enqueueMockResponseFileName(200, API_AVAILABLE_OK_EXTRA_KEYS);
        mockWebServerRule.getMockServer().enqueueMockResponseFileName(200, PUSH_RESPONSE_OK_EXTRA_KEYS);
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mockWebServerRule.getMockServer().getBaseEndpoint());
        eReferralsAPIClient.pushSurveys(new SurveyContainerWSObject("", "",
                        "", "", "", 2, "", "", new SettingsSummary("", "", false, "", false, ""), "",""),
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

    @Test
    public void return_correct_apiAvailable() throws IOException, AvailableApiException {
        mockWebServerRule.getMockServer().enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mockWebServerRule.getMockServer().getBaseEndpoint());
        ApiAvailable apiAvailable = eReferralsAPIClient.getIfIsApiAvailable();
        assertThat(apiAvailable.isAvailable(), is(true));
        assertThat(apiAvailable.getMsg(),is("Test message"));
    }

    @Test
    public void throw_exception_availableApis_exception_if_api_not_available()
            throws IOException, AvailableApiException {
        mockWebServerRule.getMockServer().enqueueMockResponseFileName(200, API_AVAILABLE_NO_OK);
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mockWebServerRule.getMockServer().getBaseEndpoint());
        thrown.expect(AvailableApiException.class);
        eReferralsAPIClient.getIfIsApiAvailable();
    }

    @Test
    public void throw_exception_availableApis_exception_on_server_error()
            throws IOException, AvailableApiException {
        mockWebServerRule.getMockServer().enqueueMockResponse(404);
        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                mockWebServerRule.getMockServer().getBaseEndpoint());
        thrown.expect(AvailableApiException.class);
        eReferralsAPIClient.getIfIsApiAvailable();
    }
}
