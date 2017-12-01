package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class DropdownMultiQuestionView extends AOptionQuestionView implements IQuestionView,
        IMultiQuestionView, IImageQuestionView {
    CustomTextView header;
    Spinner spinnerOptions;
    ImageView imageView;
    QuestionDB mQuestionDB;
    private boolean optionSetFromSavedValue = false;

    public DropdownMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        List<OptionDB> optionDBList = new ArrayList<>(optionDBs);
        optionDBList.add(0, new OptionDB(Constants.DEFAULT_SELECT_OPTION));

        spinnerOptions.setAdapter(new OptionArrayAdapter(getContext(), optionDBList));
    }

    public void setQuestionDB(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
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
    }

    @Override
    public boolean hasError() {
        return false;
    }

    private void init(final Context context) {
        setFocusableInTouchMode(true);
        inflate(context, R.layout.multi_question_tab_dropdown_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        spinnerOptions = (Spinner) findViewById(R.id.answer);
        imageView = ((ImageView) findViewById(R.id.question_image_row));
        optionSetFromSavedValue = true;

        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                OptionDB optionDB = (OptionDB) parent.getItemAtPosition(position);
                if (!optionSetFromSavedValue) {
                    notifyAnswerChanged(optionDB);
                    requestFocus();
                    hideKeyboard();
                } else {
                    optionSetFromSavedValue = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void hideKeyboard() {
        Log.d(DropdownMultiQuestionView.class.getName(), "KEYBOARD HIDE ");
        InputMethodManager keyboard = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(getWindowToken(), 0);
    }
}
