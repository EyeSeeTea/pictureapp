package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;


import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.utils.Constants;
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
        String asterisk="";
        if(survey.isCompleted()){
            asterisk="*";
        }

        String uid = mContext.getString(R.string.voucher) +
                ":" +
                survey.getEventUid();
        if (noIssueVoucher(survey)) {
            uid = mContext.getString(R.string.no_voucher);
        } else if (hasPhone(survey)) {
            uid = mContext.getString(R.string.e_voucher);
        }


        String firstImportant = "", secondImportant = "", visibleValues = "";

        Context context = PreferencesState.getInstance().getContext();
        LinearLayout container = (LinearLayout) rowView.findViewById(R.id.assessment_row);
        TextView dateText = (TextView) rowView.findViewById(R.id.completion_date);
        TextView hourText = (TextView) rowView.findViewById(R.id.survey_hour);
        TextView importantText = (TextView) rowView.findViewById(R.id.important_visible_text);
        TextView visibleValuesText = (TextView) rowView.findViewById(R.id.visible_values);
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
        boolean first = true;
        for (QuestionDB question : visible) {
            OptionDB optionSelected = OptionDB.findByCode(question.getValueBySurvey(
                    survey).getValue());
            String valueToShow;
            if (optionSelected != null) {
                valueToShow = optionSelected.getInternationalizedName();
            } else {
                valueToShow = question.getValueBySurvey(survey).getValue();
            }
            if (first) {
                visibleValues = valueToShow;
                first = false;
            } else {
                visibleValues += ", " + valueToShow;
            }
        }
        importantText.setText(asterisk + firstImportant + " " + secondImportant);
        visibleValuesText.setText(visibleValues);

        rowView.setBackgroundColor(
                context.getResources().getColor(getColorForStatus(survey.getStatus())));
    }

    private int getColorForStatus(Integer status) {
        switch (status) {
            case Constants.SURVEY_IN_PROGRESS:
                return R.color.gray_survey_in_progress;
            case Constants.SURVEY_COMPLETED:
                return R.color.gray_survey_completed;
            case Constants.SURVEY_SENT:
                return R.color.green_survey_sent;
            case Constants.SURVEY_CONFLICT:
                return R.color.red_survey_conflict;
            case Constants.SURVEY_QUARANTINE:
                return R.color.brown_survey_quarantine;
            case Constants.SURVEY_SENDING:
                return R.color.blue_survey_sending;
        }
        return R.color.grey_tab_unselected_ereferrals;
    }

    @Override
    public boolean hasAllComplementarySurveys(SurveyDB malariaSurvey) {
        return true;
    }

    private boolean noIssueVoucher(SurveyDB survey) {
        OptionDB noIssueOption = survey.getOptionSelectedForQuestionCode(
                mContext.getString(R.string.issue_voucher_qc));
        if (noIssueOption == null) {
            return false;
        }
        return noIssueOption.getName().equals(
                mContext.getString(R.string.no_voucher_on));
    }

    private boolean hasPhone(SurveyDB survey) {
        Context context = PreferencesState.getInstance().getContext();
        OptionDB optionDB = (survey.getOptionSelectedForQuestionCode(
                context.getString(R.string.phone_ownership_qc)));
        return optionDB != null && !optionDB.getName().equals(
                context.getString(R.string.no_phone_on));
        }
}

