package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;

public interface IAssessmentAdapterStrategy {
    void renderSurveySummary(View rowView, SurveyDB surveyDB);
    boolean hasAllComplementarySurveys(SurveyDB malariaSurveyDB);

}
