package org.eyeseetea.malariacare.data.sync.exporter;

import static junit.framework.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.rules.PreferencesDependenciesRules;
import org.eyeseetea.malariacare.utils.Constants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class PushUseCaseShould {

    @Mock
    ICredentialsRepository mCredentialsRepository;
    @Mock
    IDeviceRepository deviceDataSource;
    @Mock
    IAppInfoRepository appInfoDataSource;
    @Mock
    IProgramRepository programRepository;

    @Rule
    public PreferencesDependenciesRules preferencesDependenciesRules = new PreferencesDependenciesRules();

    private static final String PUSH_RESPONSE_OK_ONE_SURVEY = "push_response_ok_one_survey.json";
    private static final String API_AVAILABLE_OK = "api_available_ok.json";
    private static final String API_AVAILABLE_NO_OK = "api_available_no_ok.json";
    private Context mContext;

    @Before
    public void cleanUp() throws IOException {
        mContext = InstrumentationRegistry.getTargetContext();
    }


    @Test
    public void call_on_push_error_when_ape_is_not_available_during_push() throws IOException {
        PushUseCase pushUseCase = preferencesDependenciesRules.givenPushUseCase(appInfoDataSource, deviceDataSource, mCredentialsRepository, programRepository);
        preferencesDependenciesRules.getMockServer().enqueueMockResponseFileName(200, API_AVAILABLE_NO_OK);
        preferencesDependenciesRules.getMockServer().enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        final SurveyDB surveyDB = new SurveyDB(new OrgUnitDB(""), new ProgramDB(""),
                new UserDB("", ""));
        surveyDB.setVoucherUid("1323544116");
        surveyDB.setEventUid("testEventUID");
        surveyDB.setStatus(Constants.SURVEY_COMPLETED);
        surveyDB.save();
        pushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onPushStart() {
            }

            @Override
            public void onComplete() {
                fail("onComplete");
            }

            @Override
            public void onPushError() {
                Assert.assertTrue(true);
            }

            @Override
            public void onPushInProgressError() {
                fail("onPushInProgressError");
            }

            @Override
            public void onSurveysNotFoundError() {
                fail("onSurveyNotFound");
            }

            @Override
            public void onConversionError() {
                fail("onConversionError");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onInformativeError(String message) {
                fail("onInformativeMessage: " + message);
            }

            @Override
            public void onInformativeMessage(String message) {
                fail("onInformativeMessage" + message);
            }

            @Override
            public void onClosedUser() {
                fail("onClosedUser");
            }

            @Override
            public void onBannedOrgUnit() {
                fail("onBannedOrgUnit");
            }

            @Override
            public void onReOpenOrgUnit() {
                fail("onReOpenOrgUnit");
            }

            @Override
            public void onApiCallError() {
                fail("onApiCallError");
            }

            @Override
            public void onApiCallError(ApiCallException e) {
                fail("onApiCallError");
            }
        });
    }

    @Test
    public void setUserCanAddSurveysToFalseOn209Response() throws IOException, InterruptedException {
        PushUseCase pushUseCase = preferencesDependenciesRules.givenPushUseCase(appInfoDataSource, deviceDataSource, mCredentialsRepository, programRepository);
        preferencesDependenciesRules.getMockServer().enqueueMockResponseFileName(200, API_AVAILABLE_OK);
        preferencesDependenciesRules.getMockServer().enqueueMockResponseFileName(209, PUSH_RESPONSE_OK_ONE_SURVEY);
        final SurveyDB surveyDB = new SurveyDB(new OrgUnitDB(""), new ProgramDB(""),
                new UserDB("", ""));
        surveyDB.setVoucherUid("1323544116");
        surveyDB.setEventUid("testEventUID");
        surveyDB.setStatus(Constants.SURVEY_COMPLETED);
        surveyDB.save();
        pushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onPushStart() {

            }

            @Override
            public void onComplete() {
                List<SurveyDB> surveys = SurveyDB.getAllSurveys();
                assertThat(surveys.get(0).getStatus(), is(Constants.SURVEY_SENT));
            }

            @Override
            public void onPushError() {
                fail("onPushError");
            }

            @Override
            public void onPushInProgressError() {
                fail("onPushInProgressError");
            }

            @Override
            public void onSurveysNotFoundError() {
                fail("onSurveyNotFound");
            }

            @Override
            public void onConversionError() {
                fail("onConversionError");
            }

            @Override
            public void onNetworkError() {
                fail("onNetworkError");
            }

            @Override
            public void onInformativeError(String message) {
                fail("onInformativeError: " + message);
            }

            @Override
            public void onInformativeMessage(String message) {
                fail("onInformativeMessage: " + message);
            }

            @Override
            public void onClosedUser() {
                fail("onClosedUser");
            }

            @Override
            public void onBannedOrgUnit() {
                fail("onBannedOrgUnit");
            }

            @Override
            public void onReOpenOrgUnit() {
                fail("onReOpenOrgUnit");
            }

            @Override
            public void onApiCallError() {
                fail("onApiCallError");
            }

            @Override
            public void onApiCallError(ApiCallException e) {
                UserAccountDataSource userAccountDataSource = new UserAccountDataSource();
                assertThat(userAccountDataSource.getLoggedUser().canAddSurveys(), is(false));
            }
        });

    }
}
