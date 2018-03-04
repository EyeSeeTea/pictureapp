package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class StockSurveysAdapter extends BaseAdapter {

    protected List<SurveyDB> surveys;
    protected LayoutInflater mInflater;
    protected Context mContext;

    public StockSurveysAdapter(
            List<SurveyDB> surveys, LayoutInflater lInflater, Context context) {
        this.surveys = surveys;
        this.mInflater = lInflater;
        mContext = context;
    }

    @Override
    public int getCount() {
        return surveys.size();
    }

    @Override
    public Object getItem(int position) {
        return surveys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return surveys.get(position).getId_survey();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.stock_list_row, null);
            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(R.id.date_line);
            viewHolder.drugs = (TextView) convertView.findViewById(R.id.drugs_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        putValuesToRow(viewHolder, position);
        return convertView;
    }

    private void putValuesToRow(ViewHolder viewHolder, int position) {
        SurveyDB survey = surveys.get(position);

        String date = Utils.parseDateToString(survey.getEventDate(),
                mContext.getString(R.string.date_survey_format));
        String hour = Utils.parseDateToString(survey.getEventDate(),
                mContext.getString(R.string.hour_survey_format));

        List<ValueDB> visible = new ArrayList<>();

        for (ValueDB value : survey.getValueDBs()) {
            if (value.getQuestionDB() != null) {
                if (value.getQuestionDB().isVisible()) {
                    visible.add(value);
                }
            }
        }

        String visibleValues = "";
        boolean first = true;
        for (ValueDB value : visible) {
            if (first) {
                first = false;
            } else {
                visibleValues += ", ";
            }
            if (value.getQuestionDB() != null) {
                visibleValues += value.getQuestionDB().getInternationalizedForm_name() + " : ";
            }

            if (value.getOptionDB() != null) {
                visibleValues += value.getOptionDB().getInternationalizedName();
            } else {
                visibleValues += value.getValue();
            }
        }

        String dateText = "[ " + date + " - " + hour + " ] ";
        viewHolder.date.setText(dateText);
        viewHolder.drugs.setText(visibleValues);
        viewHolder.stockImage.setImageDrawable(
                mContext.getResources().getDrawable(getImageForSurvey(survey)));

    }

    private int getImageForSurvey(SurveyDB survey) {
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

    static class ViewHolder {
        ImageView stockImage;
        TextView date;
        TextView drugs;
    }
}
