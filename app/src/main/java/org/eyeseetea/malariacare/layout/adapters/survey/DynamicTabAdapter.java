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

import static org.eyeseetea.malariacare.database.model.Option.DOESNT_MATCH_POSITION;
import static org.eyeseetea.malariacare.database.model.Option.MATCH_POSITION;

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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
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
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.malariacare.layout.listeners.SwipeTouchListener;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.CustomButton;
import org.eyeseetea.malariacare.views.EditCard;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.filters.MinMaxInputFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import utils.PhoneMask;

/**
 * Created by Jose on 21/04/2015.
 */
public class DynamicTabAdapter extends BaseAdapter implements ITabAdapter {

    private final static String TAG = ".DynamicTabAdapter";

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

    TableLayout tableLayout = null;

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

    /**
     * Flag that indicates the number of failed validations by the active screen in multiquestion tabs
     */
    public static int failedValidations;

    public DynamicTabAdapter(Tab tab, Context context) {
        this.lInflater = LayoutInflater.from(context);
        this.context = context;
        this.id_layout = R.layout.form_without_score;

        this.navigationController = initNavigationController(tab);
        this.readOnly = Session.getSurvey() != null && !Session.getSurvey().isInProgress();
        this.isSwipeAdded = false;
        //On create dynamictabadapter, if is not readonly and has value not null it should come from reviewFragment
        if (!readOnly) {
            Question question = navigationController.getCurrentQuestion();
            if (question.getValueBySession() != null) {
                if (DashboardActivity.moveToQuestion != null) {
                    goToQuestion(DashboardActivity.moveToQuestion);
                    DashboardActivity.moveToQuestion = null;
                } else
                    goToLastQuestion();
            }
        }

        int totalPages = navigationController.getCurrentQuestion().getTotalQuestions();
        if (readOnly) {
            if (Session.getSurvey() != null) {
                Question lastQuestion = Session.getSurvey().findLastSavedQuestion();
                if (lastQuestion != null) {
                    totalPages = lastQuestion.getTotalQuestions();
                }
            }
        }
        navigationController.setTotalPages(totalPages);
        isClicked = false;
    }


    private NavigationController initNavigationController(Tab tab) {
        NavigationController navigationController = NavigationBuilder.getInstance().buildController(tab);
        navigationController.next(null);
        return navigationController;
    }

