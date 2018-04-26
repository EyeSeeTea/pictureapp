package org.eyeseetea.malariacare.layout.adapters.survey;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.DrugValues;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

public class StockTableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int FIRST_ROW = 0;
    private static final int VALUES_ROW = 1;


    private List<DrugValues> mDrugValues;

    public StockTableAdapter(
            List<DrugValues> drugValues) {
        mDrugValues = drugValues;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FIRST_ROW) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stock_table_first_row, parent, false);
            return new FirstRowViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.stock_table_row, parent, false);
            return new DrugValuesViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position > 0) {
            putValuesToRow(mDrugValues.get(position - 1), (DrugValuesViewHolder) holder);
        }
    }

    private void putValuesToRow(DrugValues drugValues, DrugValuesViewHolder holder) {
        holder.drugLabel.setText(Utils.getInternationalizedString(drugValues.getDrugLabel()));
        holder.received.setText(getStringForValue(drugValues.getReceived()));
        holder.used.setText(getStringForValue(drugValues.getUsedToday()));
        holder.delivered.setText(getStringForValue(drugValues.getExpense()));
        holder.total.setText(getStringForValue(drugValues.getTotal()));
    }

    private String getStringForValue(int value) {
        return value == 0 ? "-" : String.valueOf(value);
    }

    public void replaceValues(List<DrugValues> drugValues) {
        mDrugValues = drugValues;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDrugValues.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? FIRST_ROW : VALUES_ROW;
    }

    public static class FirstRowViewHolder extends RecyclerView.ViewHolder {

        public FirstRowViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class DrugValuesViewHolder extends RecyclerView.ViewHolder {
        TextView drugLabel;
        TextView received;
        TextView used;
        TextView delivered;
        TextView total;

        public DrugValuesViewHolder(View itemView) {
            super(itemView);
            drugLabel = (TextView) itemView.findViewById(R.id.drug_label);
            received = (TextView) itemView.findViewById(R.id.text_received);
            used = (TextView) itemView.findViewById(R.id.text_used);
            delivered = (TextView) itemView.findViewById(R.id.text_delivered);
            total = (TextView) itemView.findViewById(R.id.text_total);
        }
    }
}
