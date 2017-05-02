package org.eyeseetea.malariacare.data.sync.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.sync.exporter.model.Action;
import org.eyeseetea.malariacare.data.sync.exporter.model.AttributeValueWS;
import org.eyeseetea.malariacare.data.sync.exporter.model.EReferralWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResponse;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;
import org.json.JSONArray;
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

    private static List<Action> getWSConvertedSurveysList(List<Survey> surveys) {
        List<Action> actions = new ArrayList<>();
        for (Survey survey : surveys) {

            Action action = new Action();
            action.setActionId(CodeGenerator.generateCode());
            action.setType(SURVEY_ACTION_ID);
            action.setAttributeValues(getValuesWSFromSurvey(survey));
            actions.add(action);
        }
        return actions;
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

    private static JSONObject convertSurveyValuesToWS(Survey survey) throws JSONException {
        JSONObject surveyValues = new JSONObject();
        for (Value value : survey.getValuesFromDB()) {
            if (value.getQuestion() != null) {
                if (value.getOption() == null) {
                    surveyValues.put(value.getQuestion().getCode(), value.getValue());
                } else {
                    surveyValues.put(value.getQuestion().getCode(), value.getOption().getCode());
                }
            }
        }
        return surveyValues;
    }

    public static EReferralWSObject parseSendSurveysAnswer(String json)
            throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        EReferralWSObject eReferralWSObject = mapper.readValue(json, EReferralWSObject.class);
        JSONObject wsObject = new JSONObject(json);
//        eReferralWSObject.getReferrals().addAll(
//                getSurveyWSResponses(wsObject.getJSONArray("referrals")));

        return eReferralWSObject;
    }

    private static List<SurveyWSResponse> getSurveyWSResponses(JSONArray referrals)
            throws JSONException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<SurveyWSResponse> surveyWSResponses = new ArrayList<>();
        for (int i = 0; referrals != null && i < referrals.length(); i++) {
            SurveyWSResponse surveyWSResponse = mapper.readValue(
                    referrals.getJSONObject(i).toString(), SurveyWSResponse.class);
            surveyWSResponses.add(surveyWSResponse);
        }
        return surveyWSResponses;
    }
}
