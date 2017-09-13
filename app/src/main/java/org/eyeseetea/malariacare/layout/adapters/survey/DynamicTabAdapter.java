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
import static org.eyeseetea.malariacare.data.database.model.OptionDB.DOESNT_MATCH_POSITION;
import static org.eyeseetea.malariacare.data.database.model.OptionDB.MATCH_POSITION;
import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurveyDB;

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
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
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
    TabDB mTabDB;
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
        this.readOnly = getMalariaSurveyDB() != null && !getMalariaSurveyDB().isInProgress();

            QuestionDB questionDB = navigationController.getCurrentQuestion();
            if (questionDB.getValueBySession() != null) {
                if (DashboardActivity.moveToThisUId != null) {
                    goToQuestion(DashboardActivity.moveToThisUId);
                    DashboardActivity.moveToThisUId = null;
                } else {
                    goToQuestion(questionDB.getUid());
                }
            }


        int totalPages = 0;
        if (getMalariaSurveyDB() != null) {
            totalPages = getMalariaSurveyDB().getMaxTotalPages();
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
     * Returns the option selected for the given questionDB and boolean value or by position
     */
    public static OptionDB findSwitchOption(QuestionDB questionDB, boolean isChecked) {
        //Search option by position
        return questionDB.getAnswerDB().getOptionDBs().get((isChecked) ? 0 : 1);
    }

    /**
     * Returns the boolean selected for the given questionDB (by boolean value or position option,
     * position 1=true 0=false)
     */
    public static Boolean findSwitchBoolean(QuestionDB questionDB) {
        ValueDB valueDB = questionDB.getValueBySession();
        if (valueDB.getValue().equals(questionDB.getAnswerDB().getOptionDBs().get(0).getCode())) {
            return true;
        } else if (valueDB.getValue().equals(questionDB.getAnswerDB().getOptionDBs().get(1).getCode())) {
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

    public void OnOptionAnswered(View view, OptionDB selectedOptionDB, boolean moveToNextQuestion) {
        if (moveToNextQuestion) {
            navigationController.isMovingToForward = true;
        }

        QuestionDB questionDB = (QuestionDB) view.getTag();

        if (!selectedOptionDB.getCode().isEmpty()
                && questionDB.getOutput() == Constants.DROPDOWN_OU_LIST) {
            OrgUnitDB orgUnitDB = OrgUnitDB.findByUID(selectedOptionDB.getCode());

            assignOrgUnitToSurvey(Session.getMalariaSurveyDB(), orgUnitDB);
            assignOrgUnitToSurvey(Session.getStockSurveyDB(), orgUnitDB);
        }


        QuestionDB counterQuestionDB = questionDB.findCounterByOption(selectedOptionDB);
        if (counterQuestionDB == null) {
            saveOptionValue(view, selectedOptionDB, questionDB, moveToNextQuestion);
        } else if (!(view instanceof ImageRadioButtonSingleQuestionView)) {
            showConfirmCounter(view, selectedOptionDB, questionDB, counterQuestionDB);
        }
    }

    private void assignOrgUnitToSurvey(SurveyDB surveyDB, OrgUnitDB orgUnitDB) {
        if (surveyDB != null) {
            surveyDB.setOrgUnit(orgUnitDB);
            surveyDB.save();
        }
    }

    public void saveTextValue(View view, String newValue, boolean moveToNextQuestion) {
        QuestionDB questionDB = (QuestionDB) view.getTag();
        questionDB.saveValuesText(newValue);

        if (moveToNextQuestion) {
            navigationController.isMovingToForward = true;
            finishOrNext();
        } else {
            showOrHideChildren(questionDB);
        }
    }

    public void saveOptionValue(View view, OptionDB selectedOptionDB, QuestionDB questionDB,
            boolean moveToNextQuestion) {
        OptionDB answeredOptionDB = (questionDB != null) ? questionDB.getAnsweredOption() : null;
        ValueDB valueDB = questionDB.getValueBySession();

        if (goingBackwardAndModifiedValues(valueDB, answeredOptionDB, selectedOptionDB)) {
            navigationController.setTotalPages(questionDB.getTotalQuestions());
            isBackward = false;
        }

        questionDB.saveValuesDDL(selectedOptionDB, valueDB);


        if (questionDB.getOutput().equals(Constants.IMAGE_3_NO_DATAELEMENT) ||
                questionDB.getOutput().equals(Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT)) {
            switchHiddenMatches(questionDB, selectedOptionDB);
        }

        if (moveToNextQuestion) {
            navigationController.isMovingToForward = true;
            finishOrNext();
        } else {
            showOrHideChildren(questionDB);
        }
    }

    private boolean goingBackwardAndModifiedValues(ValueDB valueDB, OptionDB answeredOptionDB,
            OptionDB selectedOptionDB) {
        return isBackward && valueDB != null && !readOnly && (answeredOptionDB == null
                || !answeredOptionDB.getId_option().equals(selectedOptionDB.getId_option()));
    }

    private void showConfirmCounter(final View view, final OptionDB selectedOptionDB,
            final QuestionDB questionDB, QuestionDB questionDBCounter) {

        ConfirmCounterSingleCustomViewStrategy confirmCounterStrategy =
                new ConfirmCounterSingleCustomViewStrategy(this);
        confirmCounterStrategy.showConfirmCounter(view, selectedOptionDB, questionDB,
                questionDBCounter);

        isClicked = false;
    }

    /**
     * switch the matches of a no dataelement questionDB with his hidden dataelements.
     * Only applies to questionDB with options and matches the optionDB position (0)/(1) MatchDB position
     * 1 no match position 0
     */
    public void switchHiddenMatches(QuestionDB questionDB, OptionDB optionDB) {
        if (!questionDB.hasOutputWithOptions() || (!questionDB.getOutput().equals(
                Constants.IMAGE_3_NO_DATAELEMENT) && !questionDB.getOutput().equals(
                Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT))) {
            return;
        }
        //Find QuestionOptions
        for (QuestionOptionDB questionOptionDB : questionDB.getQuestionOption()) {
            if (questionOptionDB.getMatchDB().getQuestionRelationDB().getOperation()
                    != QuestionRelationDB.MATCH) {
                continue;
            }

            OptionDB matchOptionDB = questionOptionDB.getOptionDB();
            QuestionDB
                    matchQuestionDB = questionOptionDB.getMatchDB().getQuestionRelationDB().getQuestionDB();

            switchHiddenMatch(questionDB, optionDB, matchQuestionDB, matchOptionDB);
        }
    }

    private void switchHiddenMatch(QuestionDB questionDB, OptionDB optionDB, QuestionDB matchQuestionDB,
            OptionDB matchOptionDB) {
        int optionPosition = (optionDB.getName().equals(matchOptionDB.getName())) ? MATCH_POSITION
                : DOESNT_MATCH_POSITION;

        matchQuestionDB.saveValuesDDL(
                matchQuestionDB.getAnswerDB().getOptionDBs().get(optionPosition),
                matchQuestionDB.getValueBySession());
    }


    public TabDB getTabDB() {
        return this.mTabDB;
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
        return mTabDB.getName();
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
        QuestionDB questionDBItem = (QuestionDB) this.getItem(position);

        // We get values from DB and put them in Session
        if (getMalariaSurveyDB() != null) {
            if (Session.getStockSurveyDB() != null) {
                Session.getStockSurveyDB().getValuesFromDB();
            }
            getMalariaSurveyDB().getValuesFromDB();
        } else {
            //The survey in session is null when the user closes the surveyFragment, but the
            // getView is called.
            return convertView;
        }

        //QuestionDB
        CustomTextView headerView = (CustomTextView) rowView.findViewById(question);

        //Load a font which support Khmer character
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + context.getString(R.string.normal_font));
        headerView.setTypeface(tf);
        int tabType = questionDBItem.getHeaderDB().getTabDB().getType();
        if (TabDB.isMultiQuestionTab(tabType) || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(
                tabType)) {
            headerView.setText(questionDBItem.getHeaderDB().getTabDB().getInternationalizedName());
        } else {
            headerView.setText(questionDBItem.getInternationalizedForm_name());
        }

        //question image
        if (questionDBItem.getPath() != null && !questionDBItem.getPath().equals("")
                && mDynamicTabAdapterStrategy.HasQuestionImageVisibleInHeader(
                questionDBItem.getOutput())) {
            ImageView imageView = (ImageView) rowView.findViewById(R.id.questionImage);
            BaseLayoutUtils.putImageInImageView(questionDBItem.getInternationalizedPath(), imageView);
            imageView.setVisibility(View.VISIBLE);
        }

        //Progress
        ProgressUtils.updateProgressBarStatus(rowView, navigationController.getCurrentPage(),
                navigationController.getCurrentTotalPages());

        List<QuestionDB> screenQuestionDBs = new ArrayList<>();


        if (isTabScrollable(questionDBItem, tabType)) {
            tableLayout = (TableLayout) rowView.findViewById(R.id.multi_question_options_table);
            (rowView.findViewById(R.id.scrolled_table)).setVisibility(View.VISIBLE);
            (rowView.findViewById(R.id.no_scrolled_table)).setVisibility(View.GONE);

            screenQuestionDBs = mDynamicTabAdapterStrategy.addAdditionalQuestions(tabType,
                    screenQuestionDBs);

            if (TabDB.isMultiQuestionTab(tabType)) {
                screenQuestionDBs = questionDBItem.getQuestionsByTab(questionDBItem.getHeaderDB()
                        .getTabDB());
            } else if (screenQuestionDBs.size() == 0) {
                //not have additionalQuestions(variant dependent) and is not multi question tab
                screenQuestionDBs.add(questionDBItem);
            }
            mDynamicTabAdapterStrategy.addScrollToSwipeTouchListener(rowView);
        } else {
            tableLayout = (TableLayout) rowView.findViewById(R.id.dynamic_tab_options_table);
            (rowView.findViewById(R.id.no_scrolled_table)).setVisibility(View.VISIBLE);
            (rowView.findViewById(R.id.scrolled_table)).setVisibility(View.GONE);
            screenQuestionDBs.add(questionDBItem);
        }

        navigationButtonHolder = rowView.findViewById(R.id.snackbar);

        if (GradleVariantConfig.isButtonNavigationActive()) {
            initializeNavigationButtons(navigationButtonHolder);
            if(navigationController.getCurrentPage()==0){
                navigationButtonHolder.findViewById(R.id.back_btn_container).setVisibility(View.GONE);
            }
            isClicked = false;
        }

        Log.d(TAG, "Questions in actual mTabDB: " + screenQuestionDBs.size());

        swipeTouchListener.clearClickableViews();
        for (QuestionDB screenQuestionDB : screenQuestionDBs) {
            renderQuestion(rowView, tabType, screenQuestionDB);
        }

        rowView.requestLayout();
        reloadingQuestionFromInvalidOption = false;

        return rowView;
    }

    public void renderQuestion(View rowView, int tabType, QuestionDB screenQuestionDB) {
        TableRow tableRow;
        IQuestionViewFactory questionViewFactory;

        questionViewFactory = (TabDB.isMultiQuestionTab(tabType)
                || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(tabType)) ?
                new MultiQuestionViewFactory() : new SingleQuestionViewFactory();

        // Se get the valueDB from Session
        int visibility = View.GONE;

        SurveyDB surveyDB = new SurveyFragmentStrategy().getRenderSurvey(screenQuestionDB);

        if (!screenQuestionDB.isHiddenBySurveyAndHeader(surveyDB)
                || !TabDB.isMultiQuestionTab(tabType)) {
            visibility = View.VISIBLE;
        }
        ValueDB valueDB = screenQuestionDB.getValueBySession();

        tableRow = new TableRow(context);

        IQuestionView questionView = questionViewFactory.getView(context,
                screenQuestionDB.getOutput());

        if (questionView != null) {

            if (questionView instanceof IMultiQuestionView) {
                mMultiQuestionViews.add((IMultiQuestionView) questionView);
                ((IMultiQuestionView) questionView).setHeader(
                        Utils.getInternationalizedString(screenQuestionDB.getForm_name()));
            }

            addTagQuestion(screenQuestionDB, (View) questionView);

            configureLayoutParams(tabType, tableRow, (LinearLayout) questionView);

            questionView.setHelpText(
                    Utils.getInternationalizedString(screenQuestionDB.getHelp_text()));

            questionView.setEnabled(!readOnly);

            if (questionView instanceof IImageQuestionView) {
                ((IImageQuestionView) questionView).setImage(
                        screenQuestionDB.getInternationalizedPath());
            }
            mDynamicTabAdapterStrategy.renderParticularSurvey(screenQuestionDB, surveyDB, questionView);

            if (questionView instanceof AOptionQuestionView) {
                ((AOptionQuestionView) questionView).setQuestionDB(screenQuestionDB);
                List<OptionDB> optionDBs = screenQuestionDB.getAnswerDB().getOptionDBs();
                ((AOptionQuestionView) questionView).setOptions(
                        optionDBs);
            }
            mDynamicTabAdapterStrategy.instanceOfSingleQuestion(questionView, screenQuestionDB);

            if (!readOnly) {
                configureAnswerChangedListener(questionView);
                mDynamicTabAdapterStrategy.configureAnswerChangedListener(this, questionView);
            }

            if (reloadingQuestionFromInvalidOption) {
                reloadingQuestionFromInvalidOption = false;
            } else {
                questionView.setValue(valueDB);
            }

            setupNavigationByQuestionView(rowView.getRootView(), questionView);

            tableRow.addView((View) questionView);

            swipeTouchListener.addTouchableView(rowView);
            swipeTouchListener.addTouchableView(tableRow);
            swipeTouchListener.addTouchableView((View) questionView);
            swipeTouchListener.addClickableView((View) questionView);

            setVisibilityAndAddRow(tableRow, screenQuestionDB, visibility);
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

    private void setVisibilityAndAddRow(TableRow tableRow, QuestionDB screenQuestionDB,
            int visibility) {
        tableRow.setVisibility(visibility);
        showCompulsory(tableRow, screenQuestionDB);
        tableLayout.addView(tableRow);
    }

    private void showCompulsory(TableRow tableRow, QuestionDB screenQuestionDB) {
        if (screenQuestionDB.isCompulsory()) {
            ImageView rowCompulsoryView = ((ImageView) tableRow.findViewById(
                    R.id.row_header_compulsory));
            if (rowCompulsoryView != null) {
                rowCompulsoryView.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isTabScrollable(QuestionDB questionDBItem, int tabType) {
        return TabDB.isMultiQuestionTab(tabType)
                || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(tabType)
                || questionDBItem.getOutput() == Constants.IMAGE_RADIO_GROUP
                || questionDBItem.getOutput() == Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT
                || questionDBItem.getOutput() == Constants.DYNAMIC_STOCK_IMAGE_RADIO_BUTTON;
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
        if (TabDB.isMultiQuestionTab(tabType) || mDynamicTabAdapterStrategy.isMultiQuestionByVariant(
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
        View button = (View) navigationButtonsHolder.findViewById(R.id.next_btn);

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
                            final QuestionDB questionDB = navigationController.getCurrentQuestion();

                            OptionDB selectedOptionDB = selectedOptionView.getOptionDB();

                            QuestionDB counterQuestionDB = questionDB.findCounterByOption(
                                    selectedOptionDB);
                            if ((mReviewMode
                                    && isCounterValueEqualToMax(questionDB, selectedOptionDB))) {
                                saveOptionValue(selectedOptionView,
                                        selectedOptionView.getOptionDB(),
                                        questionDB, true);
                            } else if (counterQuestionDB != null) {
                                showConfirmCounter(selectedOptionView,
                                        selectedOptionView.getOptionDB(),
                                        questionDB, counterQuestionDB);
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

    private boolean isCounterValueEqualToMax(QuestionDB questionDB, OptionDB selectedOptionDB) {

        SurveyDB surveyDB = SurveyFragmentStrategy.getSessionSurveyByQuestion(questionDB);

        Float counterValue = surveyDB.getCounterValue(questionDB, selectedOptionDB);

        Float maxCounter = selectedOptionDB.getFactor();

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
     * Adds questionDB as tag in a view to identify the answers
     */
    private void addTagQuestion(QuestionDB questionDB, View viewById) {
        viewById.setTag(questionDB);
    }

    /**
     * Hide or show the childen questionDB from a given questionDB,  if is necessary  it reloads the
     * children questions values or refreshing the children questions answer component
     *
     * this code will be delete when DynamicTabAdapter refactoring will be completed
     *
     * @param questionDB is the parent questionDB
     */
    private void showOrHideChildren(QuestionDB questionDB) {
        if (!questionDB.hasChildren()) {
            return;
        }

        for (int i = 0, j = tableLayout.getChildCount(); i < j; i++) {
            View view = tableLayout.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;

                View targetView = row.getChildAt(0);

                if (targetView instanceof IMultiQuestionView
                        || targetView instanceof IQuestionView) {

                    QuestionDB rowQuestionDB = (QuestionDB) targetView.getTag();
                    if (rowQuestionDB == null) {
                        continue;
                    }
                    List<QuestionDB> questionDBChildren = questionDB.getChildren();
                    if (questionDBChildren != null && questionDBChildren.size() > 0) {
                        for (QuestionDB childQuestionDB : questionDBChildren) {
                            //if the table row questionDB is child of the modified questionDB...
                            toggleChild(row, rowQuestionDB, childQuestionDB);
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
     * @param rowQuestionDB   is the question in the view
     * @param childQuestionDB is the posible child
     */
    private boolean toggleChild(TableRow row, QuestionDB rowQuestionDB, QuestionDB childQuestionDB) {
        if (childQuestionDB.getId_question().equals(rowQuestionDB.getId_question())) {
            SurveyDB surveyDB = SurveyFragmentStrategy.getSessionSurveyByQuestion(rowQuestionDB);

            if (rowQuestionDB.isHiddenBySurveyAndHeader(surveyDB)) {
                row.clearFocus();
                row.setVisibility(View.GONE);
                ((CommonQuestionView) row.getChildAt(0)).deactivateQuestion();
                hideDefaultValue(rowQuestionDB);
            } else {
                row.setVisibility(View.VISIBLE);
                ((CommonQuestionView) row.getChildAt(0)).activateQuestion();
                showDefaultValue(row, rowQuestionDB);
            }
            return true;
        }
        return false;
    }

    /**
     * removes or modify the value with a correct value when the question is hide
     *
     * @param rowQuestionDB is the question in the view
     */
    private void hideDefaultValue(QuestionDB rowQuestionDB) {
        switch (rowQuestionDB.getOutput()) {
            case Constants.PHONE:
            case Constants.POSITIVE_INT:
            case Constants.POSITIVE_OR_ZERO_INT:
            case Constants.PREGNANT_MONTH_INT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.SHORT_TEXT:
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                rowQuestionDB.deleteValueBySession();
                break;
            case Constants.SWITCH_BUTTON:
                //the 0 option is the left option and is false in the switch, the 1 option is the
                // right option and is true
                boolean isChecked = false;
                if (rowQuestionDB.getAnswerDB().getOptionDBs().get(
                        1).getOptionAttributeDB().getDefaultOption() == 1) {
                    isChecked = true;
                }
                saveSwitchOption(rowQuestionDB, isChecked);
                break;
        }
    }

    private void showDefaultValue(TableRow tableRow, QuestionDB rowQuestionDB) {
        if (rowQuestionDB.getValueBySession() != null) {
            return;
        }
        switch (rowQuestionDB.getOutput()) {
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
                OptionDB selectedOptionDB = rowQuestionDB.getOptionBySession();
                if (selectedOptionDB == null) {
                    //the 0 option is the left option and is false in the switch, the 1 option is
                    // the right option and is true
                    boolean isChecked = false;
                    if (rowQuestionDB.getAnswerDB().getOptionDBs().get(
                            1).getOptionAttributeDB().getDefaultOption() == 1) {
                        isChecked = true;
                    }
                    saveSwitchOption(rowQuestionDB, isChecked);
                    switchView.setChecked(isChecked);
                    break;
                }
                switchView.setChecked(findSwitchBoolean(rowQuestionDB));
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
     * Checks if there are more questions to answer according to the given valueDB + current status.
     */
    public boolean isDone(ValueDB valueDB) {
        return !navigationController.hasNext(valueDB != null ? valueDB.getOptionDB() : null);
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

        QuestionDB currentQuestionDB;
        boolean isQuestionFound = false;

        //it is compared by uid because comparing by question it could be not equal by the same
        // question.
        while (!isQuestionFound) {

            currentQuestionDB = navigationController.getCurrentQuestion();

            int tabType = currentQuestionDB.getHeaderDB().getTabDB().getType();
            if (TabDB.isMultiQuestionTab(tabType)) {
                List<QuestionDB> screenQuestionDBs = currentQuestionDB.getQuestionsByTab(
                        currentQuestionDB.getHeaderDB().getTabDB());

                for (QuestionDB questionDB : screenQuestionDBs) {
                    if (questionUid.equals(questionDB.getUid())) {
                        isQuestionFound = true;
                    }
                }
            } else {
                if (questionUid.equals(currentQuestionDB.getUid())) {
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
        ValueDB valueDB = null;
        do {
            next();
            QuestionDB questionDB = navigationController.getCurrentQuestion();
            valueDB = questionDB.getValueBySession();
            skipReminder();
        } while (valueDB != null && !isDone(valueDB));
        notifyDataSetChanged();
    }

    private void skipReminder() {
        for (QuestionRelationDB relation : navigationController.getCurrentQuestion()
                .getQuestionRelationDBs()) {
            if (relation.isAReminder()) {
                next();
            }
        }
    }

    /**
     * Changes the current question moving forward
     */
    public void next() {
        QuestionDB questionDB = navigationController.getCurrentQuestion();

        ValueDB valueDB = questionDB.getValueBySession();

        if (isDone(valueDB)) {
            navigationController.isMovingToForward = false;
            return;
        }
        navigationController.next(valueDB != null ? valueDB.getOptionDB() : null);

        notifyDataSetChanged();
        hideKeyboard(PreferencesState.getInstance().getContext());

        questionDB = navigationController.getCurrentQuestion();

        if (valueDB != null && !readOnly
                && navigationController.getCurrentTotalPages() < questionDB.getTotalQuestions()) {
            navigationController.setTotalPages(questionDB.getTotalQuestions());
        }
        navigationController.isMovingToForward = false;
        isClicked = false;
    }

    private void saveSwitchOption(QuestionDB questionDB, boolean isChecked) {
        OptionDB selectedOptionDB = findSwitchOption(questionDB, isChecked);
        if (selectedOptionDB == null) {
            return;
        }
        questionDB.saveValuesDDL(selectedOptionDB, questionDB.getValueBySession());
        showOrHideChildren(questionDB);
    }
}