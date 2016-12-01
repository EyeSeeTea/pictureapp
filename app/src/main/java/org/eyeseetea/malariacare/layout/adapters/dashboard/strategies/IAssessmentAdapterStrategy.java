package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.view.View;

import org.eyeseetea.malariacare.database.model.Survey;

public interface IAssessmentAdapterStrategy {
    void renderSurveySummary(View rowView, Survey survey);
}
