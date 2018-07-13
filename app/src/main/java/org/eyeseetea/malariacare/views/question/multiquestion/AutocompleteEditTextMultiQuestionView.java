package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.widget.AutoCompleteTextView;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class AutocompleteEditTextMultiQuestionView extends AOptionQuestionView implements
        IQuestionView, IMultiQuestionView {
    private CustomTextView header;
    private AutoCompleteTextView mAutoCompleteTextView;
    private QuestionDB mQuestionDB;
    private boolean optionSetFromSavedValue = false;

    public AutocompleteEditTextMultiQuestionView(Context context) {
        super(context);
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        mAutoCompleteTextView.setAdapter(new OptionArrayAdapter(getContext(), optionDBs));
    }

    @Override
    public void setQuestionDB(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
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
    public void requestAnswerFocus() {
        mAutoCompleteTextView.requestFocus();
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB == null || valueDB.getValue() == null) {
            return;
        }
        optionSetFromSavedValue = true;

        for (int i = 0; i < mAutoCompleteTextView.getAdapter().getCount(); i++) {
            OptionDB optionDB = (OptionDB) mAutoCompleteTextView.getAdapter().getItem(i);
            if (optionDB.equals(valueDB.getOptionDB())) {
                mAutoCompleteTextView.setSelection(i);
                break;
            }
        }

        if (BuildConfig.validationInline) {
            Validation.getInstance().addinvalidInput(header, getContext().getString(
                    R.string.error_empty_question));
        }
    }

    @Override
    public void activateQuestion() {
        setActive(true);
        Object inputView = this.findViewById(R.id.row_header_text);
        if (inputView != null) {
            Validation.getInstance().addInput(inputView);
        }
    }

    @Override
    public void deactivateQuestion() {
        setActive(false);
        Object inputView = this.findViewById(R.id.row_header_text);
        if (inputView != null) {
            Validation.getInstance().removeInputError(inputView);
        }
    }
}
