package org.eyeseetea.malariacare.layout.adapters.survey;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public class AddBalanceReceiptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Question> mQuestions;

    public AddBalanceReceiptAdapter(List<Question> questions) {
        mQuestions = questions;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.add_balance_receipt_row, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        putValuesToQuestion(mQuestions.get(position), (QuestionViewHolder) holder);
    }

    private void putValuesToQuestion(Question question, QuestionViewHolder holder) {
        holder.question.setText(question.getQuestionText());
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        EditText value;

        public QuestionViewHolder(View itemView) {
            super(itemView);
            question = (TextView) itemView.findViewById(R.id.question);
            value = (EditText) itemView.findViewById(R.id.value);
        }
    }


}
