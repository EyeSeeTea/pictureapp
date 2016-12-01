package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.content.Context;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.layout.SurveyInfoUtils;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;

import java.util.List;

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

        //mAssessmentAdapter.showRDT(rowView, R.id.rdt,
          //      SurveyInfoUtils.getRDTSymbol(mContext, survey));

        mAssessmentAdapter.showInfo(rowView, R.id.info, survey.getValuesToString());
    }
}