    public void addOnSwipeListener(final ListView listView) {
        if (isSwipeAdded) {
            return;
        }

        swipeTouchListener = new SwipeTouchListener(context) {
            /**
             * Click listener for image option
             * @param view
             */
            public void onClick(final View view) {
                if (isClicked) {
                    Log.d(TAG, "onClick ignored to avoid double click");
                    return;
                }

                isClicked = true;
                Log.d(TAG, "onClick");
                navigationController.isMovingToForward = true;
                final Option selectedOption = (Option) view.getTag();
                final Question question = navigationController.getCurrentQuestion();
                Question counterQuestion = question.findCounterByOption(selectedOption);
                if (counterQuestion == null) {
                    saveOptionAndMove(view, selectedOption, question);
                } else {
                    showConfirmCounter(view, selectedOption, question, counterQuestion);
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
                Log.d(TAG, "onSwipeLeft(next)");
                if (readOnly || navigationController.isNextAllowed()) {

                    //Hide keypad
                    hideKeyboard(listView.getContext(), listView);
                    next();
                }
            }

            /**
             * Adds a clickable view
             * @param view
             */
            public void addScrollView(ScrollView view) {
                super.addScrollView(scrollView);
                scrollView = view;
            }
        };

        listView.setOnTouchListener(swipeTouchListener);
    }

    private void showConfirmCounter(final View view, final Option selectedOption, final Question question, Question questionCounter) {
        //Change question x confirm message
        View rootView = view.getRootView();
        final TextCard questionView=(TextCard)rootView.findViewById(R.id.question);
        questionView.setText(questionCounter.getInternationalizedForm_name());
        ((TextView) rootView.findViewById(R.id.dynamic_progress_text)).setText("");
        //cancel
        ImageView noView = (ImageView) rootView.findViewById(R.id.confirm_no);
        noView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Leave current question as it was
                removeConfirmCounter(v);
                isClicked = false;
                notifyDataSetChanged();
            }
        });

        //confirm
        ImageView yesView = (ImageView) rootView.findViewById(R.id.confirm_yes);
        yesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeConfirmCounter(v);
                saveOptionAndMove(view, selectedOption, question);
            }
        });

        //Show confirm on full screen
        rootView.findViewById(R.id.no_scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);

        //Show question image in counter alert
        if (questionCounter.getPath() != null && !questionCounter.getPath().equals("")) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.questionImageRow);
            putImageInImageView(questionCounter.getInternationalizedPath(), imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        //Question "header" is in the first option in Options.csv
        List<Option> questionOptions = questionCounter.getAnswer().getOptions();
        if (questionOptions.get(0) != null) {
            TextCard textCard = (TextCard) rootView.findViewById(R.id.questionTextRow);
            textCard.setText(questionOptions.get(0).getInternationalizedCode());
            textCard.setTextSize(questionOptions.get(0).getOptionAttribute().getText_size());
        }
        //Question "confirm button" is in the second option in Options.csv
        if (questionOptions.get(1) != null) {
            TextCard confirmTextCard = (TextCard) rootView.findViewById(R.id.textcard_confirm_yes);
            confirmTextCard.setText(questionOptions.get(1).getInternationalizedCode());
            confirmTextCard.setTextSize(questionOptions.get(1).getOptionAttribute().getText_size());
        }
        //Question "no confirm button" is in the third option in Options.csv
        if (questionOptions.get(2) != null) {
            TextCard noConfirmTextCard = (TextCard) rootView.findViewById(R.id.textcard_confirm_no);
            noConfirmTextCard.setText(questionOptions.get(2).getInternationalizedCode());
            noConfirmTextCard.setTextSize(questionOptions.get(2).getOptionAttribute().getText_size());
        }

    }

    private void removeConfirmCounter(View view) {
        view.getRootView().findViewById(R.id.dynamic_tab_options_table).setVisibility(View.VISIBLE);
        view.getRootView().findViewById(R.id.confirm_table).setVisibility(View.GONE);
    }

    private void saveOptionAndMove(View view, Option selectedOption, Question question) {
        Value value = question.getValueBySession();
        //set new totalpages if the value is not null and the value change
        if (value != null && !readOnly) {
            navigationController.setTotalPages(question.getTotalQuestions());
        }
        if(question.getOutput().equals(Constants.IMAGE_3_NO_DATAELEMENT)){
            switchHiddenMatches(question, selectedOption);
        }else {
            ReadWriteDB.saveValuesDDL(question, selectedOption, value);
        }
        darkenNonSelected(view, selectedOption);
        LayoutUtils.highlightSelection(view, selectedOption);
        finishOrNext();
    }

    private void darkenNonSelected(View view, Option selectedOption) {
        swipeTouchListener.clearClickableViews();
        //A Warning or Reminder (not a real option)
        if (selectedOption == null) {
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
                    Option otherOption = (Option) childItem.getTag();
                    if (selectedOption.getId_option() != otherOption.getId_option()) {
                        LayoutUtils.overshadow((FrameLayout) childItem);
                    }
                }
            }
        }
    }

    /**
     * switch the matches of a no dataelement question with his hidden dataelements.
     * Only applies to question with options and matches the option position (0)/(1) Match position 1 no match position 0
     *
     * @param option
     * @return
     */
    public void switchHiddenMatches(Question question, Option option) {
        if(!question.hasOutputWithOptions() || !question.getOutput().equals(Constants.IMAGE_3_NO_DATAELEMENT)){
            return;
        }
        //Find QuestionOptions
        for(QuestionOption questionOption:question.getQuestionOption()){
            if(questionOption.getMatch().getQuestionRelation().getOperation()!=QuestionRelation.MATCH) {
                continue;
            }

            Option matchOption = questionOption.getOption();
            Question matchQuestion= questionOption.getMatch().getQuestionRelation().getQuestion();

            switchHiddenMatch(question, option, matchQuestion, matchOption);
        }
    }

    private void switchHiddenMatch(Question question, Option option, Question matchQuestion, Option matchOption){
        int optionPosition = (option.getCode().equals(matchOption.getCode())) ? MATCH_POSITION : DOESNT_MATCH_POSITION;

        if(option.getQuestionBySession()!=null) {
            ReadWriteDB.deleteValue(option.getQuestionBySession());
        }
        ReadWriteDB.saveValuesDDL(matchQuestion ,matchQuestion.getAnswer().getOptions().get(optionPosition), matchQuestion.getValueBySession());
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
        //init validation control(used only in multiquestions tabs)
        failedValidations = 0;

        //Inflate the layout
        View rowView = lInflater.inflate(R.layout.dynamic_tab_grid_question, parent, false);

        rowView.getLayoutParams().height = parent.getHeight();
        rowView.requestLayout();
        Question questionItem = (Question) this.getItem(position);
        // We get values from DB and put them in Session
        if (Session.getSurvey() != null)
            Session.getSurvey().getValuesFromDB();

        //Question
        TextCard headerView = (TextCard) rowView.findViewById(R.id.question);

        //Load a font which support Khmer character
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + context.getString(R.string.specific_language_font));
        headerView.setTypeface(tf);
        int tabType = questionItem.getHeader().getTab().getType();
        if (isMultipleQuestionTab(tabType)) {
            headerView.setText(questionItem.getHeader().getTab().getInternationalizedName());
        } else {
            headerView.setText(questionItem.getInternationalizedForm_name());
        }

        //question image
        if (questionItem.getPath() != null && !questionItem.getPath().equals("") && questionItem.hasVisibleHeaderQuestion()) {
            ImageView imageView = (ImageView) rowView.findViewById(R.id.questionImage);
            putImageInImageView(questionItem.getInternationalizedPath(), imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        //Progress
        ProgressBar progressView = (ProgressBar) rowView.findViewById(R.id.dynamic_progress);
        TextView progressText = (TextView) rowView.findViewById(R.id.dynamic_progress_text);
        progressView.setMax(navigationController.getTotalPages());
        progressView.setProgress(navigationController.getCurrentPage() + 1);
        progressText.setText(getLocaleProgressStatus(progressView.getProgress(), progressView.getMax()));

        TableRow tableRow = null;
        TableRow tableButtonRow = null;
        List<Question> screenQuestions = new ArrayList<>();

        swipeTouchListener.clearClickableViews();
        if (isMultipleQuestionTab(tabType)) {
            tableLayout = (TableLayout) rowView.findViewById(R.id.multi_question_options_table);
            (rowView.findViewById(R.id.scrolled_table)).setVisibility(View.VISIBLE);
            (rowView.findViewById(R.id.no_scrolled_table)).setVisibility(View.GONE);
            screenQuestions = questionItem.getQuestionsByTab(questionItem.getHeader().getTab());
            swipeTouchListener.addScrollView((ScrollView) (rowView.findViewById(R.id.scrolled_table)).findViewById(R.id.table_scroll));
        } else {
            tableLayout = (TableLayout) rowView.findViewById(R.id.dynamic_tab_options_table);
            (rowView.findViewById(R.id.no_scrolled_table)).setVisibility(View.VISIBLE);
            (rowView.findViewById(R.id.scrolled_table)).setVisibility(View.GONE);
            screenQuestions.add(questionItem);
        }
        Log.d(TAG,"Questions in actual tab: "+screenQuestions.size());
        for (Question screenQuestion : screenQuestions) {
            // Se get the value from Session
            int visibility=View.GONE;
            if(!screenQuestion.isHiddenBySurveyAndHeader(Session.getSurvey()) || !isMultipleQuestionTab(tabType)) {
                visibility = View.VISIBLE;
            }
            Value value = screenQuestion.getValueBySession();
            int typeQuestion = screenQuestion.getOutput();
            switch (typeQuestion) {
                case Constants.IMAGES_2:
                case Constants.IMAGES_4:
                case Constants.IMAGES_6:
                    List<Option> options = screenQuestion.getAnswer().getOptions();
                    for (int i = 0; i < options.size(); i++) {
                        Option currentOption = options.get(i);
                        int optionID = R.id.option2;
                        int counterID = R.id.counter2;
                        int mod = i % 2;
                        //First item per row requires a new row
                        if (mod == 0) {
                            tableRow = (TableRow) lInflater.inflate(R.layout.dynamic_tab_row, tableLayout, false);
                            tableLayout.addView(tableRow);
                            optionID = R.id.option1;
                            counterID = R.id.counter1;
                        }
                        //Add counter value if possible
                        addCounterValue(screenQuestion, currentOption, tableRow, counterID);

                        FrameLayout frameLayout = (FrameLayout) tableRow.getChildAt(mod);
                        TextCard textOption = (TextCard) frameLayout.getChildAt(1);
                        setTextSettings(textOption, currentOption);
                        frameLayout.setBackgroundColor(Color.parseColor("#" + currentOption.getBackground_colour()));

                        initOptionButton(frameLayout, currentOption, value);
                    }
                    break;
                case Constants.IMAGES_3:
                case Constants.IMAGE_3_NO_DATAELEMENT:
                    List<Option> opts = screenQuestion.getAnswer().getOptions();
                    for (int i = 0; i < opts.size(); i++) {

                        Option currentOption = opts.get(i);

                        tableRow = (TableRow) lInflater.inflate(R.layout.dynamic_tab_row_singleitem, tableLayout, false);
                        tableLayout.addView(tableRow);

                        //Add counter value if possible
                        addCounterValue(screenQuestion, currentOption, tableRow, R.id.counter1);

                        FrameLayout frameLayout = (FrameLayout) tableRow.getChildAt(0);
                        TextCard textOption = (TextCard) frameLayout.getChildAt(1);
                        setTextSettings(textOption, currentOption);

                        frameLayout.setBackgroundColor(Color.parseColor("#" + currentOption.getBackground_colour()));

                        initOptionButton(frameLayout, currentOption, value);
                    }
                    break;
                case Constants.IMAGES_5:
                    List<Option> answerOptions = screenQuestion.getAnswer().getOptions();
                    for (int i = 0; i < answerOptions.size(); i++) {
                        Option currentOption = answerOptions.get(i);
                        int counterID = R.id.counter2;

                        int mod = i % 2;
                        //First item per row requires a new row
                        if (mod == 0) {
                            //Every new row admits 2 options
                            tableRow = (TableRow) lInflater.inflate(R.layout.dynamic_tab_row, tableLayout, false);
                            tableLayout.addView(tableRow);
                            counterID = R.id.counter1;
                        }

                        //Add counter value if possible
                        addCounterValue(screenQuestion, currentOption, tableRow, counterID);

                        FrameLayout frameLayout = (FrameLayout) tableRow.getChildAt(mod);
                        if (i == 4) {
                            TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f);
                            //remove the unnecessary second imageview.
                            tableRow.removeViewAt(mod + 1);
                            frameLayout.setLayoutParams(params);
                        }
                        frameLayout.setBackgroundColor(Color.parseColor("#" + currentOption.getBackground_colour()));

                        TextCard textOption = (TextCard) frameLayout.getChildAt(1);
                        setTextSettings(textOption, currentOption);

                    initOptionButton(frameLayout, currentOption, value);
                }
                break;
            case Constants.REMINDER:
            case Constants.WARNING:
                View rootView = rowView.getRootView();
                //Show confirm on full screen
                rootView .findViewById(R.id.scrolled_table).setVisibility(View.GONE);
                rootView .findViewById(R.id.no_scrolled_table).setVisibility(View.GONE);
                rootView .findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.no_container).setVisibility(View.GONE);

                ((TextView) rowView.findViewById(R.id.dynamic_progress_text)).setText("");
                List<Option> questionOptions = questionItem.getAnswer().getOptions();
                //Question "header" is in the first option in Options.csv
                if(questionOptions!=null && questionOptions.size()>0) {
                    initWarningText(rootView, questionOptions.get(0));
                }
                //Question "button" is in the second option in Options.csv
                if(questionOptions!=null && questionOptions.size()>1) {
                    initWarningValue(rootView, questionOptions.get(1));
                }


                break;
                case Constants.PHONE:
                    if (isMultipleQuestionTab(tabType)) {
                        tableRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_phone_row, tableLayout, false);
                        ((TextCard) tableRow.findViewById(R.id.row_header_text)).setText(screenQuestion.getForm_name());
                    } else {
                        tableRow = (TableRow) lInflater.inflate(R.layout.dynamic_tab_phone_row, tableLayout, false);
                    }
                    addTagQuestion(screenQuestion, tableRow.findViewById(R.id.answer));
                    initPhoneValue(tableRow, value, tabType);
                    tableRow.setVisibility(visibility);
                    tableLayout.addView(tableRow);
                    break;
                case Constants.POSITIVE_INT:
                    if (isMultipleQuestionTab(tabType)) {
                        tableRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_positive_int_row, tableLayout, false);
                        ((TextCard) tableRow.findViewById(R.id.row_header_text)).setText(screenQuestion.getForm_name());
                    } else {
                        tableRow = (TableRow) lInflater.inflate(R.layout.dynamic_tab_positiveint_row, tableLayout, false);
                    }
                    addTagQuestion(screenQuestion, tableRow.findViewById(R.id.answer));
                    initPositiveIntValue(tableRow, value, tabType);
                    tableRow.setVisibility(visibility);
                    tableLayout.addView(tableRow);
                    break;
                case Constants.INT:
                    tableRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_int_row, tableLayout, false);
                    addTagQuestion(screenQuestion, tableRow.findViewById(R.id.answer));
                    initIntValue(tableRow, value);
                    tableRow.setVisibility(visibility);
                    tableLayout.addView(tableRow);
                    break;
                case Constants.LONG_TEXT:
                    tableRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_long_text_row, tableLayout, false);
                    ((TextCard) tableRow.findViewById(R.id.row_header_text)).setText(screenQuestion.getForm_name());
                    addTagQuestion(screenQuestion, tableRow.findViewById(R.id.answer));
                    initLongTextValue(tableRow, value);
                    tableRow.setVisibility(visibility);
                    tableLayout.addView(tableRow);
                    break;
                case Constants.SHORT_TEXT:
                    tableRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_short_text_row, tableLayout, false);
                    ((TextCard) tableRow.findViewById(R.id.row_header_text)).setText(screenQuestion.getForm_name());
                    addTagQuestion(screenQuestion, tableRow.findViewById(R.id.answer));
                    initShortTextValue(tableRow, value);
                    tableRow.setVisibility(visibility);
                    tableLayout.addView(tableRow);
                    break;
                case Constants.QUESTION_LABEL:
                    tableRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_label_row, tableLayout, false);
                    ((TextCard) tableRow.findViewById(R.id.row_header_text)).setText(screenQuestion.getForm_name());
                    if(screenQuestion.getPath()!=null && !screenQuestion.getPath().equals("")) {
                        ImageView rowImageView = ((ImageView) tableRow.findViewById(R.id.question_image_row));
                        rowImageView.setVisibility(View.VISIBLE);
                        putImageInImageView(screenQuestion.getInternationalizedPath(), rowImageView);
                    }
                    ((TextCard) tableRow.findViewById(R.id.row_header_text)).setText(screenQuestion.getForm_name());
                    tableRow.setVisibility(visibility);
                    tableLayout.addView(tableRow);
                    break;
                case Constants.DROPDOWN_LIST:
                case Constants.DROPDOWN_OU_LIST:
                    tableRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_dropdown_row, tableLayout, false);
                    ((TextCard) tableRow.findViewById(R.id.row_header_text)).setText(screenQuestion.getForm_name());
                    addTagQuestion(screenQuestion, tableRow.findViewById(R.id.answer));
                    tableRow = populateSpinnerFromOptions(tableRow, screenQuestion);
                    initDropdownValue(tableRow, value);
                    tableRow.setVisibility(visibility);
                    tableLayout.addView(tableRow);
                    break;
                case Constants.SWITCH_BUTTON:
                    tableRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_switch_row, tableLayout, false);
                    ((TextCard) tableRow.findViewById(R.id.row_header_text)).setText(screenQuestion.getForm_name());
                    if(screenQuestion.getPath()!=null && !screenQuestion.getPath().equals("")) {
                            ImageView rowImageView = ((ImageView) tableRow.findViewById(R.id.question_image_row));
                            rowImageView.setVisibility(View.VISIBLE);
                            putImageInImageView(screenQuestion.getInternationalizedPath(), rowImageView);
                    }
                    ((TextCard) tableRow.findViewById(R.id.row_switch_yes)).setText(screenQuestion.getAnswer().getOptions().get(0).getCode());
                    ((TextCard) tableRow.findViewById(R.id.row_switch_no)).setText(screenQuestion.getAnswer().getOptions().get(1).getCode());
                    Switch switchView=(Switch) tableRow.findViewById(R.id.answer);
                    addTagQuestion(screenQuestion, tableRow.findViewById(R.id.answer));
                    initSwitchOption(screenQuestion, switchView);
                    tableRow.setVisibility(visibility);
                    tableLayout.addView(tableRow);
                    showOrHideChildren(screenQuestion);
                    break;
            }
        }
        if (isMultipleQuestionTab(tabType)) {
            tableButtonRow = (TableRow) lInflater.inflate(R.layout.multi_question_tab_button_row, tableLayout, false);
            tableLayout.addView(createMultipleQuestionsNextButton(tableButtonRow));
        }
        rowView.requestLayout();
        return rowView;
    }

    /**
     * Populat the dropdown (spinners) from question answer options
     *
     * @return
     */
    private TableRow populateSpinnerFromOptions(TableRow tableRow, Question question) {
        Spinner dropdown_list = (Spinner) tableRow.findViewById(R.id.answer);
        // In case the option is selected, we will need to show num/dems
        List<Option> optionList = new ArrayList<>(question.getAnswer().getOptions());
        optionList.add(0, new Option(Constants.DEFAULT_SELECT_OPTION));
        dropdown_list.setAdapter(new OptionArrayAdapter(context, optionList));
        return tableRow;
    }

    /**
     * Create a button for the question with multiple question tab
     *
     * @return
     */
    private View createMultipleQuestionsNextButton(TableRow tableButtonRow) {
        Button button = (Button) tableButtonRow.findViewById(R.id.multi_question_btn);
        //Save the numberpicker value in the DB, and continue to the next screen.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Questions with failed validation " + failedValidations);
                if (failedValidations == 0) {
                    finishOrNext();
                }
            }
        });
        return tableButtonRow;
    }


    private void setTextSettings(TextCard textOption, Option currentOption) {
        //Fixme To show a text in laos language: change "KhmerOS.ttf" to the new laos font in donottranslate laos file.
        if (currentOption.getOptionAttribute().hasHorizontalAlignment() && currentOption.getOptionAttribute().hasVerticalAlignment())
        {
            textOption.setText(currentOption.getInternationalizedCode());
            textOption.setGravity(currentOption.getOptionAttribute().getGravity());
        } else {
            textOption.setVisibility(View.GONE);
        }
        textOption.setTextSize(currentOption.getOptionAttribute().getText_size());
    }

    private void initWarningValue(View rootView, Option option) {
        ImageView errorImage = (ImageView)rootView.findViewById(R.id.confirm_yes);
        errorImage.setImageResource(R.drawable.option_button);
        //Add button to listener
        swipeTouchListener.addClickableView(errorImage);
        //Add text into the button
        TextView okText = (TextView)rootView.findViewById(R.id.textcard_confirm_yes);
        okText.setText(option.getInternationalizedCode());
        okText.setTextSize(option.getOptionAttribute().getText_size());
    }

    private void initWarningText(View rootView, Option option) {
        TextView okText = (TextView)rootView.findViewById(R.id.questionTextRow);
        okText.setText(option.getInternationalizedCode());
        okText.setTextSize(option.getOptionAttribute().getText_size());
    }

    /**
     * Adds current Counter value to image option
     *
     * @param question      Current question
     * @param currentOption Current option
     * @param tableRow      Row where the counter is gonna be added
     */
    private void addCounterValue(Question question, Option currentOption, TableRow tableRow, int counterID) {
        Question optionCounter = question.findCounterByOption(currentOption);
        if (optionCounter == null) {
            return;
        }
        String counterValue = ReadWriteDB.readValueQuestion(optionCounter);
        if (counterValue == null || counterValue.isEmpty()) {
            return;
        }

        TextView counterText = (TextView) tableRow.findViewById(counterID);
        String counterTextValue = context.getResources().getString(R.string.option_counter);

        //Repetitions: 3
        counterText.setText(counterTextValue + counterValue);
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
     *
     * @param currentPage
     * @param totalPages
     */
    private String getLocaleProgressStatus(int currentPage, int totalPages) {

        String current = context.getResources().getString(context.getResources().getIdentifier("number_" + currentPage, "string", context.getPackageName()));
        String total = context.getResources().getString(context.getResources().getIdentifier("number_" + totalPages, "string", context.getPackageName()));
        return current.concat("/").concat(total);
    }

    private void showKeyboard(Context c, View v) {
        Log.d(TAG, "KEYBOARD SHOW ");
        keyboardView = v;
        InputMethodManager keyboard = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(v, 0);
    }

    /**
     * hide keyboard using a provided view
     */
    private void hideKeyboard(Context c, View v) {
        Log.d(TAG, "KEYBOARD HIDE ");
        InputMethodManager keyboard = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (v != null)
            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    /**
     * hide keyboard using a keyboardView variable view
     */
    private void hideKeyboard(Context c) {
        Log.d(TAG, "KEYBOARD HIDE ");
        InputMethodManager keyboard = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keyboardView != null)
            keyboard.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
    }

    /**
     * Initialise NumberPicker and button to view/edit a integer between 0 and Constants.MAX_INT_AGE
     *
     * @param tableRow
     * @param value
     */
    private void initPositiveIntValue(TableRow tableRow, Value value, int tabType) {
        Button button = null;
        if (!isMultipleQuestionTab(tabType)) {
            button = (Button) tableRow.findViewById(R.id.dynamic_positiveInt_btn);
        }
        final EditText numberPicker = (EditText) tableRow.findViewById(R.id.answer);

        //Without setMinValue, setMaxValue, setValue in this order, the setValue is not displayed in the screen.
        numberPicker.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(Constants.MAX_INT_CHARS),
                new MinMaxInputFilter(0, 99)
        });

        //Has value? show it
        if (value != null) {
            numberPicker.setText(value.getValue());
        }
        if (!readOnly) {
            if (isMultipleQuestionTab(tabType)) {
                numberPicker.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        boolean isValidNewValue = validatePositiveIntValue(s.toString());
                        Object oldValue = numberPicker.getTag(R.id.TAG_VALIDATION_OLD_VALUE);
                        //if the oldValue is null or not validated value, and the new value is correct, we need decrement the failedValidations variable.
                        if (oldValue == null || (!validatePositiveIntValue(oldValue.toString()) && isValidNewValue)) {
                            failedValidations--;
                        } else if (!isValidNewValue) {
                            //if the value is not valid, in the positiveInteger it only happends when a user erase the text and it's always is necessary the increment of failedValidations.
                            failedValidations++;
                        }
                        if (isValidNewValue) {
                            savePositiveIntValue(numberPicker);
                        } else if (!validatePositiveIntValue(s.toString())) {
                            ReadWriteDB.deleteValue((Question) numberPicker.getTag());
                            numberPicker.setError(context.getString(R.string.dynamic_error_age));
                        }
                        numberPicker.setTag(R.id.TAG_VALIDATION_OLD_VALUE, s.toString());
                        Log.d("onTextChanged", "end " + s.toString() + " bool: " + validatePhoneValue(s.toString()));
                        Log.d("onTextChanged", "total: " + failedValidations);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Object oldValue = numberPicker.getTag(R.id.TAG_VALIDATION_OLD_VALUE);
                        if (oldValue == null) {
                            Question question = (Question) numberPicker.getTag();
                            Value value = question.getValueBySession();
                            if (value != null) {
                                numberPicker.setTag(R.id.TAG_VALIDATION_OLD_VALUE, value.getValue());
                            }
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
            } else {
                //Save the numberpicker value in the DB, and continue to the next screen.
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isClicked)
                            return;
                        isClicked = true;
                        if (clickPositiveIntValue(numberPicker)) {
                            finishOrNext();
                        }
                    }
                });
            }
            if (isMultipleQuestionTab(tabType) && value == null) {
                failedValidations++;
            }
        } else {
            numberPicker.setEnabled(false);
            if (!isMultipleQuestionTab(tabType)) {
                button.setEnabled(false);
            }
        }

        if (!isMultipleQuestionTab(tabType)) {
            //Add button to listener
            swipeTouchListener.addClickableView(button);
        }
        //Take focus and open keyboard
        openKeyboard(numberPicker);
    }

    /**
     * Checks if a tab is a multiple question Tab
     *
     * @return
     */
    private boolean isMultipleQuestionTab(int tabType) {
        return tabType == Constants.TAB_MULTI_QUESTION;
    }

    /**
     * Click, check and save the phone value
     *
     * @return
     */
    private boolean clickPhoneValue(EditCard editCard) {
        //Hide keypad
        hideKeyboard(PreferencesState.getInstance().getContext());
        String valueAsText = String.valueOf(editCard.getText());
        boolean isValid = validatePhoneValue(valueAsText);
        if (isValid) {
            navigationController.isMovingToForward = true;
            saveValue(editCard);
        } else {
            editCard.setError(context.getString(R.string.dynamic_error_phone_format));
        }
        return isValid;
    }

    /**
     * Validate the phone number
     *
     * @return
     */
    private boolean validatePhoneValue(String valueAsText) {
        //Required, empty values rejected
        if (!PhoneMask.checkPhoneNumberByMask(valueAsText)) {
            isClicked = false;
            return false;
        }
        return true;
    }

    /**
     * Click, check and save the positive int value
     *
     * @return
     */
    private boolean clickPositiveIntValue(EditText editText) {
        //Hide keypad
        hideKeyboard(PreferencesState.getInstance().getContext());
        String valueAsText = String.valueOf(editText.getText());
        boolean isValid = validatePositiveIntValue(valueAsText);
        if (isValid) {
            navigationController.isMovingToForward = true;
            savePositiveIntValue(editText);
        } else {
            editText.setError(context.getString(R.string.dynamic_error_age));
        }
        return isValid;
    }

    private boolean validatePositiveIntValue(String valueAsText) {
        //Required, empty values rejected
        if (checkEditTextNotNull(valueAsText)) {
            isClicked = false;
            return false;
        }
        return true;
    }

    /**
     * Inits editText and button to view/edit the phone number
     *
     * @param tableRow
     * @param value
     */
    private void initPhoneValue(TableRow tableRow, Value value, int tabType) {
        final EditCard editCard = (EditCard) tableRow.findViewById(R.id.answer);
        //Has value? show it
        if (value != null) {
            editCard.setText(value.getValue());
        }
        Button button = null;
        if (!isMultipleQuestionTab(tabType)) {
            button = (Button) tableRow.findViewById(R.id.row_phone_btn);
        }
        //Editable? add listener
        if (!readOnly) {
            if (isMultipleQuestionTab(tabType)) {
                editCard.addTextChangedListener(new TextWatcher() {
                    boolean isValid;

                    @Override
                    public void afterTextChanged(Editable s) {
                        boolean isValidNewValue = validatePhoneValue(s.toString());
                        if (isValid != isValidNewValue) {
                            if (isValidNewValue) {
                                failedValidations--;
                            } else {
                                failedValidations++;
                            }
                        }
                        if (isValidNewValue) {
                            saveValue(editCard);
                        } else {
                            editCard.setError(context.getString(R.string.dynamic_error_phone_format));
                        }
                        editCard.setTag(R.id.TAG_VALIDATION_OLD_VALUE, s.toString());
                        Log.d("onTextChanged", "end " + s.toString() + " bool: " + validatePhoneValue(s.toString()));
                        Log.d("onTextChanged", "total: " + failedValidations);
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        Object oldValue = editCard.getTag(R.id.TAG_VALIDATION_OLD_VALUE);
                        if (oldValue == null) {
                            //The phone with null value is valid
                            isValid = true;
                        } else {
                            isValid = validatePhoneValue(oldValue.toString());
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
            } else {
                //Validate format on button click
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isClicked)
                            return;
                        isClicked = true;
                        View parentView = (View) v.getParent();
                        EditCard editCard = (EditCard) parentView.findViewById(R.id.answer);
                        if (clickPhoneValue(editCard)) {
                            finishOrNext();
                        }
                    }
                });
            }
        } else {
            editCard.setEnabled(false);
            if (!isMultipleQuestionTab(tabType)) {
                button.setEnabled(false);
            }
        }

        if (!isMultipleQuestionTab(tabType)) {
            //Add button to listener
            swipeTouchListener.addClickableView(button);
        }
        //Take focus and open keyboard
        openKeyboard(editCard);
    }

    /**
     * Adds listener to the Editcard and sets the default or saved value
     *
     * @return
     */
    private void initIntValue(TableRow row, Value value) {
        final EditCard numberPicker = (EditCard) row.findViewById(R.id.answer);

        //Has value? show it
        if (value != null) {
            numberPicker.setText(value.getValue());
        }

        if (!readOnly) {
            numberPicker.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //Save the numberpicker value in the DB, and continue to the next screen.
                    saveValue(numberPicker);
                }
            });
        } else {
            numberPicker.setEnabled(false);
        }
        //Take focus and open keyboard
        openKeyboard(numberPicker);
    }

    /**
     * Adds question as tag in a view to identify the answers
     *
     * @return
     */
    private void addTagQuestion(Question question, View viewById) {
        viewById.setTag(question);
    }


    /**
     * Adds listener to the Editcard and sets the default or saved value
     *
     * @return
     */
    private void initShortTextValue(TableRow row, Value value) {
        final EditCard numberPicker = (EditCard) row.findViewById(R.id.answer);
        //Has value? show it
        if (value != null) {
            numberPicker.setText(value.getValue());
        }

        if (!readOnly) {
            numberPicker.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //Save the numberpicker value in the DB, and continue to the next screen.
                    Question question = (Question) numberPicker.getTag();
                    ReadWriteDB.saveValuesText(question, String.valueOf(s));
                    showOrHideChildren(question);
                }
            });
        } else {
            numberPicker.setEnabled(false);
        }
        //Take focus and open keyboard
        openKeyboard(numberPicker);
    }

    private void initLongTextValue(TableRow row, Value value) {
        final EditCard numberPicker = (EditCard) row.findViewById(R.id.answer);

        //Has value? show it
        if (value != null) {
            numberPicker.setText(value.getValue());
        }

        if (!readOnly) {
            numberPicker.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //Save the numberpicker value in the DB, and continue to the next screen.
                    ReadWriteDB.saveValuesText((Question) numberPicker.getTag(), String.valueOf(s));
                }
            });
        } else {
            numberPicker.setEnabled(false);
        }
        //Take focus and open keyboard
        openKeyboard(numberPicker);
    }

    /**
     * Adds listener to the dropdown and sets the default or saved value
     *
     * @return
     */
    private void initDropdownValue(TableRow row, Value value) {
        Spinner dropdown = (Spinner) row.findViewById(R.id.answer);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                Option option = (Option) parent.getItemAtPosition(position);
                Question question = (Question) parent.getTag();
                if(question.getOutput().equals(Constants.IMAGE_3_NO_DATAELEMENT)){
                    switchHiddenMatches(question, option);
                }else {
                    ReadWriteDB.saveValuesDDL(question, option, question.getValueBySession());
                }
                showOrHideChildren(question);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (value != null && value.getValue() != null) {
            for (int i = 0; i < dropdown.getAdapter().getCount(); i++) {
                Option option = (Option) dropdown.getItemAtPosition(i);
                if (option.equals(value.getOption())) {
                    dropdown.setSelection(i);
                    break;
                }
            }
        }
    }
    /**
     * Hide or show the childen question from a given question,  if is necessary  it reloads the children questions values or refreshing the children questions answer component
     *
     * @param question is the parent question
     * @return
     */
    private void showOrHideChildren(Question question) {
        if(question.hasChildren()) {
            for (int i = 0, j = tableLayout.getChildCount(); i < j; i++) {
                View view = tableLayout.getChildAt(i);
                if (view instanceof TableRow) {
                    TableRow row = (TableRow) view;
                    View answerView = view.findViewById(R.id.answer);
                    if (answerView == null) {
                        continue;
                    }
                    Question rowQuestion = (Question) answerView.getTag();
                    if (rowQuestion == null) {
                        continue;
                    }
                    List<Question> questionChildren = question.getChildren();
                    if (questionChildren != null && questionChildren.size() > 0) {
                        for (Question childQuestion : questionChildren) {
                            //if the table row question is child of the modified question...
                            toggleChild(row, rowQuestion, childQuestion);
                        }
                    }
                }
            }
        }
    }

    /**
     * find and toggle the child question
     *
     * @param row is the child question view
     * @param rowQuestion is the question in the view
     * @param childQuestion is the posible child
     * @return
     */
    private boolean toggleChild(TableRow row, Question rowQuestion, Question childQuestion) {
        if (childQuestion.getId_question().equals(rowQuestion.getId_question())) {
            if (rowQuestion.isHiddenBySurveyAndHeader(Session.getSurvey())) {
                row.setVisibility(View.GONE);
                hideDefaultValue(rowQuestion);
            } else {
                row.setVisibility(View.VISIBLE);
                showDefaultValue(row, rowQuestion);
            }
            return true;
        }
        return false;
    }

    /**
     * removes or modify the value with a correct value when the question is hide
     *
     * @param rowQuestion is the question in the view
     * @return
     */
    private void hideDefaultValue(Question rowQuestion){
        switch(rowQuestion.getOutput()) {
            case Constants.PHONE:
            case Constants.POSITIVE_INT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.SHORT_TEXT:
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                ReadWriteDB.deleteValue(rowQuestion);
                break;
            case Constants.SWITCH_BUTTON:
                saveSwitchOption(rowQuestion, true);
                break;
        }
    }

    /**
     * when a question is shown this method set the correct value.
     *
     * @param rowQuestion is the question in the view
     * @return
     */
    private void showDefaultValue(TableRow tableRow, Question rowQuestion) {
        if (rowQuestion.getValueBySession() != null) {
            return;
        }
        switch(rowQuestion.getOutput()) {
            case Constants.PHONE:
            case Constants.POSITIVE_INT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.SHORT_TEXT:
                final EditCard editCard = (EditCard) tableRow.findViewById(R.id.answer);
                editCard.setText("");
                break;
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                Spinner dropdown = (Spinner) tableRow.findViewById(R.id.answer);
                dropdown.setSelection(0);
                break;
            case Constants.SWITCH_BUTTON:
                Switch switchView=(Switch)tableRow.findViewById(R.id.answer);
                Option selectedOption=rowQuestion.getOptionBySession();
                if(selectedOption==null){
                    //Fixme true is the default value
                    saveSwitchOption(rowQuestion, true);
                }
                switchView.setChecked(findSwitchBoolean(rowQuestion));
                break;
        }
    }

    /**
     * Save value from a Positive Integer question in DB and check the children
     *
     * @return
     */
    private void savePositiveIntValue(EditText numberPicker) {
        String valueAsText = String.valueOf(numberPicker.getText());

        //The text is truncated as integer ( 00-0 , 01-1 , etc. ) before save and send as string.
        Integer positiveIntValue = Integer.parseInt(valueAsText);
        Question question = (Question) numberPicker.getTag();
        ReadWriteDB.saveValuesText(question, positiveIntValue.toString());
        showOrHideChildren(question);
    }

    /**
     * Save value in DB and check the children
     *
     * @return
     */
    private void saveValue(EditCard editCard) {
        Question question = (Question) editCard.getTag();
        ReadWriteDB.saveValuesText(question, editCard.getText().toString());
        showOrHideChildren(question);
    }

    /**
     * Open keyboard and add listeners to click/next option.
     *
     * @return
     */
    private void openKeyboard(final EditText editText) {
        if (!readOnly) {
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
                    if (isClicked)
                        return false;
                    isClicked = true;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        EditCard editText = ((EditCard) v);
                        if (v.getId() == R.id.answer) {
                            if (clickPositiveIntValue(editText)) {
                                finishOrNext();
                            }
                        } else if (v.getId() == R.id.answer) {
                            if (clickPhoneValue(editText)) {
                                finishOrNext();
                            }
                        }
                        return true;
                    } else {
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
     *
     * @param editValue
     * @return true|false
     */
    private boolean checkEditTextNotNull(String editValue) {
        if (editValue == null) {
            editValue = "";
        }
        return editValue.isEmpty();
    }

    /**
     * Attach an option with its button in view, adding the listener
     *
     * @param button
     * @param option
     */
    private void initOptionButton(FrameLayout button, Option option, Value value) {
        // value = null --> first time calling initOptionButton
        //Highlight button
        if (value != null && value.getValue().equals(option.getName())) {
            LayoutUtils.highlightSelection(button, option);
        } else if (value != null) {
            LayoutUtils.overshadow(button);
        }

        //the button is a framelayout that contains a imageview
        ImageView imageView = (ImageView) button.getChildAt(0);
        //Put image
        putImageInImageView(option.getInternationalizedPath(), imageView);
        //Associate option
        button.setTag(option);

        //Readonly (not clickable, enabled)
        if (readOnly) {
            button.setEnabled(false);
            return;
        }

        //Add button to listener
        swipeTouchListener.addClickableView(button);

        resizeTextWidth(button, (TextCard) button.getChildAt(1));
    }

    /**
     * Sets a image from assets path in a imageView
     *
     * @param path path from assets image
     * @param imageView is the imageView to set the image
     * @return
     */
    private void putImageInImageView(String path, ImageView imageView) {
        try {
            if (path == null || path.equals(""))
                return;
            InputStream inputStream = context.getAssets().open(Utils.getInternationalizedString(path));
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
    private void finishOrNext() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Question question = navigationController.getCurrentQuestion();
                Value value = question.getValueBySession();
                if (isDone(value)) {
                    navigationController.isMovingToForward = false;
                    if (!Session.getSurvey().isRDT() || !BuildConfig.reviewScreen)
                        showDone();
                    else {
                        DashboardActivity.dashboardActivity.showReviewFragment();
                        hideKeyboard(PreferencesState.getInstance().getContext());
                        isClicked = false;
                    }
                    return;
                }
                next();
            }
        }, 750);
    }

    /**
     * Show a final dialog to announce the survey is over without reviewfragment.
     */
    private void showDone() {
        final Activity activity = (Activity) context;
        AlertDialog.Builder msgConfirmation = new AlertDialog.Builder(context)
                .setTitle(R.string.survey_title_completed)
                .setMessage(R.string.survey_info_completed)
                .setCancelable(false)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        hideKeyboard(PreferencesState.getInstance().getContext());
                        DashboardActivity.dashboardActivity.closeSurveyFragment();
                        isClicked = false;
                    }
                });
        msgConfirmation.setNegativeButton(R.string.review, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                hideKeyboard(PreferencesState.getInstance().getContext());
                review();
                isClicked = false;
            }
        });

        msgConfirmation.create().show();
    }

    /**
     * Checks if there are more questions to answer according to the given value + current status.
     *
     * @param value
     * @return
     */
    private boolean isDone(Value value) {
        return !navigationController.hasNext(value != null ? value.getOption() : null);
    }

    /**
     * Changes the current question moving forward
     */
    private void next() {
        Question question = navigationController.getCurrentQuestion();
        Value value = question.getValueBySession();
        if (isDone(value)) {
            navigationController.isMovingToForward = false;
            return;
        }
        navigationController.next(value != null ? value.getOption() : null);
        notifyDataSetChanged();
        hideKeyboard(PreferencesState.getInstance().getContext());

        question = navigationController.getCurrentQuestion();
        value = question.getValueBySession();
        //set new page number if the value is null
        if (value == null && !readOnly)
            navigationController.setTotalPages(navigationController.getCurrentQuestion().getTotalQuestions());
        navigationController.isMovingToForward = false;
        isClicked = false;
    }

    /**
     * Changes the current question moving backward
     */
    private void previous() {
        if (!navigationController.hasPrevious()) {
            return;
        }
        navigationController.previous();
        notifyDataSetChanged();
        isClicked = false;
    }

    /**
     * Back to initial question to review questions
     */
    private void review() {
        navigationController.first();
        notifyDataSetChanged();
    }

    /**
     * When the user click in a value in the review fragment the navigationController should go to related question
     */
    private void goToQuestion(Question isMoveToQuestion) {
        navigationController.first();
        //it is compared by uid because comparing by question it could be not equal by the same question.
        while (!isMoveToQuestion.getUid().equals(navigationController.getCurrentQuestion().getUid())) {
            next();
            skipReminder();
        }
        notifyDataSetChanged();
    }

    /**
     * When the user swip back from review fragment the navigationController should go to the last question
     */
    private void goToLastQuestion() {
        navigationController.first();
        Value value = null;
        do {
            next();
            Question question = navigationController.getCurrentQuestion();
            value = question.getValueBySession();
            skipReminder();
        } while (value != null && !isDone(value));
        notifyDataSetChanged();
    }


    /**
     * Skips the reminder question in the navigation
     *
     * @return
     */
    private void skipReminder() {
        for (QuestionRelation relation : navigationController.getCurrentQuestion().getQuestionRelations())
            if (relation.isAReminder())
                next();
    }


    /**
     * Switch listener to save the switch value
     *
     * @return
     */
    public class SwitchButtonListener implements CompoundButton.OnCheckedChangeListener{

        private Question question;
        private Switch switchButton;

        public SwitchButtonListener(Question question, Switch switchButton) {
            this.question = question;
            this.switchButton = switchButton;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!buttonView.isShown()){
                return;
            }
            saveSwitchOption(question, isChecked);
        }

    }

    /**
     * Initialize the default switch value or load the saved value
     *
     * @param question is the question in the view
     * @param switchQuestion is the switch view
     * @return
     */
    private void initSwitchOption(Question question, Switch switchQuestion) {

        //Take option
        Option selectedOption=question.getOptionBySession();
        if(selectedOption==null){
            saveSwitchOption(question, switchQuestion.isChecked());
        }
        else{
            switchQuestion.setChecked(findSwitchBoolean(question));
        }
        switchQuestion.setOnCheckedChangeListener(new SwitchButtonListener(question,switchQuestion));
    }

    /**
     * Save the switch option and check children questions
     *
     * @param question is the question in the view
     * @param isChecked is the value to be saved
     * @return
     */
    private void saveSwitchOption(Question question, boolean isChecked) {
        //Take option
        Option selectedOption=findSwitchOption(question, isChecked);
        if(selectedOption==null){
            return;
        }
        ReadWriteDB.saveValuesDDL(question,selectedOption,question.getValueBySession());
        showOrHideChildren(question);
    }
    /**
     * Returns the option selected for the given question and boolean value or by position
     * @param question
     * @param isChecked
     * @return
     */
    public static Option findSwitchOption(Question question,boolean isChecked){
        String expectedCode=String.valueOf(isChecked);
        //Search "false/true" code in the options
        for(Option option:question.getAnswer().getOptions()){
            if(expectedCode.equals(option.getCode())){
                return option;
            }
        }
        //Search option by position
        if(isChecked){
            return question.getAnswer().getOptions().get(1);
        }
        else{
            return question.getAnswer().getOptions().get(0);
        }
    }
    /**
     * Returns the boolean selected for the given question (by boolean value or position option, position 1=true 0=false)
     * @param question
     * @return
     */
    public static Boolean findSwitchBoolean(Question question){
        Value value= question.getValueBySession();
        if(value.getValue().toLowerCase().equals("true")){
            return true;
        }
        else if(value.getValue().toLowerCase().equals("false")){
            return false;
        }
        else{
            if(value.getValue().equals(question.getAnswer().getOptions().get(1).getCode())){
                return true;
            }
            else if(value.getValue().equals(question.getAnswer().getOptions().get(0).getCode())) {
                return false;
            }
        }
        return  false;
    }
}