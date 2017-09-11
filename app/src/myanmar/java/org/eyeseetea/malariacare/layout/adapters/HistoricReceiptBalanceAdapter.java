package org.eyeseetea.malariacare.layout.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.domain.entity.SurveyQuestionTreatmentValue;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

public class HistoricReceiptBalanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Survey> mSurveys;
    private int mType;

    private static final int HEADER_VIEW_TYPE = 0;
    private static final int ROW_VIEW_TYPE = 1;

    public HistoricReceiptBalanceAdapter(List<Survey> surveys, int type) {
        mSurveys = surveys;
        mType = type;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.historic_receipt_balance_header_title);
        }
    }

    public class RowViewHolder extends RecyclerView.ViewHolder {
        public TextView rdt, act6, act12, act18, act24, cq, pq, date;

        public RowViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.historic_receipt_balance_row_date);
            rdt = (TextView) itemView.findViewById(R.id.historic_receipt_balance_row_rdt);
            act6 = (TextView) itemView.findViewById(R.id.historic_receipt_balance_row_x6);
            act12 = (TextView) itemView.findViewById(R.id.historic_receipt_balance_row_x12);
            act18 = (TextView) itemView.findViewById(R.id.historic_receipt_balance_row_x18);
            act24 = (TextView) itemView.findViewById(R.id.historic_receipt_balance_row_x24);
            cq = (TextView) itemView.findViewById(R.id.historic_receipt_balance_row_cq);
            pq = (TextView) itemView.findViewById(R.id.historic_receipt_balance_row_pq);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.historic_receipt_balance_header_row, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.historic_receipt_balance_row, parent, false);
            return new RowViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RowViewHolder) {
            RowViewHolder rowViewHolder = (RowViewHolder) holder;
            Survey survey = mSurveys.get(position - 1);
            putValuesToRow(rowViewHolder, survey);
        }
        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            putValuesToHeader(headerViewHolder);
        }

    }

    private void putValuesToHeader(HeaderViewHolder holder) {
        if (mType == Constants.SURVEY_RESET) {
            holder.title.setText(R.string.reset_on);
        } else {
            holder.title.setText(R.string.received);
        }
    }

    private void putValuesToRow(RowViewHolder holder, Survey survey) {
        SurveyQuestionTreatmentValue surveyQuestionValue = new SurveyQuestionTreatmentValue(survey);
        holder.rdt.setText(surveyQuestionValue.getRDTValue());
        holder.act6.setText(surveyQuestionValue.getACT6Value());
        holder.act12.setText(surveyQuestionValue.getACT12Value());
        holder.act18.setText(surveyQuestionValue.getACT18Value());
        holder.act24.setText(surveyQuestionValue.getACT24Value());
        holder.pq.setText(surveyQuestionValue.getPqValue());
        holder.cq.setText(surveyQuestionValue.getCqValue());

        String date = Utils.getStringFromCalendarWithFormat(
                Utils.DateToCalendar(survey.getEventDate()), "MMM dd/yy");
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return mSurveys.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER_VIEW_TYPE : ROW_VIEW_TYPE;
    }
}
