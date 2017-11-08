package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies
        .APhoneMultiquestionViewStrategy;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies
        .PhoneMultiquestionViewStrategy;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class PhoneMultiQuestionView extends AKeyboardQuestionView implements IQuestionView,
        IMultiQuestionView {
    CustomTextView header;
    CustomEditText mCustomEditText;
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
        mCustomEditText.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {
        mCustomEditText.setHint(helpText);
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB != null) {
            mCustomEditText.setText(valueDB.getValue());
        }
    }

    @Override
    public boolean hasError() {
        return mCustomEditText.getError() != null;
    }

    @Override
    public void setOnAnswerChangedListener(
            onAnswerChangedListener onAnswerChangedListener) {
        super.setOnAnswerChangedListener(onAnswerChangedListener);
        mPhoneMultiquestionViewStrategy.setOnAnswerChangedListener(onAnswerChangedListener);
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_phone_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        mCustomEditText = (CustomEditText) findViewById(R.id.answer);

        Validation.getInstance().addInput(mCustomEditText);
        mPhoneMultiquestionViewStrategy.addTextChangeListener(mCustomEditText);
    }
}
