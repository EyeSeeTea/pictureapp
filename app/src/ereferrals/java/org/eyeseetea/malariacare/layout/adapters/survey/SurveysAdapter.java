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
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.presentation.models.SurveyViewModel;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

public class SurveysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    public interface OnSurveyClickListener {
        void onSurveyClick(View view, SurveyViewModel surveyViewModel);
    }

    private OnSurveyClickListener onSurveyClickListener;

    List<SurveyViewModel> items;

    public SurveysAdapter(List<SurveyViewModel> items) {
        this.items = items;
    }

    public void setOnSurveyClickListener (OnSurveyClickListener listener){
        this.onSurveyClickListener = listener;
    }

    public SurveyViewModel getSurvey(int position) {
        return items.get(position);
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
        final SurveyViewModel surveyViewModel = items.get(i);

        surveyViewHolder.bindView(getSurvey(i), i);

        surveyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSurveyClickListener!= null) {
                    onSurveyClickListener.onSurveyClick(view, surveyViewModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<SurveyViewModel> items) {
        this.items = items;
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

        public void bindView(SurveyViewModel survey, int position) {
            LayoutUtils.fixRowViewBackground(itemView, position);
            LayoutUtils.setListRowBackgroundColor(itemView);

            String asterisk="";
            if(survey.isCompleted()){
                asterisk="*";
            }

            String uid = context.getString(R.string.voucher) + ":" + survey.getVoucherUid();

            if (survey.noIssueVoucher()) {
                uid = context.getString(R.string.no_voucher);
            } else if (survey.hasPhone()) {
                uid = context.getString(R.string.e_voucher);
            }

            dateText.setText(Utils.parseDateToString(survey.getEventDate(),
                    context.getString(R.string.date_survey_format)));
            hourText.setText(Utils.parseDateToString(survey.getEventDate(),
                    context.getString(R.string.hour_survey_format)));
            uidText.setText(uid);


            String firstImportant = "";
            String secondImportant = "";
            String visibleValues = "";

            if (survey.getImportantValues().size() > 0) {
                firstImportant =
                        Utils.getInternationalizedString(survey.getImportantValues().get(0));

                if (survey.getImportantValues().size() > 1) {
                    secondImportant =
                            Utils.getInternationalizedString(survey.getImportantValues().get(1));
                }
            }

            boolean first = true;

            for (String value : survey.getVisibleValues()) {
                if (first) {
                    visibleValues = Utils.getInternationalizedString(value);
                    first = false;
                } else {
                    visibleValues += ", " + Utils.getInternationalizedString(value);
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
    }
}