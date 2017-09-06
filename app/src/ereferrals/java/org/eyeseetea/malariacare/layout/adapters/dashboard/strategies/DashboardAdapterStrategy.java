package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.utils.Utils;

public class DashboardAdapterStrategy implements IAssessmentAdapterStrategy {

    private AssessmentAdapter mAssessmentAdapter;
    private Context mContext;

    public DashboardAdapterStrategy(Context context, AssessmentAdapter dashboardAdapter) {
        mAssessmentAdapter = dashboardAdapter;
        mContext = context;
    }

    @Override
    public void renderSurveySummary(View rowView, SurveyDB survey) {
        String firstRowBuilder = mContext.getString(R.string.uid) +
                ":" +
                survey.getEventUid();
        String name = "", surname = "", phone = "", program = "";

        Context context = PreferencesState.getInstance().getContext();
        TextView dateText = (TextView) rowView.findViewById(R.id.completion_date);
        TextView hourText = (TextView) rowView.findViewById(R.id.survey_hour);
        TextView nameText = (TextView) rowView.findViewById(R.id.survey_name);
        TextView phoneText = (TextView) rowView.findViewById(R.id.survey_phone);
        TextView programText = (TextView) rowView.findViewById(R.id.survey_program);
        dateText.setText(Utils.parseDateToString(survey.getEventDate(), "dd/MMM/yy"));
        hourText.setText(Utils.parseDateToString(survey.getEventDate(), "hh:mm a"));

        for (ValueDB value : survey.getValueDBs()) {
            if (value.getQuestionDB().getCode().equals(context.getString(R.string.first_name_qc))) {
                name = value.getValue();
            } else if (value.getQuestionDB().getCode().equals(
                    context.getString(R.string.surname_qc))) {
                surname = value.getValue();
            } else if (value.getQuestionDB().getCode().equals(
                    context.getString(R.string.phone_qc))) {
                phone = value.getValue();
            } else if (value.getQuestionDB().getCode().equals(
                    context.getString(R.string.program_qc))) {
                program = value.getValue();
            }
        }
        nameText.setText(name + " " + surname);
        phoneText.setText(phone);
        programText.setText(program);
    }
    @Override
    public boolean hasAllComplementarySurveys(SurveyDB malariaSurvey) {
        return true;
    }
}

