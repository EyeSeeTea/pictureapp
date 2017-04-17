package org.eyeseetea.malariacare.data.sync.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.sync.exporter.model.EReferralWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWS;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SurveyWSParser {
    public static JSONObject convertSurveysToJson(List<Survey> surveys)
            throws JsonProcessingException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        EReferralWSObject eReferralWSObject = new EReferralWSObject();
        eReferralWSObject.getReferrals().addAll(getWSConvertedSurveysList(surveys));
        JSONObject resultObject = new JSONObject(mapper.writeValueAsString(eReferralWSObject));

        JSONArray surveysJA = resultObject.getJSONArray("referral");
        for (int i = 0; i < surveysJA.length(); i++) {
            JSONArray values = new JSONArray();
            values.put(convertSurveyValuesToWS(surveys.get(i)));
            surveysJA.getJSONObject(i).put("values", values);
        }

        return resultObject;
    }

    private static List<SurveyWS> getWSConvertedSurveysList(List<Survey> surveys) {
        List<SurveyWS> surveyWSes = new ArrayList<>();
        for (Survey survey : surveys) {
            SurveyWS surveyWS = new SurveyWS(survey.getEventUid());
            surveyWSes.add(surveyWS);
        }
        return surveyWSes;
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

    public EReferralWSObject parseSendSurveysAnswer(String json) throws IOException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        EReferralWSObject eReferralWSObject = mapper.readValue(json, EReferralWSObject.class);
        JSONObject wsObject = new JSONObject(json);
        eReferralWSObject.getReferrals().addAll(
                getSurveyWSResponses(wsObject.getJSONArray("referrals")));

        return eReferralWSObject;
    }

    private List<SurveyWSResponse> getSurveyWSResponses(JSONArray referrals)
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
