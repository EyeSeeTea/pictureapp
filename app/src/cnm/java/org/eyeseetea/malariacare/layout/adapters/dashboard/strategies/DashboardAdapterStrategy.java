package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DashboardAdapterStrategy implements IAssessmentAdapterStrategy {

    private AssessmentAdapter mAssessmentAdapter;
    private Context mContext;

    public DashboardAdapterStrategy(Context context, AssessmentAdapter dashboardAdapter) {
        mAssessmentAdapter = dashboardAdapter;
        mContext = context;
    }

    @Override
    public void renderSurveySummary(View rowView, SurveyDB survey) {
        TextView firstLine = (TextView) rowView.findViewById(R.id.first_line);
        TextView secondLine = (TextView) rowView.findViewById(R.id.second_line);

        String date = Utils.parseDateToString(survey.getEventDate(),
                mContext.getString(R.string.date_survey_format));
        String hour = Utils.parseDateToString(survey.getEventDate(),
                mContext.getString(R.string.hour_survey_format));

        List<ValueDB> important = new ArrayList<>();
        List<ValueDB> visible = new ArrayList<>();

        for (ValueDB value : survey.getValueDBs()) {
            if (value.getQuestionDB().isImportant()) {
                important.add(value);
            } else if (value.getQuestionDB().isVisible()) {
                visible.add(value);
            }
        }

        String importantValues = "";
        String visibleValues = "";
        boolean first = true;
        for (ValueDB value : important) {
            if (first) {
                first = false;
            } else {
                importantValues += ", ";
            }
            if (value.getOptionDB() != null) {
                importantValues += value.getOptionDB().getInternationalizedName();
            } else {
                importantValues += value.getValue();
            }
        }
        first = true;
        for (ValueDB value : visible) {
            if (first) {
                first = false;
            } else {
                visibleValues += ", ";
            }
            if (value.getOptionDB() != null) {
                visibleValues += value.getOptionDB().getInternationalizedName();
            } else {
                visibleValues += value.getValue();
            }
        }

        String firstText = date + " / " + hour + " / " + importantValues;
        firstLine.setText(firstText);
        secondLine.setText(visibleValues);


    }
    @Override
    public boolean hasAllComplementarySurveys(SurveyDB malariaSurvey) {
        return true;
    }
}
