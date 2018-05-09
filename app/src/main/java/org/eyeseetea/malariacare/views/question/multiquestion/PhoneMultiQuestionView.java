package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.widget.EditText;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.PhoneFormatLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IPhoneFormatRepository;
import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.entity.PhoneFormat;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.eyeseetea.malariacare.domain.usecase.GetPhoneFormatUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
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
            if (BuildConfig.validationInline) {
                if (!mCustomEditText.getText().toString().isEmpty()) {
                        validatePhone(valueDB.getValue());
                }
            }
        }
    }

    private void validatePhone(final String value) {

        IPhoneFormatRepository phoneLocalDataSource = new PhoneFormatLocalDataSource();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        GetPhoneFormatUseCase getPhoneFormatUseCase = new GetPhoneFormatUseCase(
                phoneLocalDataSource, mainExecutor, asyncExecutor);
        getPhoneFormatUseCase.execute(new GetPhoneFormatUseCase.Callback() {
            @Override
            public void onSuccess(final PhoneFormat phoneFormat) {
                        try{
                            new Phone(value, phoneFormat);
                            Validation.getInstance().removeInputError(mCustomEditText);
                        } catch (InvalidPhoneException e) {
                        }
                    }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public boolean hasError() {
        return mCustomEditText.getError() != null;
    }

    @Override
    public void requestAnswerFocus() {
        mCustomEditText.requestFocus();
        showKeyboard(getContext(), mCustomEditText);
    }

    @Override
    public void setOnAnswerChangedListener(
            onAnswerChangedListener onAnswerChangedListener) {
        super.setOnAnswerChangedListener(onAnswerChangedListener);
        mPhoneMultiquestionViewStrategy.setOnAnswerChangedListener(onAnswerChangedListener);
    }

    @Override
    public EditText getAnswerView() {
        return mCustomEditText;
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_phone_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        mCustomEditText = (CustomEditText) findViewById(R.id.answer);

        Validation.getInstance().addInput(mCustomEditText);
        if (BuildConfig.validationInline) {
            Validation.getInstance().addinvalidInput(mCustomEditText, getContext().getString(
                    R.string.dynamic_error_phone_format));
        }
        mPhoneMultiquestionViewStrategy.addTextChangeListener(mCustomEditText);
    }
}
