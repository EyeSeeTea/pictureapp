package org.eyeseetea.malariacare.data.sync.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.sync.exporter.model.EReferralWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWS;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SurveyWSParser {
    public static JSONObject convertSurveysToJson(List<Survey> surveys)
            throws JsonProcessingException, JSONException {
        ObjectMapper mapper = new ObjectMapper();
        EReferralWSObject eReferralWSObject = new EReferralWSObject();
        eReferralWSObject.getReferrals().addAll(getWSConvertedSurveysList(surveys));

        return new JSONObject(mapper.writeValueAsString(eReferralWSObject));
    }

    private static List<SurveyWS> getWSConvertedSurveysList(List<Survey> surveys) {
        List<SurveyWS> surveyWSes = new ArrayList<>();
        for (Survey survey : surveys) {
            SurveyWS surveyWS = new SurveyWS(survey.getEventUid());
            List<Object> values = convertSurveyValuesToWS(survey);
            surveyWSes.add(surveyWS);
        }
        return surveyWSes;
    }

    private static List<Object> convertSurveyValuesToWS(Survey survey) {
        return new ArrayList<>();
    }
}
