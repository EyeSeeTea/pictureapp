package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.adapters.general.DialogSpinnerAdapter;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IExtraValidation;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies
        .ADropdownMultiQuestionViewStrategy;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies
        .DropdownMultiQuestionViewStrategy;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class DropdownWithFilterMultiQuestionView extends AOptionQuestionView implements
        IQuestionView, IMultiQuestionView, IExtraValidation {
    private CustomTextView header;
    private Spinner spinnerAsButton;
    private QuestionDB mQuestionDB;
    View dialog;
    private boolean optionSetFromSavedValue = false;
    private List<OptionDB> mOptionDBS;
    private ADropdownMultiQuestionViewStrategy mDropdownMultiQuestionViewStrategy;
    private ListView listView;
    private AlertDialog.Builder builder;
    private OptionDB selectedOptionDB;

    public DropdownWithFilterMultiQuestionView(Context context) {
        super(context);
        init(context);
    }


    @Override
    public void requestAnswerFocus() {
    }

    @Override
    public void setValue(ValueDB valueDB) {
        if (valueDB == null || valueDB.getValue() == null || valueDB.getOptionDB()==null) {
            return;
        }
        optionSetFromSavedValue = true;
        spinnerAsButton.setAdapter(new OptionArrayAdapter(getContext(), mOptionDBS));

        spinnerAsButton.setSelection(findOptionPosition(valueDB.getOptionDB()));
        selectedOptionDB = valueDB.getOptionDB();
        if (BuildConfig.validationInline) {
            if(spinnerAsButton.getSelectedItem()==null) {
                Validation.getInstance().addinvalidInput(header, getContext().getString(
                        R.string.error_empty_question));
            }
        }
    }

    private int findOptionPosition(OptionDB optionDB) {
        int position = 0;
        while(spinnerAsButton.getItemAtPosition(position)!=null){
            if(spinnerAsButton.getItemAtPosition(position).equals(optionDB)){
                return position;
            }else{
                position++;
            }
        }
        return position;
    }

    private void init(final Context context) {
        mDropdownMultiQuestionViewStrategy = new DropdownMultiQuestionViewStrategy(context);
        inflate(context, R.layout.multi_question_tab_spinner_with_filter_row, this);
        header = (CustomTextView) findViewById(R.id.row_header_text);
        spinnerAsButton = (Spinner) findViewById(R.id.answer);
        if(isEnabled()){
            spinnerAsButton.setOnTouchListener(new OnTouchListener(context));
        }

    }

    private void filter(ListView listView, String text) {
        List<OptionDB> filteredOptions = new ArrayList<>();
        if(text==null || text.isEmpty()){
            setOptionsOnList(mOptionDBS, listView);
            return;
        }
        for(OptionDB option : mOptionDBS){
            if(option.getInternationalizedName().toLowerCase().contains(text.toLowerCase())){
                filteredOptions.add(option);
            }
        }
        setOptionsOnList(filteredOptions, listView);
    }

    private void setOptionsOnList(List<OptionDB> options, ListView listView) {
        listView.setAdapter(new DialogSpinnerAdapter(getContext(), options, R.layout.simple_spinner_dropdown_item, selectedOptionDB));

    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        mOptionDBS = optionDBs;
        List<OptionDB> optionDBList = new ArrayList<>(optionDBs);
        OptionDB optionDB = mDropdownMultiQuestionViewStrategy.getDefaultOption(mQuestionDB);
        if(!mOptionDBS.contains(optionDB)) {
            optionDBList.add(0, optionDB);
        }
        mOptionDBS = optionDBList;
        spinnerAsButton.setAdapter(new OptionArrayAdapter(getContext(), mOptionDBS));
    }

    public void setQuestionDB(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
        setOptions(questionDB.getOptions());
    }

    @Override
    public void checkLoadedErrors() {
        Validation.getInstance().removeInputError(header);
        header.setError(null);
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        spinnerAsButton.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public boolean hasError() {
        return false;
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

    private class OnTouchListener implements View.OnTouchListener {

        Context context;
        public OnTouchListener(Context context){
            this.context = context;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                showDialog(context);
                return true;
            }
            return false;
        }
    }

    private void showDialog(Context context) {
        dialog = inflate(context, R.layout.dialog_spinner_with_filter, null);
        builder = new AlertDialog.Builder(context);
        builder.setView(dialog);
        final AlertDialog alertDialog = builder.show();
        Window window = alertDialog.getWindow();
        window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.TOP);
        listView = (ListView) dialog.findViewById(R.id.listView);
        EditText editText = (EditText) dialog.findViewById(R.id.filter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(listView, s.toString());
            }
        });
        setOptionsOnList(mOptionDBS, listView);
        listView.setOnItemClickListener(new OnItemClickListener(alertDialog));
        editText.requestFocus();
    }

    private class OnItemClickListener implements AdapterView.OnItemClickListener {
        AlertDialog alertDialog;

        public OnItemClickListener(AlertDialog alertDialog){
            this.alertDialog = alertDialog;
        }
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            alertDialog.dismiss();
            selectedOptionDB = null;
            OptionDB optionDB = (OptionDB) parent.getItemAtPosition(position);
            position = findOptionPosition(optionDB);
            spinnerAsButton.setSelection(position);
            if (!optionSetFromSavedValue) {
                if (position > 0) {
                    notifyAnswerChanged(optionDB);
                    selectedOptionDB = optionDB;
                }
            } else {
                optionSetFromSavedValue = false;
            }
            if (BuildConfig.validationInline) {
                if (position > 0 || !question.isCompulsory()) {
                    Validation.getInstance().removeInputError(header);
                    header.setError(null);
                } else {
                    Validation.getInstance().addinvalidInput(header, getContext().getString(
                            R.string.error_empty_question));
                }
            }
        }
    }
}
