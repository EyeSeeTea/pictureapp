package org.eyeseetea.malariacare.layout.adapters.survey;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public class AddBalanceReceiptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_QUESTION = 0;
    private static final int VIEW_TYPE_SAVE = 1;
    private List<Question> mQuestions;
    private onInteractionListener mOnInteractionListener;

    public AddBalanceReceiptAdapter(
            List<Question> questions,
            onInteractionListener onInteractionListener) {
        mQuestions = questions;
        mOnInteractionListener = onInteractionListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_QUESTION) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_balance_receipt_row, parent, false);
            return new QuestionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_receipt_balance_save_row, parent, false);
            return new SaveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_QUESTION) {
            putValuesToQuestion(mQuestions.get(position), (QuestionViewHolder) holder);
        } else {
            initSave((SaveViewHolder) holder);
        }
    }

    private void initSave(SaveViewHolder holder) {
        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnInteractionListener.onSaveClick();
            }
        });
    }

    private void putValuesToQuestion(final Question question, final QuestionViewHolder holder) {
        holder.question.setText(question.getQuestionText());
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
        mOnInteractionListener.onQuestionAnswered(question,
                holder.value.getText().toString().isEmpty() ? holder.value.getHint().toString()
                        : holder.value.getText().toString());
    }

    @Override
    public int getItemCount() {
        return mQuestions.size() + 1;
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

    @Override
    public int getItemViewType(int position) {
        return position < mQuestions.size() ? VIEW_TYPE_QUESTION : VIEW_TYPE_SAVE;
    }

    public interface onInteractionListener {
        void onSaveClick();

        void onQuestionAnswered(Question question, String answeredValue);
    }

    public static class SaveViewHolder extends RecyclerView.ViewHolder {
        Button save;

        public SaveViewHolder(View itemView) {
            super(itemView);
            save = (Button) itemView.findViewById(R.id.save);
        }
    }
}
