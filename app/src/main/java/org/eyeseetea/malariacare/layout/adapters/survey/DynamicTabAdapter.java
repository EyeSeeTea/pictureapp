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

import static org.eyeseetea.malariacare.R.id.question;
import static org.eyeseetea.malariacare.data.database.model.Option.DOESNT_MATCH_POSITION;
import static org.eyeseetea.malariacare.data.database.model.Option.MATCH_POSITION;
import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurvey;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.malariacare.layout.adapters.survey.strategies.ADynamicTabAdapterStrategy;
import org.eyeseetea.malariacare.layout.adapters.survey.strategies.DynamicTabAdapterStrategy;
import org.eyeseetea.malariacare.layout.listeners.SwipeTouchListener;
import org.eyeseetea.malariacare.layout.listeners.question.QuestionAnswerChangedListener;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.malariacare.presentation.factory.IQuestionViewFactory;
import org.eyeseetea.malariacare.presentation.factory.MultiQuestionViewFactory;
import org.eyeseetea.malariacare.presentation.factory.SingleQuestionViewFactory;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;
import org.eyeseetea.malariacare.strategies.UIMessagesStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.GradleVariantConfig;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.option.ImageRadioButtonOption;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.CommonQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.INavigationQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.YearSelectorQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.ImageRadioButtonSingleQuestionView;
import org.eyeseetea.malariacare.views.question.singlequestion.strategies
        .ConfirmCounterSingleCustomViewStrategy;
import org.eyeseetea.sdk.presentation.views.CustomEditText;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

import utils.ProgressUtils;

public class DynamicTabAdapter extends BaseAdapter implements ITabAdapter {

    private final static String TAG = ".DynamicTabAdapter";
    /**
     * Flag that indicates if the actual question option is clicked to prevent multiple clicks.
     */
    public static boolean isClicked;
    /**
     * Flag that indicates the number of failed validations by the active screen in multiquestion
     * tabs
     */
    public static int failedValidations;

    public static View navigationButtonHolder;
    private final Context context;
    public NavigationController navigationController;
    public boolean reloadingQuestionFromInvalidOption;
    Tab tab;
    LayoutInflater lInflater;
    TableLayout tableLayout = null;
    int id_layout;
    /**
     * View needed to close the keyboard in methods with view
     */
    View keyboardView;
    List<IMultiQuestionView> mMultiQuestionViews = new ArrayList<>();
    ADynamicTabAdapterStrategy mDynamicTabAdapterStrategy;
    /**
     * Flag that indicates if the current survey in session is already sent or not (it affects
     * readonly settings)
     */
    private boolean readOnly;

    /**
     * Flag that indicates the number of failed validations by the active screen in multiquestion
     * tabs
     * Listener that detects taps on buttons & swipe
     */
    public static SwipeTouchListener swipeTouchListener;
    private boolean mReviewMode = false;
    private boolean isBackward = true;

    public DynamicTabAdapter(Context context, boolean reviewMode) throws NullPointerException {
        mReviewMode = reviewMode;
        this.lInflater = LayoutInflater.from(context);
        this.context = context;
        this.id_layout = R.layout.form_without_score;

        this.navigationController = initNavigationController();
        this.readOnly = getMalariaSurvey() != null && !getMalariaSurvey().isInProgress();

            Question question = navigationController.getCurrentQuestion();
            if (question.getValueBySession() != null) {
                if (DashboardActivity.moveToThisUId != null) {
                    goToQuestion(DashboardActivity.moveToThisUId);
                    DashboardActivity.moveToThisUId = null;
                } else {
                    goToQuestion(question.getUid());
                }
            }


        int totalPages = 0;
        if (getMalariaSurvey() != null) {
            totalPages = getMalariaSurvey().getMaxTotalPages();
        }
        if (totalPages == 0) {
            totalPages = navigationController.getCurrentQuestion().getTotalQuestions();
        }

        navigationController.setTotalPages(totalPages);
        isClicked = false;

        mDynamicTabAdapterStrategy = new DynamicTabAdapterStrategy(this);
        mDynamicTabAdapterStrategy.initSurveys(readOnly);
    }

    /**
     * Returns the option selected for the given question and boolean value or by position
     */
    public static Option findSwitchOption(Question question, boolean isChecked) {
        //Search option by position
        return question.getAnswer().getOptions().get((isChecked) ? 0 : 1);
    }

