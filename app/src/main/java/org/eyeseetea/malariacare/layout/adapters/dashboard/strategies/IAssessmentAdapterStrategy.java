package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.model.Survey;

public interface IAssessmentAdapterStrategy {
    void renderSurveySummary(View rowView, Survey survey);
    boolean hasAllComplementarySurveys(Survey malariaSurvey);

}
