package org.eyeseetea.malariacare.views.question.multiquestion;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
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

    private DatePickerFragment datePickerFragment;


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
        datePickerFragment.setTitle(headerValue);
    }

    @Override
    public boolean hasError() {
        return dateText.getError() != null;
    }

    @Override
    public void requestAnswerFocus() {
        dateText.requestFocus();
        dateText.performClick();
    }

    @Override
    public void setHelpText(String helpText) {
        dateText.setHint(helpText);
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            dateText.setText(valueDB.getValue());
            if (BuildConfig.validationInline) {
                if (!dateText.getText().toString().isEmpty()) {
                    if(validateQuestionRegExp(dateText)) {
                        Validation.getInstance().removeInputError(dateText);
                    }
                }
            }
        }else{
            Validation.getInstance().addinvalidInput(dateText, getContext().getString(
                    R.string.error_empty_question));
        }
        if(dateText.getText().toString().isEmpty() && !question.isCompulsory()){
            Validation.getInstance().removeInputError(dateText);
        }
    }

    protected void notifyAnswerChanged(String newValue) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(this, newValue);
        }
        if (BuildConfig.validationInline) {
            if (!dateText.getText().toString().isEmpty()) {
                if(validateQuestionRegExp(dateText)) {
                    Validation.getInstance().removeInputError(dateText);
                    dateText.setError(null);
                }
            } else {
                Validation.getInstance().addinvalidInput(dateText, getContext().getString(
                        R.string.error_empty_question));
            }
            if(dateText.getText().toString().isEmpty() && !question.isCompulsory()){
                Validation.getInstance().removeInputError(dateText);
            }
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
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.setTitle(header.getText().toString());
        datePickerFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date = fixDate(year, monthOfYear, dayOfMonth);
                dateText.setText(date);
                notifyAnswerChanged(date);
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
            if(dateText.getText()==null || dateText.getText().toString().isEmpty()) {
                Validation.getInstance().addInput(dateText);
                Validation.getInstance().addinvalidInput(dateText, getResources().getString(
                        R.string.error_empty_question));
            }
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
