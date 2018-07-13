package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
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
    private List<OptionDB> mOptionDBS;

    public AutocompleteEditTextMultiQuestionView(Context context) {
        super(context);
        init(context);
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        mOptionDBS = optionDBs;
        mAutoCompleteTextView.setAdapter(getAutoCompleteTextViewAdapter(optionDBs));
    }

    private ArrayAdapter<String> getAutoCompleteTextViewAdapter(List<OptionDB> optionDBs) {
        String[] options = new String[optionDBs.size()];
        for (int i = 0; i < optionDBs.size(); i++) {
            options[i] = optionDBs.get(i).getInternationalizedName();
        }
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line,
                options);
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

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_autocomplet_text_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.answer);
        mAutoCompleteTextView.setThreshold(1);

        mAutoCompleteTextView.setFocusable(true);
        mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                OptionDB optionDB = getOptionFromString(
                        (String) parent.getItemAtPosition(position));
                if (!optionSetFromSavedValue) {
                    notifyAnswerChanged(optionDB);
                } else {
                    optionSetFromSavedValue = false;
                }
                if (BuildConfig.validationInline) {
                    Validation.getInstance().removeInputError(header);
                    header.setError(null);
                }
            }
        });
        mAutoCompleteTextView.addTextChangedListener(getTextChangeListener());
        if (BuildConfig.validationInline) {
            Validation.getInstance().addInput(header);
            Validation.getInstance().addinvalidInput(header,
                    getResources().getString(R.string.error_empty_question));
        }
    }

    private OptionDB getOptionFromString(String optionName) {
        for (OptionDB optionDB : mOptionDBS) {
            if (optionName.equals(optionDB.getInternationalizedName())) {
                return optionDB;
            }
        }
        return null;
    }

    public TextWatcher getTextChangeListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Validation.getInstance().addinvalidInput(header, getContext().getString(
                        R.string.error_empty_question));
            }
        };
    }
}
