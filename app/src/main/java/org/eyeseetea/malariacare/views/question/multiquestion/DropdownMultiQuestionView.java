package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IExtraValidation;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies
        .ADropdownMultiQuestionViewStrategy;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies
        .DropdownMultiQuestionViewStrategy;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class DropdownMultiQuestionView extends AOptionQuestionView implements IQuestionView,
        IMultiQuestionView, IImageQuestionView, IExtraValidation {
    CustomTextView header;
    Spinner spinnerOptions;
    ImageView imageView;
    QuestionDB mQuestionDB;
    private boolean optionSetFromSavedValue = false;
    private ADropdownMultiQuestionViewStrategy mDropdownMultiQuestionViewStrategy;

    public DropdownMultiQuestionView(Context context) {
        super(context);
        mDropdownMultiQuestionViewStrategy = new DropdownMultiQuestionViewStrategy(context);
        init(context);
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        List<OptionDB> optionDBList = new ArrayList<>(optionDBs);
        optionDBList.add(0, mDropdownMultiQuestionViewStrategy.getDefaultOption(mQuestionDB));

        spinnerOptions.setAdapter(new OptionArrayAdapter(getContext(), optionDBList));
    }

    public void setQuestionDB(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
    }

    @Override
    public void checkLoadedErrors() {
        if(!question.isCompulsory()){
            Validation.getInstance().removeInputError(header);
        }
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
        spinnerOptions.setPrompt(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        spinnerOptions.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setImage(String path) {
        if (path != null && !path.equals("")) {
            LayoutUtils.makeImageVisible(path, imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB == null || valueDB.getValue() == null) {
            return;
        }
        optionSetFromSavedValue = true;

        for (int i = 0; i < spinnerOptions.getAdapter().getCount(); i++) {
            OptionDB optionDB = (OptionDB) spinnerOptions.getItemAtPosition(i);
            if (optionDB.equals(valueDB.getOptionDB())) {
                spinnerOptions.setSelection(i);
                break;
            }
        }

        validateAnswer();
    }

    private void validateAnswer() {
        if (BuildConfig.validationInline) {
            if (!question.isCompulsory() || spinnerOptions.getSelectedItemPosition() > 0) {
                if(validateQuestionRegExp(header)) {
                    Validation.getInstance().removeInputError(header);
                    header.setError(null);
                }
            } else {
                Validation.getInstance().addinvalidInput(header, getContext().getString(
                        R.string.error_empty_question));
            }
        }
    }

    @Override
    public void requestAnswerFocus() {
        spinnerOptions.requestFocusFromTouch();
    }

    @Override
    public boolean hasError() {
        return false;
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_dropdown_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        spinnerOptions = (Spinner) findViewById(R.id.answer);
        imageView = ((ImageView) findViewById(R.id.question_image_row));
        optionSetFromSavedValue = true;

        spinnerOptions.setFocusable(true);
        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                OptionDB optionDB = (OptionDB) parent.getItemAtPosition(position);
                if (!optionSetFromSavedValue) {
                        notifyAnswerChanged(optionDB);
                } else {
                    optionSetFromSavedValue = false;
                }
                validateAnswer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerOptions.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hideKeyboard(v);
                }
                return false;
            }
        });
        if (BuildConfig.validationInline) {
            Validation.getInstance().addInput(header);
            Validation.getInstance().addinvalidInput(header,
                    getResources().getString(R.string.error_empty_question));
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
