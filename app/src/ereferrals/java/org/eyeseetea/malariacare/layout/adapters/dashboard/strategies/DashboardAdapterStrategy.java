package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.content.Context;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;

public class DashboardAdapterStrategy implements IAssessmentAdapterStrategy {

    private AssessmentAdapter mAssessmentAdapter;
    private Context mContext;

    public DashboardAdapterStrategy(Context context, AssessmentAdapter dashboardAdapter) {
        mAssessmentAdapter = dashboardAdapter;
        mContext = context;
    }

    @Override
    public void renderSurveySummary(View rowView, Survey survey) {
        mAssessmentAdapter.showDate(rowView, R.id.completionDate, survey.getEventDate());

        mAssessmentAdapter.showInfo(rowView, R.id.info, survey.getValuesToString());
    }
    @Override
    public boolean hasAllComplementarySurveys(Survey malariaSurvey) {
        return true;
    }
}

