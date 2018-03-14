package org.eyeseetea.malariacare.layout.adapters.survey;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

public class AddBalanceReceiptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Question> mQuestions;
    private onInteractionListener mOnInteractionListener;
    private String mDefValue;

    public AddBalanceReceiptAdapter(
            List<Question> questions,
            onInteractionListener onInteractionListener, String defValue) {
        mQuestions = questions;
        mOnInteractionListener = onInteractionListener;
        mDefValue = defValue;
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

    private void putValuesToQuestion(final Question question, final QuestionViewHolder holder) {
        holder.question.setText(
                Utils.getInternationalizedString(question.getName(), holder.question.getContext()));
        holder.value.setHint(mDefValue);
        holder.value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mOnInteractionListener.onQuestionAnswered(question,
                        holder.value.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mOnInteractionListener.onQuestionAnswered(question, holder.value.getText().toString());
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

    public interface onInteractionListener {

        void onQuestionAnswered(Question question, String answeredValue);
    }

}
