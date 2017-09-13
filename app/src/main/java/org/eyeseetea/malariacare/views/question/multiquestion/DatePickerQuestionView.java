package org.eyeseetea.malariacare.views.question.multiquestion;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.layout.listeners.question.QuestionAnswerChangedListener;
import org.eyeseetea.malariacare.views.DatePickerFragment;
import org.eyeseetea.malariacare.views.question.CommonQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;


public class DatePickerQuestionView extends CommonQuestionView implements IQuestionView,
        IMultiQuestionView {

    TextView header;
    TextView dateText;
    private QuestionAnswerChangedListener mOnAnswerChangedListener;
    private Activity mActivity;
    private String TAG = "DatePickerQuestionView";
    private boolean enabled;


    public DatePickerQuestionView(Context context) {
        super(context);
        mActivity = (Activity) context;
        enabled = true;
        init(context);
    }

    public void setOnAnswerChangedListener(QuestionAnswerChangedListener onAnswerChangedListener) {
        mOnAnswerChangedListener = onAnswerChangedListener;
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public void setHelpText(String helpText) {
        dateText.setHint(helpText);
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            dateText.setText(valueDB.getValue());
        }
    }

    protected void notifyAnswerChanged(String newValue) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(this, newValue);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
    }

    public interface onAnswerChangedListener {
        void onAnswerChanged(View view, String newValue);
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_year_selector, this);
        header = (TextView) findViewById(R.id.row_header_text);
        dateText = (TextView) findViewById(R.id.answer);
        final DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateText.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        });
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enabled) {
                    datePickerFragment.show(mActivity.getFragmentManager(), TAG);
                }
            }
        });
    }

}
