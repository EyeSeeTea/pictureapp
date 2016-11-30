package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.content.Context;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AAssessmentAdapter;

import java.util.List;

public class DashboardAdapterStrategy implements IDashboardAdapterStrategy {

    private AAssessmentAdapter mAssessmentAdapter;
    private Context mContext;

    private static String PATIENT_RESIDENCE_VILLAGE_CODE_QUESTION = "Code: PR";
    private static String IF_OTHER_ENTER_LOCATION_CODE_QUESTION = "Code: PO";
    private static String PATIENT_RESIDENCE_VILLAGE_OTHER_OPTION_CODE = "Other";

    public DashboardAdapterStrategy(Context context, AAssessmentAdapter assessmentAdapter) {
        mAssessmentAdapter = assessmentAdapter;
        mContext = context;
    }

    @Override
    public void renderSurveySummary(View rowView, Survey survey) {
        mAssessmentAdapter.showDate(rowView, R.id.completionDate, survey.getEventDate());

        String info = getQuestionInfo(survey);

        mAssessmentAdapter.showInfo(rowView, R.id.info, info);
    }

    private String getQuestionInfo(Survey survey) {
        String info;
        String visibleValues = survey.getValuesToString();

        String location = getLocation(survey);

        if (location.isEmpty()) {
            info = visibleValues;
        } else {
            info = location + ", " + visibleValues;
        }

        return info;
    }

    private String getLocation(Survey survey) {
        List<Value> values = survey.getValues();

        String location = "";
        Value patientResidenceVillageValue = null;
        Value manualLocationValue = null;

        for (Value value : values) {
            if (value.getQuestion().getCode().equals(PATIENT_RESIDENCE_VILLAGE_CODE_QUESTION)) {
                patientResidenceVillageValue = value;
            } else if (value.getQuestion().getCode().equals(
                    IF_OTHER_ENTER_LOCATION_CODE_QUESTION)) {
                manualLocationValue = value;
            }
        }

        if (patientResidenceVillageValue != null) {
            if (!patientResidenceVillageValue.getOption().getCode().equals(
                    PATIENT_RESIDENCE_VILLAGE_OTHER_OPTION_CODE)) {
                location = patientResidenceVillageValue.getOption().getInternationalizedCode();
            } else {
                if (manualLocationValue != null) {
                    location = manualLocationValue.getValue();
                }
            }
        }

        return location;
    }
}
