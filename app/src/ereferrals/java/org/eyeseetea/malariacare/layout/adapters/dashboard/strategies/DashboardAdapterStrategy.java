package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
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
        String uid = mContext.getString(R.string.voucher) +
                ":" +
                survey.getEventUid();
        String firstImportant = "", secondImportant = "", firstVisible = "", secondVisible = "";

        Context context = PreferencesState.getInstance().getContext();
        TextView dateText = (TextView) rowView.findViewById(R.id.completion_date);
        TextView hourText = (TextView) rowView.findViewById(R.id.survey_hour);
        TextView nameText = (TextView) rowView.findViewById(R.id.important_visible_text);
        TextView phoneText = (TextView) rowView.findViewById(R.id.visible_first);
        TextView programText = (TextView) rowView.findViewById(R.id.visible_second);
        TextView uidText = (TextView) rowView.findViewById(R.id.survey_uid);
        dateText.setText(Utils.parseDateToString(survey.getEventDate(),
                context.getString(R.string.date_survey_format)));
        hourText.setText(Utils.parseDateToString(survey.getEventDate(),
                context.getString(R.string.hour_survey_format)));
        uidText.setText(uid);

        List<QuestionDB> important = new ArrayList<>();
        List<QuestionDB> visible = new ArrayList<>();

        for (ValueDB value : survey.getValueDBs()) {
            if (value.getQuestionDB().isImportant()) {
                important.add(value.getQuestionDB());
            } else if (value.getQuestionDB().isVisible()) {
                visible.add(value.getQuestionDB());
            }
        }
        Collections.sort(important, new QuestionDB.QuestionOrderComparator());
        Collections.sort(visible, new QuestionDB.QuestionOrderComparator());

        if (important.size() > 1) {
            firstImportant = important.get(0).getValueBySurvey(survey).getValue();
            secondImportant = important.get(1).getValueBySurvey(survey).getValue();
        } else if (!important.isEmpty()) {
            firstImportant = important.get(0).getValueBySurvey(survey).getValue();
        }
        if (visible.size() > 1) {
            firstVisible = visible.get(0).getValueBySurvey(survey).getValue();
            OptionDB optionSelected = OptionDB.findByCode(visible.get(1).getValueBySurvey(
                    survey).getValue());
            secondVisible = optionSelected != null ? optionSelected.getInternationalizedName() :
                    visible.get(1).getValueBySurvey(
                            survey).getValue();
        } else if (!visible.isEmpty()) {
            OptionDB optionSelected = OptionDB.findByCode(visible.get(0).getValueBySurvey(
                    survey).getValue());
            secondVisible = optionSelected != null ? optionSelected.getInternationalizedName() :
                    visible.get(0).getValueBySurvey(
                            survey).getValue();
        }
        nameText.setText(firstImportant + " " + secondImportant);
        phoneText.setText(firstVisible);
        programText.setText(secondVisible);

    }
    @Override
    public boolean hasAllComplementarySurveys(SurveyDB malariaSurvey) {
        return true;
    }
}

