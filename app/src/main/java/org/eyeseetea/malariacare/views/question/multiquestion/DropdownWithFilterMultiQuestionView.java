package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.adapters.general.DialogSpinnerAdapter;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.strategies.DropdownAdapterStrategy;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies.ADropdownMultiQuestionViewStrategy;
import org.eyeseetea.malariacare.views.question.multiquestion.strategies.DropdownMultiQuestionViewStrategy;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class DropdownWithFilterMultiQuestionView extends AOptionQuestionView implements
        IQuestionView, IMultiQuestionView {
    private CustomTextView header;
    private TextView textViewButton;
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
        textViewButton.setText(valueDB.getOptionDB().getInternationalizedName());
        selectedOptionDB = valueDB.getOptionDB();
        if (BuildConfig.validationInline) {
            if(textViewButton.getText()==null || textViewButton.getText().toString().isEmpty()) {
                Validation.getInstance().addinvalidInput(header, getContext().getString(
                        R.string.error_empty_question));
            }
        }
    }

    private void init(final Context context) {
        mDropdownMultiQuestionViewStrategy = new DropdownMultiQuestionViewStrategy(context);
        inflate(context, R.layout.multi_question_tab_spinner_with_filter_row, this);
        header = (CustomTextView) findViewById(R.id.row_header_text);
        textViewButton = (TextView) findViewById(R.id.answer);
        textViewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = inflate(context, R.layout.dialog_spinner_with_filter, null);
                builder = new AlertDialog.Builder(context);
                builder.setView(dialog);
                final AlertDialog alertDialog = builder.show();
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
                setOptions(mOptionDBS);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        alertDialog.dismiss();
                        selectedOptionDB = null;
                        OptionDB optionDB = (OptionDB) parent.getItemAtPosition(position);
                        textViewButton.setText(optionDB.getInternationalizedName().toString());
                        if (!optionSetFromSavedValue) {
                            if (position > 0) {
                                notifyAnswerChanged(optionDB);
                                selectedOptionDB = optionDB;
                            }
                        } else {
                            optionSetFromSavedValue = false;
                        }
                        if (BuildConfig.validationInline) {
                            if (position > 0) {
                                Validation.getInstance().removeInputError(header);
                                header.setError(null);
                            } else {
                                Validation.getInstance().addinvalidInput(header, getContext().getString(
                                        R.string.error_empty_question));
                            }
                        }
                    }
                });
                editText.requestFocus();
            }
        });
    }

    private void filter(ListView listView, String s) {
        List<OptionDB> filteredOptions = new ArrayList<>();
        if(s==null || s.isEmpty()){
            return;
        }
        for(OptionDB option : mOptionDBS){
            if(option.getInternationalizedName().toLowerCase().contains(s.toLowerCase())){
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
        optionDBList.add(0, mDropdownMultiQuestionViewStrategy.getDefaultOption(mQuestionDB));

        if(listView==null){
            return;
        }
        setOptionsOnList(optionDBList, listView);
    }

    public void setQuestionDB(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
        setOptions(questionDB.getOptions());
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        textViewButton.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {

    }

   //@Override
   //public void setImage(String path) {
   //}

   //@Override
   //public void setValue(ValueDB valueDB) {
       //if (valueDB == null || valueDB.getValue() == null) {
       //    return;
       //}
       //optionSetFromSavedValue = true;

       //for (int i = 0; i < listView.getAdapter().getCount(); i++) {
       //    OptionDB optionDB = (OptionDB) listView.getItemAtPosition(i);
       //    if (optionDB.equals(valueDB.getOptionDB())) {
       //        listView.setSelection(i);
       //        break;
       //    }
       //}

       //if (BuildConfig.validationInline) {
       //    if (listView.getSelectedItemPosition() > 0) {
       //        Validation.getInstance().removeInputError(header);
       //        header.setError(null);
       //    } else {
       //        Validation.getInstance().addinvalidInput(header, getContext().getString(
       //                R.string.error_empty_question));
       //    }
       //}
 //   }

    @Override
    public boolean hasError() {
        return false;
    }

    private void init2(final Context context) {
      //  inflate(context, R.layout.multi_question_tab_dropdown_row, this);
//
      //  header = (CustomTextView) findViewById(R.id.row_header_text);
      //  spinnerOptions = (Spinner) findViewById(R.id.answer);
      //  imageView = ((ImageView) findViewById(R.id.question_image_row));
      //  optionSetFromSavedValue = true;
//
      //  spinnerOptions.setFocusable(true);
      //  spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      //      @Override
      //      public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
      //          OptionDB optionDB = (OptionDB) parent.getItemAtPosition(position);
      //          if (!optionSetFromSavedValue) {
      //              if (position > 0) {
      //                  notifyAnswerChanged(optionDB);
      //              }
      //          } else {
      //              optionSetFromSavedValue = false;
      //          }
      //          if (BuildConfig.validationInline) {
      //              if (position > 0) {
      //                  Validation.getInstance().removeInputError(header);
      //                  header.setError(null);
      //              } else {
      //                  Validation.getInstance().addinvalidInput(header, getContext().getString(
      //                          R.string.error_empty_question));
      //              }
      //          }
      //      }
//
      //      @Override
      //      public void onNothingSelected(AdapterView<?> parent) {
//
      //      }
      //  });
      //  spinnerOptions.setOnTouchListener(new OnTouchListener() {
      //      @Override
      //      public boolean onTouch(View v, MotionEvent event) {
      //          if (event.getAction() == MotionEvent.ACTION_UP) {
      //              hideKeyboard(v);
      //          }
      //          return false;
      //      }
      //  });
      //  if (BuildConfig.validationInline) {
      //      Validation.getInstance().addInput(header);
      //      Validation.getInstance().addinvalidInput(header,
      //              getResources().getString(R.string.error_empty_question));
      //  }
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
