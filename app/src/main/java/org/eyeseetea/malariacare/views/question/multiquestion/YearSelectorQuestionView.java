package org.eyeseetea.malariacare.views.question.multiquestion;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.layout.listeners.question.QuestionAnswerChangedListener;
import org.eyeseetea.malariacare.views.question.CommonQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.dialogs.YearPicker;

public class YearSelectorQuestionView extends CommonQuestionView implements IQuestionView,
        IMultiQuestionView {

    TextView header;
    TextView yearText;
    private QuestionAnswerChangedListener mOnAnswerChangedListener;
    private Activity mActivity;
    private String TAG = "YearPicker";
    private boolean enabled;

    public YearSelectorQuestionView(Context context) {
        super(context);
        mActivity = (Activity) context;
        enabled = true;
        init(context);
    }


    public void setOnAnswerChangedListener(QuestionAnswerChangedListener onAnswerChangedListener) {
        mOnAnswerChangedListener = onAnswerChangedListener;
    }

    @Override
    public void setHelpText(String helpText) {
        yearText.setHint(helpText);
    }

    @Override
    public void setValue(Value value) {
        if (value != null) {
            yearText.setText(value.getValue());
        }
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public boolean hasError() {
        return false;
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_year_selector, this);
        header = (TextView) findViewById(R.id.row_header_text);
        yearText = (TextView) findViewById(R.id.answer);

        yearText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (enabled) {
                    YearPicker yearPicker = new YearPicker();
                    yearPicker.setInterval(context.getResources().getInteger(R.integer.year_interval));
                    yearPicker.setOnYearSelectedListener(new YearPicker.OnYearSelectedListener() {
                        @Override
                        public void onYearSelected(int year) {
                            yearText.setText(String.valueOf(year));
                            notifyAnswerChanged(String.valueOf(year));
                        }
                    });
                    yearPicker.show(mActivity.getFragmentManager(), TAG);
                }
            }
        });

    }

    protected void notifyAnswerChanged(String newValue) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(this, newValue);
        }
    }

    public interface onAnswerChangedListener {
        void onAnswerChanged(View view, String newValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
    }
}
