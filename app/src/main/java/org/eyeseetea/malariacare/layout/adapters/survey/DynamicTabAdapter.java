/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.layout.adapters.survey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.malariacare.layout.listeners.SwipeTouchListener;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.filters.MinMaxInputFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import utils.PhoneMask;

/**
 * Created by Jose on 21/04/2015.
 */
public class DynamicTabAdapter extends BaseAdapter implements ITabAdapter {

    private final static String TAG=".DynamicTabAdapter";

    public NavigationController navigationController;

    /**
     * Flag that indicates if the swipe listener has been already added to the listview container
     */
    private boolean isSwipeAdded;

    /**
     * Listener that detects taps on buttons & swipe
     */
    private SwipeTouchListener swipeTouchListener;

    Tab tab;

    LayoutInflater lInflater;

    private final Context context;

    int id_layout;


    /**
     * Flag that indicates if the current survey in session is already sent or not (it affects readonly settings)
     */
    private boolean readOnly;

    /**
     * View needed to close the keyboard in methods with view
     */
    View keyboardView;


    /**
     * Flag that indicates if the actual question option is clicked to prevent multiple clicks.
     */
    public static boolean isClicked;

    public DynamicTabAdapter(Tab tab, Context context) {
        this.lInflater = LayoutInflater.from(context);
        this.context = context;
        this.id_layout = R.layout.form_without_score;

        this.navigationController = initNavigationController(tab);
        this.readOnly = Session.getSurvey() != null && !Session.getSurvey().isInProgress();
        this.isSwipeAdded=false;
        //On create dynamictabadapter, if is not readonly and has value not null it should come from reviewFragment
        if(!readOnly){
            Question question=navigationController.getCurrentQuestion();
            if(question.getValueBySession()!=null) {
                goToLastQuestion();
            }
        }

        int totalPages=navigationController.getCurrentQuestion().getTotalQuestions();
        if(readOnly){
            if(Session.getSurvey()!=null){
                Question lastQuestion=Session.getSurvey().findLastSavedQuestion();
                if(lastQuestion!=null){
                    totalPages=lastQuestion.getTotalQuestions();
                }
            }
        }
        navigationController.setTotalPages(totalPages);
        isClicked=false;
    }

    private NavigationController initNavigationController(Tab tab) {
        NavigationController navigationController = NavigationBuilder.getInstance().buildController(tab);
        navigationController.next(null);
        return navigationController;
    }

    public void addOnSwipeListener(final ListView listView){
        if(isSwipeAdded){
            return;
        }

        swipeTouchListener=new SwipeTouchListener(context) {
            /**
             * Click listener for image option
             * @param view
             */
            public void onClick(final View view) {
                if(isClicked){
                    Log.d(TAG, "onClick ignored to avoid double click");
                    return;
                }

                isClicked=true;
                Log.d(TAG, "onClick");
                navigationController.isMovingToForward=true;
                final Option selectedOption=(Option)view.getTag();
                final Question question=navigationController.getCurrentQuestion();
                Question counterQuestion = question.findCounterByOption(selectedOption);
                if(counterQuestion==null){
                    saveOptionAndMove(view,selectedOption,question);
                }else{
                    showConfirmCounter(view,selectedOption,question,counterQuestion);
                }
            }

            /**
             * Swipe right listener moves to previous question
             */
            public void onSwipeRight() {
                Log.d(TAG, "onSwipeRight(previous)");

                //Hide keypad
                hideKeyboard(listView.getContext(), listView);

                previous();
            }

            /**
             * Swipe left listener moves to next question
             */
            public void onSwipeLeft() {
                Log.d(TAG,"onSwipeLeft(next)");
                if(readOnly || navigationController.isNextAllowed()) {

                    //Hide keypad
                    hideKeyboard(listView.getContext(), listView);

                    next();
                }
            }
        };

        listView.setOnTouchListener(swipeTouchListener);
    }

