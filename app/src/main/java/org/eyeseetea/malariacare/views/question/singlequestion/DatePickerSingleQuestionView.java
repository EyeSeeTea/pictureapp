package org.eyeseetea.malariacare.views.question.singlequestion;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.listeners.question.QuestionAnswerChangedListener;
import org.eyeseetea.malariacare.views.DatePickerFragment;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

public class DatePickerSingleQuestionView extends AKeyboardSingleQuestionView implements
        IQuestionView {
    TextView header;
    TextView dateText;
    private QuestionAnswerChangedListener mOnAnswerChangedListener;
    private Activity mActivity;
    private String TAG = "DatePickerQuestionView";
    private boolean enabled;


    public DatePickerSingleQuestionView(Context context) {
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
        if(BuildConfig.validationInline) {
            if (dateText.getText().toString().isEmpty()) {
                Validation.getInstance().addinvalidInput(dateText,
                        getResources().getString(
                                R.string.error_empty_question));
            } else {
                Validation.getInstance().removeInputError(dateText);
                dateText.setError(null);
            }
        }
    }

    @Override
    public EditText getAnswerView() {
        return null;
    }

    @Override
    protected void validateAnswer(Context context) {
        if (dateText.getText().toString().isEmpty()) {
            Validation.getInstance().addinvalidInput(dateText,
                    getResources().getString(
                            R.string.error_empty_question));
        } else {
            Validation.getInstance().removeInputError(dateText);
            dateText.setError(null);
            super.notifyAnswerChanged(dateText.getText().toString());
        }
    }

    @Override
    public boolean isEnabled(){
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_datepiker_row, this);
        dateText = (TextView) findViewById(R.id.answer);
        final DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = fixDate(year, monthOfYear, dayOfMonth);
                dateText.setText(date);
                notifyAnswerChanged(date);
                action(context);
            }
        });
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enabled) {
                    datePickerFragment.show(mActivity.getFragmentManager(), TAG);
                    hideKeyboard(v);
                }
            }
        });
        dateText.setFocusable(true);
        if (BuildConfig.validationInline) {
            Validation.getInstance().addInput(dateText);
            Validation.getInstance().addinvalidInput(dateText, getResources().getString(
                    R.string.error_empty_question));
        }
    }

    @NonNull
    private String fixDate(int year, int monthOfYear, int dayOfMonth) {
        String fixedMonth = String.valueOf(monthOfYear);
        if(fixedMonth.length()==1){
            fixedMonth = 0 + fixedMonth;
        }
        String fixedDay = String.valueOf(dayOfMonth);
        if(fixedDay.length()==1){
            fixedDay = 0 + fixedDay;
        }
        return year + "-" + fixedMonth + "-" + fixedDay;
    }
}
