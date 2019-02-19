/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.layout.adapters.survey;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SurveysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    public interface OnSurveyClickListener {
        void onSurveyClick(View view, SurveyDB surveyDB);
    }

    private OnSurveyClickListener onSurveyClickListener;

    List<SurveyDB> items;

    public SurveysAdapter(List<SurveyDB> items) {
        this.items = items;
    }

    public void setOnSurveyClickListener (OnSurveyClickListener listener){
        this.onSurveyClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_survey, viewGroup, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        SurveyViewHolder surveyViewHolder = (SurveyViewHolder) viewHolder;
        final SurveyDB surveyDB = items.get(i);

        surveyViewHolder.bindView(items.get(i), i);

        surveyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSurveyClickListener!= null) {
                    onSurveyClickListener.onSurveyClick(view, surveyDB);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List items) {
        this.items = (List<SurveyDB>) items;
        super.notifyDataSetChanged();
    }

    public void remove(Object item) {
        this.items.remove(item);
    }


    public class SurveyViewHolder extends RecyclerView.ViewHolder {
        private final TextView dateText;
        private final TextView hourText;

        private final TextView importantText;

        private final TextView visibleValuesText;
        private final TextView uidText;

        private final Context context;


        public SurveyViewHolder(View view) {
            super(view);
            context = view.getContext();

            dateText = view.findViewById(R.id.completion_date);
            hourText = view.findViewById(R.id.survey_hour);
            importantText = view.findViewById(R.id.important_visible_text);
            visibleValuesText = view.findViewById(R.id.visible_values);
            uidText = view.findViewById(R.id.survey_uid);
        }

        @Override
        public String toString() {
            return super.toString();
        }

        public void bindView(SurveyDB survey, int position) {
            LayoutUtils.fixRowViewBackground(itemView, position);
            LayoutUtils.setListRowBackgroundColor(itemView);


            String asterisk="";
            if(survey.isCompleted()){
                asterisk="*";
            }

            String uid = context.getString(R.string.voucher) +
                    ":" +
                    survey.getVoucherUid();
            if (noIssueVoucher(survey)) {
                uid = context.getString(R.string.no_voucher);
            } else if (hasPhone(survey)) {
                uid = context.getString(R.string.e_voucher);
            }


            String firstImportant = "", secondImportant = "", visibleValues = "";

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

            itemView.setBackgroundColor(
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

        private boolean noIssueVoucher(SurveyDB survey) {
            OptionDB noIssueOption = survey.getOptionSelectedForQuestionCode(
                    context.getString(R.string.issue_voucher_qc));
            if (noIssueOption == null) {
                return false;
            }
            return noIssueOption.getName().equals(
                    context.getString(R.string.no_voucher_on));
        }

        private boolean hasPhone(SurveyDB survey) {
            Context context = PreferencesState.getInstance().getContext();
            OptionDB optionDB = (survey.getOptionSelectedForQuestionCode(
                    context.getString(R.string.phone_ownership_qc)));
            return optionDB != null && !optionDB.getName().equals(
                    context.getString(R.string.no_phone_on));
        }
    }
}