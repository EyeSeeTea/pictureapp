package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.content.Context;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.layout.SurveyInfoUtils;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;

public class DashboardAdapterStrategy implements IAssessmentAdapterStrategy {

    private AssessmentAdapter mAssessmentAdapter;
    private Context mContext;

    public DashboardAdapterStrategy(Context context, AssessmentAdapter dashboardAdapter) {
        mAssessmentAdapter = dashboardAdapter;
        mContext = context;
    }

    @Override
    public void renderSurveySummary(View rowView, SurveyDB surveyDB) {
        mAssessmentAdapter.showDate(rowView, R.id.completionDate, surveyDB.getEventDate());

        mAssessmentAdapter.showRDT(rowView, R.id.rdt,
                SurveyInfoUtils.getRDTSymbol(mContext, surveyDB));

        mAssessmentAdapter.showInfo(rowView, R.id.info, surveyDB.getValuesToString());
    }
    @Override
    public boolean hasAllComplementarySurveys(SurveyDB malariaSurveyDB) {
        return true;
    }
}
