package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.PhoneFormatLocalDataSource;
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
import org.eyeseetea.malariacare.views.question.multiquestion.PhoneMultiQuestionView;

public class PhoneMultiquestionViewStrategy extends APhoneMultiquestionViewStrategy {
    public PhoneMultiquestionViewStrategy(
            PhoneMultiQuestionView phoneMultiQuestionView) {
        super(phoneMultiQuestionView);
    }

    @Override
    public void addTextChangeListener(final EditText phoneText) {
        IPhoneFormatRepository phoneLocalDataSource = new PhoneFormatLocalDataSource();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        GetPhoneFormatUseCase getPhoneFormatUseCase = new GetPhoneFormatUseCase(
                phoneLocalDataSource, mainExecutor, asyncExecutor);
        getPhoneFormatUseCase.execute(new GetPhoneFormatUseCase.Callback() {
            @Override
            public void onSuccess(final PhoneFormat phoneFormat) {
                phoneText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            Phone phone = new Phone(phoneText.getText().toString(), phoneFormat);
                            notifyAnswerChanged(phone.getValue());
                            Validation.getInstance().removeInputError(phoneText);

                        } catch (InvalidPhoneException e) {
                            Validation.getInstance().addinvalidInput(phoneText,
                                    mPhoneMultiQuestionView.getContext().getString(
                                            R.string.dynamic_error_phone_format));
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }
                });
            }

            @Override
            public void onError() {
                Log.e(getClass().getName(), "Error getting phone fromat");
            }
        });
    }
}
