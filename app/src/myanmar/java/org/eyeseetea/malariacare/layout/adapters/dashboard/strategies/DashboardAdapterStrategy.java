package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
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
    public boolean hasComplementarySurveyNeeded(Survey malariaSurvey) {
        Survey stockSurvey = Survey.getStockSurveyWithCreationDate(malariaSurvey.getCreationDate());
        if (stockSurvey != null) {
            Session.setStockSurvey(stockSurvey);
            return true;
        } else {
            Toast.makeText(mContext,
                    PreferencesState.getInstance().getContext().getString(
                            R.string.error_survey_not_has_stock_survye_asociated),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
