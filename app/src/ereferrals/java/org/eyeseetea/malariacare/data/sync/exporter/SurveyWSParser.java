package org.eyeseetea.malariacare.data.sync.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.sync.exporter.model.AttributeValueWS;
import org.eyeseetea.malariacare.data.sync.exporter.model.EReferralWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySendAction;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SurveyWSParser {

    private static final String SURVEY_ACTION_ID = "issueReferral";

    public static JSONObject convertSurveysToJson(String version, String source,
            Credentials credentials, List<Survey> surveys)
            throws JsonProcessingException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        EReferralWSObject eReferralWSObject = new EReferralWSObject();
        eReferralWSObject.setVersion(version);
        eReferralWSObject.setSource(source);
        eReferralWSObject.setUserName(credentials.getUsername());
        eReferralWSObject.setPassword(credentials.getPassword());
        eReferralWSObject.setActions(getWSConvertedSurveysList(surveys));
        return new JSONObject(mapper.writeValueAsString(eReferralWSObject));
    }

    private static List<SurveySendAction> getWSConvertedSurveysList(List<Survey> surveys) {
        List<SurveySendAction> surveySendActions = new ArrayList<>();
        for (Survey survey : surveys) {

            SurveySendAction surveySendAction = new SurveySendAction();
            surveySendAction.setActionId(CodeGenerator.generateCode());
            surveySendAction.setType(SURVEY_ACTION_ID);
            surveySendAction.setAttributeValues(getValuesWSFromSurvey(survey));
            surveySendActions.add(surveySendAction);
        }
        return surveySendActions;
    }

    private static List<AttributeValueWS> getValuesWSFromSurvey(Survey survey) {
        List<AttributeValueWS> valueWSes = new ArrayList<>();
        for (Value value : survey.getValuesFromDB()) {
            if (value.getQuestion() != null) {
                if (value.getOption() == null) {
                    valueWSes.add(
                            new AttributeValueWS(value.getQuestion().getCode(), value.getValue()));
                } else {
                    valueWSes.add(new AttributeValueWS(value.getQuestion().getCode(),
                            value.getOption().getCode()));
                }
            }
        }
        return valueWSes;
    }

    public static SurveyWSResult parseSendSurveysAnswer(String json)
            throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        SurveyWSResult surveyWSResult = mapper.readValue(json, SurveyWSResult.class);
        return surveyWSResult;
    }


}