    /**
     * Returns the boolean selected for the given question (by boolean value or position option,
     * position 1=true 0=false)
     */
    public static Boolean findSwitchBoolean(Question question) {
        Value value = question.getValueBySession();
        if (value.getValue().equals(question.getAnswer().getOptions().get(0).getCode())) {
            return true;
        } else if (value.getValue().equals(question.getAnswer().getOptions().get(1).getCode())) {
            return false;
        }
        return false;
    }

    private NavigationController initNavigationController() throws NullPointerException{
        NavigationController navigationController = Session.getNavigationController();
        navigationController.first();
        return navigationController;
    }

    public void addOnSwipeListener(final ListView listView) {
        swipeTouchListener = new SwipeTouchListener(context) {
            /**
             * Swipe right listener moves to previous question
             */
            public void onSwipeRight() {
                if (!GradleVariantConfig.isSwipeActionActive()) {
                    return;
                }
                Log.d(TAG, "onSwipeRight(previous)");
                //Hide keypad
                if (!readOnly)
                    hideKeyboard(listView.getContext(), listView);
                previous();
            }

            /**
             * Swipe left listener moves to next question
             */
            public void onSwipeLeft() {
                if (!GradleVariantConfig.isSwipeActionActive()) {
                    return;
                }
                Log.d(TAG, "onSwipeLeft(next)");
                if (readOnly)
                    next();
                else if (navigationController.isNextAllowed()) {
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

    public void OnOptionAnswered(View view, Option selectedOption, boolean moveToNextQuestion) {
        if (moveToNextQuestion) {
            navigationController.isMovingToForward = true;
        }

        Question question = (Question) view.getTag();

        if (!selectedOption.getCode().isEmpty()
                && question.getOutput() == Constants.DROPDOWN_OU_LIST) {
            OrgUnit orgUnit = OrgUnit.findByUID(selectedOption.getCode());

            assignOrgUnitToSurvey(Session.getMalariaSurvey(), orgUnit);
            assignOrgUnitToSurvey(Session.getStockSurvey(), orgUnit);
        }


        Question counterQuestion = question.findCounterByOption(selectedOption);
        if (counterQuestion == null) {
            saveOptionValue(view, selectedOption, question, moveToNextQuestion);
        } else if (!(view instanceof ImageRadioButtonSingleQuestionView)) {
            showConfirmCounter(view, selectedOption, question, counterQuestion);
        }
    }

    private void assignOrgUnitToSurvey(Survey survey, OrgUnit orgUnit) {
        if (survey != null) {
            survey.setOrgUnit(orgUnit);
            survey.save();
        }
    }

    public void saveTextValue(View view, String newValue, boolean moveToNextQuestion) {
        Question question = (Question) view.getTag();
        question.saveValuesText(newValue);

        if (moveToNextQuestion) {
            navigationController.isMovingToForward = true;
            finishOrNext();
        } else {
            showOrHideChildren(question);
        }
    }

    public void saveOptionValue(View view, Option selectedOption, Question question,
            boolean moveToNextQuestion) {
        Option answeredOption = (question != null) ? question.getAnsweredOption() : null;
        Value value = question.getValueBySession();

        if (goingBackwardAndModifiedValues(value, answeredOption, selectedOption)) {
            navigationController.setTotalPages(question.getTotalQuestions());
            isBackward = false;
        }

        question.saveValuesDDL(selectedOption, value);


        if (question.getOutput().equals(Constants.IMAGE_3_NO_DATAELEMENT) ||
                question.getOutput().equals(Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT)) {
            switchHiddenMatches(question, selectedOption);
        }

        if (moveToNextQuestion) {
            navigationController.isMovingToForward = true;
            finishOrNext();
        } else {
            showOrHideChildren(question);
        }
    }

    private boolean goingBackwardAndModifiedValues(Value value, Option answeredOption,
            Option selectedOption) {
        return isBackward && value != null && !readOnly && (answeredOption == null
                || !answeredOption.getId_option().equals(selectedOption.getId_option()));
    }

    private void showConfirmCounter(final View view, final Option selectedOption,
            final Question question, Question questionCounter) {

        ConfirmCounterSingleCustomViewStrategy confirmCounterStrategy =
                new ConfirmCounterSingleCustomViewStrategy(this);
        confirmCounterStrategy.showConfirmCounter(view, selectedOption, question, questionCounter);

        isClicked = false;
    }

    /**
     * switch the matches of a no dataelement question with his hidden dataelements.
     * Only applies to question with options and matches the option position (0)/(1) Match position
     * 1 no match position 0
     */
    public void switchHiddenMatches(Question question, Option option) {
        if (!question.hasOutputWithOptions() || (!question.getOutput().equals(
                Constants.IMAGE_3_NO_DATAELEMENT) && !question.getOutput().equals(
                Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT))) {
            return;
        }
        //Find QuestionOptions
        for (QuestionOption questionOption : question.getQuestionOption()) {
            if (questionOption.getMatch().getQuestionRelation().getOperation()
                    != QuestionRelation.MATCH) {
                continue;
            }

            Option matchOption = questionOption.getOption();
            Question matchQuestion = questionOption.getMatch().getQuestionRelation().getQuestion();

            switchHiddenMatch(question, option, matchQuestion, matchOption);
        }
    }

    private void switchHiddenMatch(Question question, Option option, Question matchQuestion,
            Option matchOption) {
        int optionPosition = (option.getName().equals(matchOption.getName())) ? MATCH_POSITION
                : DOESNT_MATCH_POSITION;

        matchQuestion.saveValuesDDL(
                matchQuestion.getAnswer().getOptions().get(optionPosition),
                matchQuestion.getValueBySession());
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
        mMultiQuestionViews.clear();
        Validation.init();
        //init validation control(used only in multiquestions tabs)
        failedValidations = 0;
        //Inflate the layout
        View rowView = lInflater.inflate(R.layout.dynamic_tab_grid_question, parent, false);

        rowView.getLayoutParams().height = parent.getHeight();
        rowView.requestLayout();
        Question questionItem = (Question) this.getItem(position);

        // We get values from DB and put them in Session
        if (getMalariaSurvey() != null) {
            if (Session.getStockSurvey() != null) {
                Session.getStockSurvey().getValuesFromDB();
            }
            getMalariaSurvey().getValuesFromDB();
        } else {
            //The survey in session is null when the user closes the surveyFragment, but the
            // getView is called.
            return convertView;
        }

        //Question
        CustomTextView headerView = (CustomTextView) rowView.findViewById(question);

        //Load a font which support Khmer character
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + context.getString(R.string.specific_language_font));
        headerView.setTypeface(tf);
        int tabType = questionItem.getHeader().getTab().getType();
        if (Tab.isMultiQuestionTab(tabType) || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(
                tabType)) {
            headerView.setText(questionItem.getHeader().getTab().getInternationalizedName());
        } else {
            headerView.setText(questionItem.getInternationalizedForm_name());
        }

        //question image
        if (questionItem.getPath() != null && !questionItem.getPath().equals("")
                && mDynamicTabAdapterStrategy.HasQuestionImageVisibleInHeader(
                questionItem.getOutput())) {
            ImageView imageView = (ImageView) rowView.findViewById(R.id.questionImage);
            BaseLayoutUtils.putImageInImageView(questionItem.getInternationalizedPath(), imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        //Progress
        ProgressUtils.updateProgressBarStatus(rowView, navigationController.getCurrentPage(),
                navigationController.getCurrentTotalPages());

        List<Question> screenQuestions = new ArrayList<>();


        if (isTabScrollable(questionItem, tabType)) {
            tableLayout = (TableLayout) rowView.findViewById(R.id.multi_question_options_table);
            (rowView.findViewById(R.id.scrolled_table)).setVisibility(View.VISIBLE);
            (rowView.findViewById(R.id.no_scrolled_table)).setVisibility(View.GONE);

            screenQuestions = mDynamicTabAdapterStrategy.addAdditionalQuestions(tabType,
                    screenQuestions);

            if (Tab.isMultiQuestionTab(tabType)) {
                screenQuestions = questionItem.getQuestionsByTab(questionItem.getHeader().getTab());
            } else if (screenQuestions.size() == 0) {
                //not have additionalQuestions(variant dependent) and is not multi question tab
                screenQuestions.add(questionItem);
            }
            mDynamicTabAdapterStrategy.addScrollToSwipeTouchListener(rowView);
        } else {
            tableLayout = (TableLayout) rowView.findViewById(R.id.dynamic_tab_options_table);
            (rowView.findViewById(R.id.no_scrolled_table)).setVisibility(View.VISIBLE);
            (rowView.findViewById(R.id.scrolled_table)).setVisibility(View.GONE);
            screenQuestions.add(questionItem);
        }

        navigationButtonHolder = rowView.findViewById(R.id.snackbar);

        if (GradleVariantConfig.isButtonNavigationActive()) {
            initializeNavigationButtons(navigationButtonHolder);
            if(navigationController.getCurrentPage()==0){
                navigationButtonHolder.findViewById(R.id.back_btn_container).setVisibility(View.GONE);
            }
            isClicked = false;
        }

        Log.d(TAG, "Questions in actual tab: " + screenQuestions.size());

        swipeTouchListener.clearClickableViews();
        for (Question screenQuestion : screenQuestions) {
            renderQuestion(rowView, tabType, screenQuestion);
        }

        rowView.requestLayout();
        reloadingQuestionFromInvalidOption = false;

        return rowView;
    }

    public void renderQuestion(View rowView, int tabType, Question screenQuestion) {
        TableRow tableRow;
        IQuestionViewFactory questionViewFactory;

        questionViewFactory = (Tab.isMultiQuestionTab(tabType)
                || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(tabType)) ?
                new MultiQuestionViewFactory() : new SingleQuestionViewFactory();

        // Se get the value from Session
        int visibility = View.GONE;

        Survey survey = new SurveyFragmentStrategy().getRenderSurvey(screenQuestion);

        if (!screenQuestion.isHiddenBySurveyAndHeader(survey)
                || !Tab.isMultiQuestionTab(tabType)) {
            visibility = View.VISIBLE;
        }
        Value value = screenQuestion.getValueBySession();

        tableRow = new TableRow(context);

        IQuestionView questionView = questionViewFactory.getView(context,
                screenQuestion.getOutput());

        if (questionView != null) {

            if (questionView instanceof IMultiQuestionView) {
                mMultiQuestionViews.add((IMultiQuestionView) questionView);
                ((IMultiQuestionView) questionView).setHeader(
                        Utils.getInternationalizedString(screenQuestion.getForm_name()));
            }

            addTagQuestion(screenQuestion, (View) questionView);

            configureLayoutParams(tabType, tableRow, (LinearLayout) questionView);

            questionView.setHelpText(
                    Utils.getInternationalizedString(screenQuestion.getHelp_text()));

            questionView.setEnabled(!readOnly);

            if (questionView instanceof IImageQuestionView) {
                ((IImageQuestionView) questionView).setImage(
                        screenQuestion.getInternationalizedPath());
            }
            mDynamicTabAdapterStrategy.renderParticularSurvey(screenQuestion, survey, questionView);

            if (questionView instanceof AOptionQuestionView) {
                ((AOptionQuestionView) questionView).setQuestion(screenQuestion);
                List<Option> options = screenQuestion.getAnswer().getOptions();
                ((AOptionQuestionView) questionView).setOptions(
                        options);
            }
            mDynamicTabAdapterStrategy.instanceOfSingleQuestion(questionView, screenQuestion);

            if (!readOnly) {
                configureAnswerChangedListener(questionView);
                mDynamicTabAdapterStrategy.configureAnswerChangedListener(this, questionView);
            }

            if (reloadingQuestionFromInvalidOption) {
                reloadingQuestionFromInvalidOption = false;
            } else {
                questionView.setValue(value);
            }

            setupNavigationByQuestionView(rowView.getRootView(), questionView);

            tableRow.addView((View) questionView);

            swipeTouchListener.addTouchableView(rowView);
            swipeTouchListener.addTouchableView(tableRow);
            swipeTouchListener.addTouchableView((View) questionView);
            swipeTouchListener.addClickableView((View) questionView);

            setVisibilityAndAddRow(tableRow, screenQuestion, visibility);
        }
    }

    private void setupNavigationByQuestionView(View rootView, IQuestionView questionView) {
        if (questionView instanceof INavigationQuestionView) {
            INavigationQuestionView navigationQuestionView = (INavigationQuestionView) questionView;

            CustomTextView textNextButton = (CustomTextView) rootView.findViewById(R.id.next_txt);
            textNextButton.setText(navigationQuestionView.nextText());
            textNextButton.setTextSize(navigationQuestionView.nextTextSize());
        }
    }

    private void setVisibilityAndAddRow(TableRow tableRow, Question screenQuestion,
            int visibility) {
        tableRow.setVisibility(visibility);
        showCompulsory(tableRow, screenQuestion);
        tableLayout.addView(tableRow);
    }

    private void showCompulsory(TableRow tableRow, Question screenQuestion) {
        if (screenQuestion.isCompulsory()) {
            ImageView rowCompulsoryView = ((ImageView) tableRow.findViewById(
                    R.id.row_header_compulsory));
            if (rowCompulsoryView != null) {
                rowCompulsoryView.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isTabScrollable(Question questionItem, int tabType) {
        return Tab.isMultiQuestionTab(tabType)
                || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(tabType)
                || questionItem.getOutput() == Constants.IMAGE_RADIO_GROUP
                || questionItem.getOutput() == Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT
                || questionItem.getOutput() == Constants.DYNAMIC_STOCK_IMAGE_RADIO_BUTTON;
    }

    private void configureAnswerChangedListener(IQuestionView questionView) {

        if (questionView instanceof AKeyboardQuestionView) {
            ((AKeyboardQuestionView) questionView).setOnAnswerChangedListener(
                    new QuestionAnswerChangedListener(this,
                            !GradleVariantConfig.isButtonNavigationActive()));
        } else if (questionView instanceof AOptionQuestionView) {
            ((AOptionQuestionView) questionView).setOnAnswerChangedListener(
                    new QuestionAnswerChangedListener(this,
                            !GradleVariantConfig.isButtonNavigationActive()));
        } else if (questionView instanceof YearSelectorQuestionView) {
            ((YearSelectorQuestionView) questionView).setOnAnswerChangedListener(
                    new QuestionAnswerChangedListener(this,
                            !GradleVariantConfig.isButtonNavigationActive()));
        }
    }

    private void configureLayoutParams(int tabType, TableRow tableRow, LinearLayout questionView) {
        if (Tab.isMultiQuestionTab(tabType) || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(
                tabType)) {

            tableRow.setLayoutParams(
                    new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT, 1));

            questionView.setLayoutParams(
                    new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT, 0.5f));
        } else {
            tableRow.setLayoutParams(
                    new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.MATCH_PARENT, 1));

            questionView.setLayoutParams(
                    new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT, 1));
        }
    }

    private void initializeNavigationButtons(View navigationButtonsHolder) {
        ImageButton button = (ImageButton) navigationButtonsHolder.findViewById(R.id.next_btn);

        ((LinearLayout) button.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClicked) {
                    Log.d(TAG, "onClick ignored to avoid double click");
                    return;
                }
                Log.d(TAG, "onClicked");

                isClicked = true;
                boolean questionsWithError = false;

                for (IMultiQuestionView multiquestionView : mMultiQuestionViews) {
                    if (((CommonQuestionView) multiquestionView).isActive()
                            && multiquestionView.hasError()) {
                        questionsWithError = true;
                        break;
                    }
                }

                Log.d(TAG, "Questions with failed validation " + failedValidations);
                if (failedValidations == 0 && !questionsWithError) {

                    TableRow currentRow = (TableRow) tableLayout.getChildAt(0);

                    if (!readOnly && currentRow != null && currentRow.getChildAt(
                            0) instanceof ImageRadioButtonSingleQuestionView) {

                        navigationController.isMovingToForward = true;

                        ImageRadioButtonSingleQuestionView imageRadioButtonSingleQuestionView =
                                (ImageRadioButtonSingleQuestionView) currentRow.getChildAt(0);

                        ImageRadioButtonOption selectedOptionView =
                                imageRadioButtonSingleQuestionView.getSelectedOptionView();

                        if (selectedOptionView != null) {
                            final Question question = navigationController.getCurrentQuestion();

                            Option selectedOption = selectedOptionView.getOption();

                            Question counterQuestion = question.findCounterByOption(
                                    selectedOption);
                            if ((mReviewMode
                                    && isCounterValueEqualToMax(question, selectedOption))) {
                                saveOptionValue(selectedOptionView,
                                        selectedOptionView.getOption(),
                                        question, true);
                            } else if (counterQuestion != null) {
                                showConfirmCounter(selectedOptionView,
                                        selectedOptionView.getOption(),
                                        question, counterQuestion);
                            } else {
                                finishOrNext();
                            }
                        } else {
                            isClicked = false;
                        }
                    } else {
                        finishOrNext();
                    }
                } else if (navigationController.getCurrentQuestion().hasCompulsoryNotAnswered()
                        || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(
                        navigationController.getCurrentTab().getType())) {
                    UIMessagesStrategy.getInstance().showCompulsoryUnansweredToast();
                    isClicked = false;
                    return;
                } else {
                    isClicked = false;
                }
            }
        });
        button = (ImageButton) navigationButtonsHolder.findViewById(R.id.back_btn);
        //Save the numberpicker value in the DB, and continue to the next screen.
        ((LinearLayout) button.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
    }

    private boolean isCounterValueEqualToMax(Question question, Option selectedOption) {

        Survey survey = SurveyFragmentStrategy.getSessionSurveyByQuestion(question);

        Float counterValue = survey.getCounterValue(question, selectedOption);

        Float maxCounter = selectedOption.getFactor();

        return counterValue.equals(maxCounter);
    }

    private void hideKeyboard(Context c, View v) {
        Log.d(TAG, "KEYBOARD HIDE ");
        InputMethodManager keyboard = (InputMethodManager) c.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (v != null) {
            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void setIsClicked(boolean isClicked) {
        DynamicTabAdapter.isClicked = isClicked;
    }

    /**
     * Adds question as tag in a view to identify the answers
     */
    private void addTagQuestion(Question question, View viewById) {
        viewById.setTag(question);
    }

    /**
     * Hide or show the childen question from a given question,  if is necessary  it reloads the
     * children questions values or refreshing the children questions answer component
     *
     * this code will be delete when DynamicTabAdapter refactoring will be completed
     *
     * @param question is the parent question
     */
    private void showOrHideChildren(Question question) {
        if (!question.hasChildren()) {
            return;
        }

        for (int i = 0, j = tableLayout.getChildCount(); i < j; i++) {
            View view = tableLayout.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;

                View targetView = row.getChildAt(0);

                if (targetView instanceof IMultiQuestionView
                        || targetView instanceof IQuestionView) {

                    Question rowQuestion = (Question) targetView.getTag();
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
     * @param row           is the child question view
     * @param rowQuestion   is the question in the view
     * @param childQuestion is the posible child
     */
    private boolean toggleChild(TableRow row, Question rowQuestion, Question childQuestion) {
        if (childQuestion.getId_question().equals(rowQuestion.getId_question())) {
            Survey survey = SurveyFragmentStrategy.getSessionSurveyByQuestion(rowQuestion);

            if (rowQuestion.isHiddenBySurveyAndHeader(survey)) {
                row.clearFocus();
                row.setVisibility(View.GONE);
                ((CommonQuestionView) row.getChildAt(0)).deactivateQuestion();
                hideDefaultValue(rowQuestion);
            } else {
                row.setVisibility(View.VISIBLE);
                ((CommonQuestionView) row.getChildAt(0)).activateQuestion();
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
     */
    private void hideDefaultValue(Question rowQuestion) {
        switch (rowQuestion.getOutput()) {
            case Constants.PHONE:
            case Constants.POSITIVE_INT:
            case Constants.POSITIVE_OR_ZERO_INT:
            case Constants.PREGNANT_MONTH_INT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.SHORT_TEXT:
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                rowQuestion.deleteValueBySession();
                break;
            case Constants.SWITCH_BUTTON:
                //the 0 option is the left option and is false in the switch, the 1 option is the
                // right option and is true
                boolean isChecked = false;
                if (rowQuestion.getAnswer().getOptions().get(
                        1).getOptionAttribute().getDefaultOption() == 1) {
                    isChecked = true;
                }
                saveSwitchOption(rowQuestion, isChecked);
                break;
        }
    }

    private void showDefaultValue(TableRow tableRow, Question rowQuestion) {
        if (rowQuestion.getValueBySession() != null) {
            return;
        }
        switch (rowQuestion.getOutput()) {
            case Constants.PHONE:
            case Constants.POSITIVE_INT:
            case Constants.POSITIVE_OR_ZERO_INT:
            case Constants.PREGNANT_MONTH_INT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.SHORT_TEXT:
                final CustomEditText editCard = (CustomEditText) tableRow.findViewById(R.id.answer);
                editCard.setText("");
                break;
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                Spinner dropdown = (Spinner) tableRow.findViewById(R.id.answer);
                dropdown.setSelection(0);
                break;
            case Constants.SWITCH_BUTTON:
                Switch switchView = (Switch) tableRow.findViewById(R.id.answer);
                Option selectedOption = rowQuestion.getOptionBySession();
                if (selectedOption == null) {
                    //the 0 option is the left option and is false in the switch, the 1 option is
                    // the right option and is true
                    boolean isChecked = false;
                    if (rowQuestion.getAnswer().getOptions().get(
                            1).getOptionAttribute().getDefaultOption() == 1) {
                        isChecked = true;
                    }
                    saveSwitchOption(rowQuestion, isChecked);
                    switchView.setChecked(isChecked);
                    break;
                }
                switchView.setChecked(findSwitchBoolean(rowQuestion));
                break;
        }
    }

    /**
     * hide keyboard using a keyboardView variable view
     */
    public void hideKeyboard(Context c) {
        Log.d(TAG, "KEYBOARD HIDE ");
        InputMethodManager keyboard = (InputMethodManager) c.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (keyboardView != null) {
            keyboard.hideSoftInputFromWindow(keyboardView.getWindowToken(), 0);
        }
    }

    /**
     * Advance to the next question with delay applied or finish survey according to question and
     * value.
     */
    public void finishOrNext() {
        mDynamicTabAdapterStrategy.finishOrNext();
    }

    /**
     * Show a final dialog to announce the survey is over without reviewfragment.
     */
    public void surveyShowDone() {
        AlertDialog.Builder msgConfirmation = new AlertDialog.Builder(context)
                .setTitle(R.string.survey_completed)
                .setMessage(R.string.survey_completed_text)
                .setCancelable(false)
                .setPositiveButton(R.string.survey_send, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        hideKeyboard(PreferencesState.getInstance().getContext());
                        DashboardActivity.dashboardActivity.completeSurvey();
                        isClicked = false;
                    }
                });
        msgConfirmation.setNegativeButton(R.string.survey_review,
                new DialogInterface.OnClickListener() {
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
     */
    public boolean isDone(Value value) {
        return !navigationController.hasNext(value != null ? value.getOption() : null);
    }

    /**
     * Changes the current question moving backward
     */
    private void previous() {
        if (!navigationController.hasPrevious()) {
            return;
        }
        navigationController.previous();
        isBackward = navigationController.getCurrentTotalPages()
                > navigationController.getCurrentQuestion().getTotalQuestions();
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
     * When the user click in a value in the review fragment the navigationController should go to
     * related question
     */
    private void goToQuestion(String questionUid) {
        navigationController.first();

        Question currentQuestion;
        boolean isQuestionFound = false;

        //it is compared by uid because comparing by question it could be not equal by the same
        // question.
        while (!isQuestionFound) {

            currentQuestion = navigationController.getCurrentQuestion();

            int tabType = currentQuestion.getHeader().getTab().getType();
            if (Tab.isMultiQuestionTab(tabType)) {
                List<Question> screenQuestions = currentQuestion.getQuestionsByTab(
                        currentQuestion.getHeader().getTab());

                for (Question question : screenQuestions) {
                    if (questionUid.equals(question.getUid())) {
                        isQuestionFound = true;
                    }
                }
            } else {
                if (questionUid.equals(currentQuestion.getUid())) {
                    isQuestionFound = true;
                }
            }


            if (!isQuestionFound) {
                next();
                skipReminder();
            }
        }

        notifyDataSetChanged();
    }

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

    private void skipReminder() {
        for (QuestionRelation relation : navigationController.getCurrentQuestion()
                .getQuestionRelations()) {
            if (relation.isAReminder()) {
                next();
            }
        }
    }

    /**
     * Changes the current question moving forward
     */
    public void next() {
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

        if (value != null && !readOnly
                && navigationController.getCurrentTotalPages() < question.getTotalQuestions()) {
            navigationController.setTotalPages(question.getTotalQuestions());
        }
        navigationController.isMovingToForward = false;
        isClicked = false;
    }

    private void saveSwitchOption(Question question, boolean isChecked) {
        Option selectedOption = findSwitchOption(question, isChecked);
        if (selectedOption == null) {
            return;
        }
        question.saveValuesDDL(selectedOption, question.getValueBySession());
        showOrHideChildren(question);
    }
}