    private void showConfirmCounter(final View view, final Option selectedOption, final Question question, Question questionCounter){
        //Change question x confirm message
        View rootView = view.getRootView();
        final TextCard questionView=(TextCard)rootView.findViewById(R.id.question);
        questionView.setText(questionCounter.getForm_name());

        //cancel
        ImageView noView=(ImageView)rootView.findViewById(R.id.confirm_no);
        noView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Leave current question as it was
                removeConfirmCounter(v);
                isClicked=false;
                notifyDataSetChanged();
            }
        });

        //confirm
        ImageView yesView=(ImageView)rootView.findViewById(R.id.confirm_yes);
        yesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeConfirmCounter(v);
                saveOptionAndMove(view,selectedOption,question);
            }
        });

        //Show confirm on full screen
        rootView .findViewById(R.id.options_table).setVisibility(View.GONE);
        rootView .findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);

        //Show question image in counter alert
        if(questionCounter.getPath()!=null && !questionCounter.getPath().equals("")) {
            ImageView imageView=(ImageView) rootView.findViewById(R.id.questionImageRow);
            putImageInImageView(questionCounter.getPath(), imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        //Question "header" is in the first option in Options.csv
        List<QuestionOption> questionOptions = questionCounter.getQuestionOption();
        if(questionOptions.get(0)!=null) {
            TextCard textCard = (TextCard) rootView.findViewById(R.id.questionTextRow);
            textCard.setText(questionOptions.get(0).getOption().getCode());
            textCard.setTextSize(questionOptions.get(0).getOption().getOptionAttribute().getText_size());
        }
        //Question "confirm button" is in the second option in Options.csv
        if(questionOptions.get(1)!=null) {
            TextCard confirmTextCard = (TextCard) rootView.findViewById(R.id.textcard_confirm_yes);
            confirmTextCard.setText(questionOptions.get(1).getOption().getCode());
            confirmTextCard.setTextSize(questionOptions.get(1).getOption().getOptionAttribute().getText_size());
        }
        //Question "no confirm button" is in the third option in Options.csv
        if(questionOptions.get(2)!=null) {
            TextCard noConfirmTextCard = (TextCard) rootView.findViewById(R.id.textcard_confirm_no);
            noConfirmTextCard.setText(questionOptions.get(2).getOption().getCode());
            noConfirmTextCard.setTextSize(questionOptions.get(2).getOption().getOptionAttribute().getText_size());
        }

    }

    private void removeConfirmCounter(View view){
        view.getRootView().findViewById(R.id.options_table).setVisibility(View.VISIBLE);
        view.getRootView().findViewById(R.id.confirm_table).setVisibility(View.GONE);
    }

    private void saveOptionAndMove(View view, Option selectedOption, Question question) {
        Value value = question.getValueBySession();
        //set new totalpages if the value is not null and the value change
        if(value!=null && !readOnly) {
            navigationController.setTotalPages(question.getTotalQuestions());
        }
        ReadWriteDB.saveValuesDDL(question, selectedOption, value);
        darkenNonSelected(view, selectedOption);
        highlightSelection(view, selectedOption);
        finishOrNext();
    }

    private void darkenNonSelected(View view, Option selectedOption) {
        swipeTouchListener.clearClickableViews();
        //A Warning or Reminder (not a real option)
        if(selectedOption==null){
            return;
        }
        //A question with real options -> darken non selected
        ViewGroup vgTable = (ViewGroup) view.getParent().getParent();
        for (int rowPos = 0; rowPos < vgTable.getChildCount(); rowPos++) {
            ViewGroup vgRow = (ViewGroup) vgTable.getChildAt(rowPos);
            for (int itemPos = 0; itemPos < vgRow.getChildCount(); itemPos++) {
                View childItem = vgRow.getChildAt(itemPos);
                if (childItem instanceof ImageView) {
                    //We dont want the user to click anything else
                    Option otherOption=(Option)childItem.getTag();
                    if(selectedOption.getId_option() != otherOption.getId_option()){
                        overshadow((FrameLayout) childItem);
                    }
                }
            }
        }
    }


    public Tab getTab() {
        return this.tab;
    }

    @Override
    public BaseAdapter getAdapter() {
        return this;
    }

    @Override
    public int getLayout() {
        return id_layout;
    }

    @Override
    public Float getScore() {
        return 0F;
    }

    /**
     * No scores required
     */
    @Override
    public void initializeSubscore() {
    }

    @Override
    public String getName() {
        return tab.getName();
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return this.navigationController.getCurrentQuestion();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Inflate the layout
        View rowView = lInflater.inflate(R.layout.dynamic_tab_grid_question, parent, false);

        rowView.getLayoutParams().height=parent.getHeight();
        rowView.requestLayout();
        Question question=(Question)this.getItem(position);
        // We get values from DB and put them in Session
        if(Session.getSurvey()!=null)
            Session.getSurvey().getValuesFromDB();
        // Se get the value from Session
        Value value=question.getValueBySession();

        //Question
        TextCard headerView=(TextCard) rowView.findViewById(R.id.question);

        //Load a font which support Khmer character
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + context.getString(R.string.specific_language_font));
        headerView.setTypeface(tf);
        headerView.setText(question.getForm_name());

        //question image
        if(question.getPath()!=null && !question.getPath().equals("")) {
            ImageView imageView=(ImageView) rowView.findViewById(R.id.questionImage);
            putImageInImageView(question.getPath(), imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        //Progress
        ProgressBar progressView=(ProgressBar)rowView.findViewById(R.id.dynamic_progress);
        TextView progressText=(TextView)rowView.findViewById(R.id.dynamic_progress_text);
        progressView.setMax(navigationController.getTotalPages());
        progressView.setProgress(navigationController.getCurrentPage()+1);
        progressText.setText(getLocaleProgressStatus(progressView.getProgress(), progressView.getMax()));

        //Options
        TableLayout tableLayout=(TableLayout)rowView.findViewById(R.id.options_table);

        TableRow tableRow=null;
        int typeQuestion=question.getOutput();
        swipeTouchListener.clearClickableViews();
        switch (typeQuestion){
            case Constants.IMAGES_2:
            case Constants.IMAGES_4:
            case Constants.IMAGES_6:
                List<Option> options = question.getAnswer().getOptions();
                for(int i=0;i<options.size();i++){
                    Option currentOption = options.get(i);
                    int optionID=R.id.option2;
                    int counterID=R.id.counter2;
                    int mod=i%2;
                    //First item per row requires a new row
                    if(mod==0){
                        tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_row,tableLayout,false);
                        tableLayout.addView(tableRow);
                        optionID=R.id.option1;
                        counterID=R.id.counter1;
                    }
                    //Add counter value if possible                   
                    addCounterValue(question,currentOption,tableRow,counterID);

                    FrameLayout frameLayout = (FrameLayout) tableRow.getChildAt(mod);
                    TextCard textOption = (TextCard) frameLayout.getChildAt(1);
                    setTextSettings(textOption,currentOption);
                    frameLayout.setBackgroundColor(Color.parseColor("#" + currentOption.getBackground_colour()));

                    initOptionButton(frameLayout, currentOption, value);
                }
                break;
            case Constants.IMAGES_3:
                List<Option> opts = question.getAnswer().getOptions();
                for(int i=0;i<opts.size();i++){

                    Option currentOption = opts.get(i);

                    tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_row_singleitem,tableLayout,false);
                    tableLayout.addView(tableRow);

                    //Add counter value if possible
                    addCounterValue(question,currentOption,tableRow,R.id.counter1);

                    FrameLayout frameLayout = (FrameLayout) tableRow.getChildAt(0);
                    TextCard textOption = (TextCard) frameLayout.getChildAt(1);
                    setTextSettings(textOption,currentOption);

                    frameLayout.setBackgroundColor(Color.parseColor("#" + currentOption.getBackground_colour()));

                    initOptionButton(frameLayout, currentOption, value);
                }
                break;
            case Constants.IMAGES_5:
                List<Option> answerOptions = question.getAnswer().getOptions();
                for(int i=0;i<answerOptions.size();i++) {
                    Option currentOption = answerOptions.get(i);
                    int counterID=R.id.counter2;

                    int mod = i % 2;
                    //First item per row requires a new row
                    if (mod == 0) {
                        //Every new row admits 2 options
                        tableRow = (TableRow) lInflater.inflate(R.layout.dynamic_tab_row, tableLayout, false);
                        tableLayout.addView(tableRow);
                        counterID=R.id.counter1;
                    }

                    //Add counter value if possible
                    addCounterValue(question,currentOption,tableRow,counterID);

                    FrameLayout frameLayout = (FrameLayout) tableRow.getChildAt(mod);
                    if (i == 4) {
                        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1f);
                        //remove the innecesary second imageview.
                        tableRow.removeViewAt(mod+1);
                        frameLayout.setLayoutParams(params);
                    }
                    frameLayout.setBackgroundColor(Color.parseColor("#" + currentOption.getBackground_colour()));

                    TextCard textOption = (TextCard) frameLayout.getChildAt(1);
                    setTextSettings(textOption,currentOption);


                    initOptionButton(frameLayout, currentOption, value);
                }
                break;
            case Constants.REMINDER:
            case Constants.WARNING:

                tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_row_question_text, tableLayout, false);
                tableLayout.addView(tableRow);
                List<QuestionOption> questionOptions= question.getQuestionOption();
                //Question "header" is in the first option in Options.csv
                if(questionOptions!=null && questionOptions.size()>0) {
                    initWarningText(tableRow, questionOptions.get(0).getOption());
                }

                //Question "button" is in the second option in Options.csv
                if( questionOptions!=null && questionOptions.size()>1) {
                    tableRow = (TableRow) lInflater.inflate(R.layout.dynamic_tab_row_confirm_yes, tableLayout, false);
                    tableLayout.addView(tableRow);
                    initWarningValue(tableRow,  questionOptions.get(1).getOption());
                    int paddingSize= (int) PreferencesState.getInstance().getContext().getResources().getDimension(R.dimen.question_padding);
                    tableRow.setPadding(paddingSize,paddingSize,paddingSize,paddingSize);
                }

                break;
            case Constants.PHONE:
                tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_phone_row, tableLayout, false);
                tableLayout.addView(tableRow);
                initPhoneValue(tableRow, value);
                break;
            case Constants.POSITIVE_INT:
                tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_positiveint_row, tableLayout, false);
                tableLayout.addView(tableRow);
                initPositiveIntValue(tableRow, value);
                break;
        }
        rowView.requestLayout();
        return rowView;
    }

    private void setTextSettings(TextCard textOption, Option currentOption) {
        //Fixme To show a text in laos language: change "KhmerOS.ttf" to the new laos font in donottranslate laos file.
        if (currentOption.getOptionAttribute().hasHorizontalAlignment() && currentOption.getOptionAttribute().hasVerticalAlignment())
        {
            textOption.setText(currentOption.getCode());
            textOption.setGravity(currentOption.getOptionAttribute().getGravity());
        }
        else{
            textOption.setVisibility(View.GONE);
        }
        textOption.setTextSize(currentOption.getOptionAttribute().getText_size());
    }

    private void initWarningValue(TableRow tableRow, Option option) {
        ImageView errorImage = (ImageView)tableRow.findViewById(R.id.confirm_yes);
        errorImage.setImageResource(R.drawable.option_button);
        //Add button to listener
        swipeTouchListener.addClickableView(errorImage);
        //Add text into the button
        TextView okText = (TextView)tableRow.findViewById(R.id.textcard_confirm_yes);
        okText.setText(option.getCode());
        okText.setTextSize(option.getOptionAttribute().getText_size());
    }

    private void initWarningText(TableRow tableRow, Option option) {
        TextView okText = (TextView)tableRow.findViewById(R.id.questionTextRow);
        okText.setText(option.getCode());
        okText.setTextSize(option.getOptionAttribute().getText_size());
    }
    /**
     * Adds current Counter value to image option
     * @param question Current question
     * @param currentOption Current option
     * @param tableRow Row where the counter is gonna be added
     */
    private void addCounterValue(Question question, Option currentOption, TableRow tableRow, int counterID) {
        Question optionCounter = question.findCounterByOption(currentOption);
        if(optionCounter==null){
            return;
        }
        String counterValue = ReadWriteDB.readValueQuestion(optionCounter);
        if(counterValue==null || counterValue.isEmpty()){
            return;
        }

        TextView counterText = (TextView) tableRow.findViewById(counterID);
        String counterTextValue=context.getResources().getString(R.string.option_counter);

        //Repetitions: 3
        counterText.setText(counterTextValue+counterValue);
        counterText.setVisibility(View.VISIBLE);
    }

    /**
     * Used to set the text widht like the framelayout size
     * to prevent a resize of the frameLayout if the textoption is more bigger.
     */
    private void resizeTextWidth(FrameLayout frameLayout, TextCard textOption) {
            textOption.setWidth(frameLayout.getWidth());
    }

    /**
     * Get status progress in locale strings
     * @param currentPage
     * @param totalPages
     */
    private String getLocaleProgressStatus(int currentPage, int totalPages){

        String current = context.getResources().getString(context.getResources().getIdentifier("number_"+currentPage, "string", context.getPackageName()));
        String total = context.getResources().getString(context.getResources().getIdentifier("number_"+totalPages, "string", context.getPackageName()));
        return current.concat("/").concat(total);
    }

    private void showKeyboard(Context c, View v){
        Log.d(TAG,"KEYBOARD SHOW ");
        keyboardView=v;
        InputMethodManager keyboard = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(v, 0);
    }

    /**
     * hide keyboard using a provided view
     */
    private void hideKeyboard(Context c, View v){
        Log.d(TAG,"KEYBOARD HIDE ");
        InputMethodManager keyboard = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(v!=null)
            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
       }


    /**
     * hide keyboard using a keyboardView variable view
     */
    private void hideKeyboard(Context c){
        Log.d(TAG,"KEYBOARD HIDE ");
        InputMethodManager keyboard = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(keyboardView!=null)
            keyboard.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
    }
    /**
     * Initialise NumberPicker and button to view/edit a integer between 0 and Constants.MAX_INT_AGE
     * @param tableRow
     * @param value
     */
    private void initPositiveIntValue(TableRow tableRow, Value value){
        Button button=(Button)tableRow.findViewById(R.id.dynamic_positiveInt_btn);

        final EditText numberPicker = (EditText)tableRow.findViewById(R.id.dynamic_positiveInt_edit);

        //Without setMinValue, setMaxValue, setValue in this order, the setValue is not displayed in the screen.
        numberPicker.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(Constants.MAX_INT_CHARS),
                new MinMaxInputFilter(0, 99)
        });

        //Has value? show it
        if(value!=null){
            numberPicker.setText(value.getValue());
        }

        if (!readOnly) {
            //Save the numberpicker value in the DB, and continue to the next screen.
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isClicked)
                        return;
                    isClicked=true;
                    savePositiveIntValue(numberPicker);
                }
            });

        }else{
            numberPicker.setEnabled(false);
            button.setEnabled(false);
        }

        //Add button to listener
        swipeTouchListener.addClickableView(button);

        //Take focus and open keyboard
        openKeyboard(numberPicker);
    }

    private void savePositiveIntValue(EditText numberPicker) {
        String positiveIntValue = String.valueOf(numberPicker.getText());

        //Required, empty values rejected
        if(checkEditTextNotNull(positiveIntValue)){
            numberPicker.setError(context.getString(R.string.dynamic_error_age));
            isClicked=false;
            return;
        }

        navigationController.isMovingToForward=true;
        Question question = navigationController.getCurrentQuestion();
        ReadWriteDB.saveValuesText(question, positiveIntValue);
        finishOrNext();
    }

    /**
     * Inits editText and button to view/edit the phone number
     * @param tableRow
     * @param value
     */
    private void initPhoneValue(TableRow tableRow, Value value){
        Button button=(Button)tableRow.findViewById(R.id.dynamic_phone_btn);
        final EditText editText=(EditText)tableRow.findViewById(R.id.dynamic_phone_edit);
        final Context ctx = tableRow.getContext();

        //Has value? show it
        if(value!=null){
            editText.setText(value.getValue());
        }

        //Editable? add listener
        if(!readOnly){

            //Try to format on done
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(isClicked)
                        return false;
                    isClicked=true;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String phoneValue = editText.getText().toString();
                        if (PhoneMask.checkPhoneNumberByMask(phoneValue)) {
                            editText.setText(PhoneMask.formatPhoneNumber(phoneValue));
                        }
                        savePhoneValue(editText);
                    }
                    return false;
                }
            });

            //Validate format on button click
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isClicked)
                        return;
                    isClicked=true;
                    View parentView = (View) v.getParent();
                    EditText editText = (EditText) parentView.findViewById(R.id.dynamic_phone_edit);
                    savePhoneValue(editText);
                }
            });
        }else{
            editText.setEnabled(false);
            button.setEnabled(false);
        }

        //Add button to listener
        swipeTouchListener.addClickableView(button);

        //Take focus and open keyboard
        openKeyboard(editText);
    }

    private void savePhoneValue(EditText editText) {

        String phoneValue = editText.getText().toString();
        //Check phone ok
        if(!PhoneMask.checkPhoneNumberByMask(phoneValue)){
            editText.setError(context.getString(R.string.dynamic_error_phone_format));
            isClicked=false;
            return;
        }

        navigationController.isMovingToForward=true;
        String value=editText.getText().toString();
        //Hide keypad
        hideKeyboard(PreferencesState.getInstance().getContext());
        editText.setText(value);
        Question question = navigationController.getCurrentQuestion();
        ReadWriteDB.saveValuesText(question, phoneValue);
        finishOrNext();
    }

    private void openKeyboard(final EditText editText){
        if(!readOnly) {
            editText.requestFocus();
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Show keypad
                    showKeyboard(context, editText);
                }
            }, 300);
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if(isClicked)
                        return false;
                    isClicked=true;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if(v.getId()==R.id.dynamic_positiveInt_edit)
                            savePositiveIntValue((EditText) v);
                        else if(v.getId()==R.id.dynamic_phone_edit)
                            savePhoneValue((EditText)v);
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });
        }
    }

    /**
     * Checks if the given string corresponds a correct phone number for the current country (by locale)
     * @param phoneValue
     * @return true|false
     */
    private boolean checkPhoneNumberByCountry(String phoneValue){

        //Empty  is ok
        if (phoneValue == null || "".equals(phoneValue)) {
            phoneValue = "";
        }

        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            phoneNumber = PhoneNumberUtil.getInstance().parse(phoneValue, locale.getCountry());
        } catch (NumberParseException e) {
            return false;
        }
        return PhoneNumberUtil.getInstance().isValidNumber(phoneNumber);
    }


    /**
     * Checks if edit text is not null:
     * @param editValue
     * @return true|false
     */
    private boolean checkEditTextNotNull(String editValue){
        if (editValue == null) {
            editValue = "";
        }
        return editValue.isEmpty();
    }

    /**
     * Attach an option with its button in view, adding the listener
     * @param button
     * @param option
     */
    private void initOptionButton(FrameLayout button, Option option, Value value){

        // value = null --> first time calling initOptionButton
        //Highlight button
        if (value != null && value.getValue().equals(option.getName())) {
            highlightSelection(button, option);
        } else if (value != null) {
            overshadow(button);
        }

        //the button is a framelayout that contains a imageview
        ImageView imageView= (ImageView) button.getChildAt(0);
        //Put image
        putImageInImageView(option.getPath(), imageView);
        //Associate option
        button.setTag(option);

        //Readonly (not clickable, enabled)
        if(readOnly){
            button.setEnabled(false);
            return;
        }

        //Add button to listener
        swipeTouchListener.addClickableView(button);

        resizeTextWidth(button,(TextCard) button.getChildAt(1));
    }

    private void putImageInImageView(String path, ImageView imageView) {
        try {
            if(path==null || path.equals(""))
                return;
            InputStream inputStream = context.getAssets().open(path);
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param view
     * @param option
     */
    private void highlightSelection(View view, Option option){
        Drawable selectedBackground = context.getResources().getDrawable(R.drawable.background_dynamic_clicked_option);
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {    //JELLY_BEAN=API16
            view.setBackground(selectedBackground);
        } else {
            view.setBackgroundDrawable(selectedBackground);
        }

        if(option!=null) {
            GradientDrawable bgShape = (GradientDrawable) view.getBackground();
            String backGColor = option.getOptionAttribute() != null ? option.getOptionAttribute().getBackground_colour() : option.getBackground_colour();
            bgShape.setColor(Color.parseColor("#" + backGColor));
            bgShape.setStroke(3, Color.WHITE);
        }

        //the view is a framelayout that contains a imageview
        ImageView imageView;
        if(view instanceof FrameLayout){
            FrameLayout f = (FrameLayout) view;
            imageView= (ImageView) f.getChildAt(0);
        }else{
            imageView = (ImageView)view;
        }

        imageView.clearColorFilter();
    }

    /**
     * Puts a sort of dark shadow over the given view
     * @param view
     */
    private void overshadow(FrameLayout view){
        //FIXME: (API17) setColorFilter for view.getBackground() has no effect...
        view.getBackground().setColorFilter(Color.parseColor("#805a595b"), PorterDuff.Mode.SRC_ATOP);
        ImageView imageView = (ImageView) view.getChildAt(0);
        imageView.setColorFilter(Color.parseColor("#805a595b"));

        Drawable bg = view.getBackground();
        if(bg instanceof GradientDrawable) {
            GradientDrawable bgShape = (GradientDrawable)bg;
            bgShape.setStroke(0, 0);
        }
    }

    /**
     * Advance to the next question with delay applied or finish survey according to question and value.
     */
    private void finishOrNext(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Question question = navigationController.getCurrentQuestion();
                Value value = question.getValueBySession();
                if (isDone(value)) {
                    navigationController.isMovingToForward=false;
                    showDone();
                    return;
                }
                next();
            }
        }, 750);
    }

    /**
     * Show a final dialog to announce the survey is over
     */
    private void showDone(){
        final Activity activity=(Activity)context;
        AlertDialog.Builder msgConfirmation = new AlertDialog.Builder((activity))
            .setTitle(R.string.survey_title_completed)
            .setMessage(R.string.survey_info_completed)
            .setCancelable(false)
            .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int arg1) {
                    hideKeyboard(PreferencesState.getInstance().getContext());
                    if(Session.getSurvey().isRDT() && BuildConfig.reviewScreen)
                        DashboardActivity.dashboardActivity.showReviewFragment();
                    else
                        DashboardActivity.dashboardActivity.closeSurveyFragment();
                    isClicked=false;
                }
            });
        msgConfirmation.setNegativeButton(R.string.review, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                hideKeyboard(PreferencesState.getInstance().getContext());
                review();
                isClicked=false;
            }
        });

        msgConfirmation.create().show();
    }

    /**
     * Checks if there are more questions to answer according to the given value + current status.
     * @param value
     * @return
     */
    private boolean isDone(Value value){
        return !navigationController.hasNext(value!=null?value.getOption():null);
    }

    /**
     * Changes the current question moving forward
     */
    private void next(){
        Question question = navigationController.getCurrentQuestion();
        Value value = question.getValueBySession();
        if (isDone(value)) {
            navigationController.isMovingToForward=false;
            return;
        }
        navigationController.next(value!=null?value.getOption():null);
        notifyDataSetChanged();
        hideKeyboard(PreferencesState.getInstance().getContext());

        question = navigationController.getCurrentQuestion();
        value = question.getValueBySession();
        //set new page number if the value is null
        if(value==null  && !readOnly)
            navigationController.setTotalPages(navigationController.getCurrentQuestion().getTotalQuestions());
        navigationController.isMovingToForward=false;
        isClicked=false;
    }

    /**
     * Changes the current question moving backward
     */
    private void previous(){
        if(!navigationController.hasPrevious()){
            return;
        }
        navigationController.previous();
        notifyDataSetChanged();
        isClicked=false;
    }

    /**
     * Back to initial question to review questions
     */
    private void review(){
        navigationController.first();
        notifyDataSetChanged();
    }

    /**
     * When the user swip back from review fragment the navigationController should go to the last question
     */
    private void goToLastQuestion(){
        navigationController.first();
        Value value=null;
        do {
            next();
            Question question = navigationController.getCurrentQuestion();
            value = question.getValueBySession();
        }while(value!=null && !isDone(value));
        notifyDataSetChanged();
    }



}