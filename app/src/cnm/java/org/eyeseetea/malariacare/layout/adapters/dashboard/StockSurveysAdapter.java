package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

public class StockSurveysAdapter extends RecyclerView.Adapter<StockSurveysAdapter.SurveyHolder> {

    protected List<Survey> surveys;
    protected Context mContext;

    public StockSurveysAdapter(Context context) {
        mContext = context;
    }

    public void addSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }

    @Override
    public SurveyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_row,
                parent, false);
        return new SurveyHolder(rowView);
    }

    @Override
    public void onBindViewHolder(SurveyHolder holder, int position) {
        Survey survey = surveys.get(position);

        String date = Utils.parseDateToString(survey.getSurveyDate(),
                mContext.getString(R.string.date_survey_format));
        String hour = Utils.parseDateToString(survey.getSurveyDate(),
                mContext.getString(R.string.hour_survey_format));

        List<Question> questions = survey.getQuestions();

        String visibleValues = "";
        boolean first = true;
        for (Question question : questions) {
            if (first) {
                first = false;
            } else {
                visibleValues += ", ";
            }
            visibleValues += Utils.getInternationalizedString(question.getName(), mContext) + " : ";

            visibleValues += question.getValue().getValue();
        }

        String dateText = "[ " + date + " - " + hour + " ] ";
        holder.date.setText(dateText);
        holder.drugs.setText(visibleValues);
        holder.stockImage.setImageDrawable(
                mContext.getResources().getDrawable(getImageForSurvey(survey)));
        holder.notSent.setVisibility(isSurveyUnsent(survey.getStatus()) ? View.VISIBLE : View.GONE);
    }

    @Override
    public long getItemId(int position) {
        return surveys.get(position).getId();
    }

    @Override
    public int getItemCount() {
        if (surveys == null) {
            return 0;
        }
        return surveys.size();
    }

    private boolean isSurveyUnsent(int surveyStatus) {
        return surveyStatus == Constants.SURVEY_IN_PROGRESS
                || surveyStatus == Constants.SURVEY_SENDING
                || surveyStatus == Constants.SURVEY_COMPLETED
                || surveyStatus==Constants.SURVEY_QUARANTINE;
    }

    private int getImageForSurvey(Survey survey) {
        switch (survey.getType()) {
            case Constants.SURVEY_RECEIPT:
                return R.drawable.ic_arrow_survey_receipt;
            case Constants.SURVEY_RESET:
                return R.drawable.ic_sheet_survey_balance;
            case Constants.SURVEY_ISSUE:
                return R.drawable.ic_arrow_survey_expense;
        }
        return R.drawable.ic_arrow_survey_expense;
    }



    public class SurveyHolder extends RecyclerView.ViewHolder {
        ImageView stockImage;
        TextView date;
        TextView drugs;
        TextView notSent;

        public SurveyHolder(View itemView) {
            super(itemView);
            stockImage = (ImageView) itemView.findViewById(R.id.image_stock);
            date = (TextView) itemView.findViewById(R.id.date_line);
            drugs = (TextView) itemView.findViewById(R.id.drugs_line);
            notSent = (TextView) itemView.findViewById(R.id.not_sent);
        }
    }
}
