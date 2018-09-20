package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.widget.EditText;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies.APhoneMultiquestionViewStrategy;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies.PhoneMultiquestionViewStrategy;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class PhoneMultiQuestionView extends AKeyboardQuestionView implements IQuestionView,
        IMultiQuestionView {
    CustomTextView header;
    private APhoneMultiquestionViewStrategy mPhoneMultiquestionViewStrategy;

    public PhoneMultiQuestionView(Context context) {
        super(context);
        mPhoneMultiquestionViewStrategy = new PhoneMultiquestionViewStrategy(this);
        init(context);
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        answer.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {
        answer.setHint(helpText);
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            answer.setText(valueDB.getValue());
            validateAnswer(answer.getText().toString(), answer);
        }
    }

    @Override
    public boolean hasError() {
        return answer.getError() != null;
    }

    @Override
    public void requestAnswerFocus() {
        answer.requestFocus();
        showKeyboard(getContext(), answer);
    }

    @Override
    public void setOnAnswerChangedListener(
            onAnswerChangedListener onAnswerChangedListener) {
        super.setOnAnswerChangedListener(onAnswerChangedListener);
        mPhoneMultiquestionViewStrategy.setOnAnswerChangedListener(onAnswerChangedListener);
    }

    @Override
    public EditText getAnswerView() {
        return answer;
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_phone_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        answer = (CustomEditText) findViewById(R.id.answer);

        Validation.getInstance().addInput(answer);
        if (BuildConfig.validationInline) {
            Validation.getInstance().addinvalidInput(answer, getContext().getString(
                    R.string.dynamic_error_phone_format));
        }
        mPhoneMultiquestionViewStrategy.addTextChangeListener(answer);
    }
}
