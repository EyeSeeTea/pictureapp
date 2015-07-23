/*
 * Copyright (c) 2015.
 *
 * This file is part of Facility QA Tool App.
 *
 *  Facility QA Tool App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Facility QA Tool App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.layout.adapters.survey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.primitives.Booleans;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.progress.ProgressTabStatus;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.EditCard;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.UncheckeableRadioButton;
import org.eyeseetea.malariacare.views.filters.MinMaxInputFilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Jose on 21/04/2015.
 */
public class DynamicTabAdapter extends BaseAdapter implements ITabAdapter {

    private final static String TAG=".DynamicTabAdapter";

    /**
     * This adapter is special, despite of there might be N questions there is only 1 on screen but some header and footer are required.
     * So getCount() will get 3 = header + currentQuestion + footerProgress
     */
    private final static int NUM_LIST_VISIBLE_ELEMENTS=3;

    /**
     * Hold the progress of completion
     */
    private ProgressTabStatus progressTabStatus;

    /**
     * This type of tab has only one header
     */
    private Header header;



    //List of Headers and Questions. Each position contains an object to be showed in the listview
    List<Object> items;
    Tab tab;

    LayoutInflater lInflater;

    private final Context context;

    // The length of this arrays is the same that the items list. Each position indicates if the item
    // on this position is hidden (true) or visible (false)

    private final LinkedHashMap<Object, Boolean> elementInvisibility = new LinkedHashMap<>();

    int id_layout;

    /**
     * Flag that indicates if the current survey in session is already sent or not (it affects readonly settings)
     */
    private boolean readOnly;

    //Store the Views references for each row (to avoid many calls to getViewById)
    static class ViewHolder {
        //Label
        public TextCard statement;

        // Main component in the row: Spinner, EditText or RadioGroup
        public View component;

        public int type;
    }

    public DynamicTabAdapter(Tab tab, Context context) {
        this.lInflater = LayoutInflater.from(context);
        this.context = context;
        this.id_layout = R.layout.form_without_score;

        this.items=initItems(tab);
        List<Question> questions= initHeaderAndQuestions();
        this.progressTabStatus=initProgress(questions);
        this.readOnly = Session.getSurvey().isSent();
    }

    /**
     * Turns a tab into an ordered list of headers+questions
     * @param tab
     */
    private List<Object> initItems(Tab tab){
        this.tab=tab;
        return Utils.convertTabToArray(tab);
    }

    /**
     * Initializes the clean list of questions (without headers)
     */
    private List<Question> initHeaderAndQuestions() {
        List<Question> questions=new ArrayList<Question>();

        this.header=(Header)this.items.get(0);
        for(int i=1;i<this.items.size();i++){
            questions.add((Question)this.items.get(i));
        }

        return questions;
    }

    /**
     * Builds a progress status based on the current list of questions
     * @param questions
     * @return
     */
    private ProgressTabStatus initProgress(List<Question> questions){
        return new ProgressTabStatus(questions);
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
        return this.progressTabStatus.getCurrentQuestion();
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Inflate the layout
        View rowView = lInflater.inflate(R.layout.dynamic_tab_grid_question, parent, false);

        Question question=this.progressTabStatus.getCurrentQuestion();

        //Header
        TextCard headerView=(TextCard) rowView.findViewById(R.id.question);
        headerView.setText(question.getForm_name());

        //Progress
        ProgressBar progressView=(ProgressBar)rowView.findViewById(R.id.dynamic_progress);
        progressView.setMax(progressTabStatus.getTotalPages());
        progressView.setProgress(progressTabStatus.getCurrentPage()+1);
        TextView progressText=(TextView)rowView.findViewById(R.id.dynamic_progress_text);
        progressText.setText(progressTabStatus.getStatusAsString());

//        ViewHolder viewHolder = new ViewHolder();
//        if (item instanceof Question) {
//            question = (Question) item;
//
//            //FIXME This should be moved into its own class (Ex: ViewHolderFactory.getView(item))
//            switch (question.getAnswer().getOutput()) {
//
//                case Constants.LONG_TEXT:
//                    rowView = initialiseView(R.layout.longtext, parent, question, viewHolder, position);
//
//                    //Add main component and listener
//                    ((EditCard) viewHolder.component).addTextChangedListener(new TextViewListener(false, question));
//                    break;
//                case Constants.NO_ANSWER:
//                    rowView = initialiseView(R.layout.label, parent, question, viewHolder, position);
//                    break;
//                case Constants.POSITIVE_INT:
//                    rowView = initialiseView(R.layout.integer, parent, question, viewHolder, position);
//
//                    //Add main component, set filters and listener
//                    ((EditCard) viewHolder.component).setFilters(new InputFilter[]{
//                            new InputFilter.LengthFilter(Constants.MAX_INT_CHARS),
//                            new MinMaxInputFilter(1, null)
//                    });
//                    ((EditCard) viewHolder.component).addTextChangedListener(new TextViewListener(false, question));
//                    break;
//                case Constants.INT:
//                case Constants.PHONE:
//                    rowView = initialiseView(R.layout.integer, parent, question, viewHolder, position);
//
//                    //Add main component, set filters and listener
//                    ((EditCard) viewHolder.component).setFilters(new InputFilter[]{
//                            new InputFilter.LengthFilter(Constants.MAX_INT_CHARS)
//                    });
//                    ((EditCard) viewHolder.component).addTextChangedListener(new TextViewListener(false, question));
//                    break;
//                case Constants.DATE:
//                    rowView = initialiseView(R.layout.date, parent, question, viewHolder, position);
//
//                    //Add main component and listener
//                    ((EditCard) viewHolder.component).addTextChangedListener(new TextViewListener(false, question));
//                    break;
//
//                case Constants.SHORT_TEXT:
//                    rowView = initialiseView(R.layout.shorttext, parent, question, viewHolder, position);
//
//                    //Add main component and listener
//                    ((EditCard) viewHolder.component).addTextChangedListener(new TextViewListener(false, question));
//                    break;
//
//                case Constants.DROPDOWN_LIST:
//                case Constants.IMAGES_2:
//                case Constants.IMAGES_4:
//                case Constants.IMAGES_6:
//                    rowView = initialiseView(R.layout.ddl, parent, question, viewHolder, position);
//
//                    initialiseScorableComponent(rowView, viewHolder);
//
//                    // In case the option is selected, we will need to show num/dems
//                    List<Option> optionList = question.getAnswer().getOptions();
//                    optionList.add(0, new Option(Constants.DEFAULT_SELECT_OPTION));
//                    Spinner spinner = (Spinner) viewHolder.component;
//                    spinner.setAdapter(new OptionArrayAdapter(context, optionList));
//
////                    //Add Listener
////                    if (!question.hasRelatives())
////                        ((Spinner) viewHolder.component).setOnItemSelectedListener(new SpinnerListener(false, question, viewHolder));
////                    else
////                        autoFillAnswer(viewHolder, question);
//                    break;
//                case Constants.RADIO_GROUP_HORIZONTAL:
//                    rowView = initialiseView(R.layout.radio, parent, question, viewHolder, position);
//
//                    initialiseScorableComponent(rowView, viewHolder);
//
//                    createRadioGroupComponent(question, viewHolder, LinearLayout.HORIZONTAL);
//                    break;
//                case Constants.RADIO_GROUP_VERTICAL:
//                    rowView = initialiseView(R.layout.radio, parent, question, viewHolder, position);
//
//                    initialiseScorableComponent(rowView, viewHolder);
//
//                    createRadioGroupComponent(question, viewHolder, LinearLayout.VERTICAL);
//                    break;
//
//                default:
//                    break;
//            }
//
//            //Put current value in the component
//            setValues(viewHolder, question);
//            //Disables component if survey has already been sent
//            updateReadOnly(viewHolder.component);
//        }

        return rowView;
    }


    /**
     * Enables/Disables input view according to the state of the survey.
     * Sent surveys cannot be modified.
     *
     * @param view
     */
    private void updateReadOnly(View view) {
        if (view == null) {
            return;
        }

        if (view instanceof RadioGroup) {
            RadioGroup radioGroup = (RadioGroup) view;
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                radioGroup.getChildAt(i).setEnabled(!readOnly);
            }
        } else {
            view.setEnabled(!readOnly);
        }
    }

    /**
     * Show a final dialog to announce the survey is over
     */
    private void showDone(){
        final Activity activity=(Activity)context;
        new AlertDialog.Builder((activity))
                .setTitle(R.string.survey_title_exit)
                .setMessage(R.string.survey_info_exit)
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        activity.finish();
                    }
                }).create().show();
    }

    /**
     * Checks if there are more questions to answer according to the given value + current status.
     * @param value
     * @return
     */
    private boolean isDone(Value value){
        //First question  + NO => true
        if(progressTabStatus.isFirstQuestion() && !value.isAYes()){
            return true;
        }

        //No more questions => true
        return !progressTabStatus.hasNextQuestion();
    }

    /**
     * Changes the current question moving forward
     */
    private void next(){
        if(!progressTabStatus.hasNextQuestion()){
            return;
        }

        progressTabStatus.getNextQuestion();
        notifyDataSetChanged();
    }

    /**
     * Changes the current question moving backward
     */
    private void previous(){
        if(!progressTabStatus.hasPreviousQuestion()){
            return;
        }

        progressTabStatus.getPreviousQuestion();
        notifyDataSetChanged();
    }


    public void setValues(ViewHolder viewHolder, Question question) {

        switch (question.getAnswer().getOutput()) {
            case Constants.DATE:
            case Constants.SHORT_TEXT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.POSITIVE_INT:
            case Constants.PHONE:
                ((EditCard) viewHolder.component).setText(ReadWriteDB.readValueQuestion(question));
                break;
            case Constants.DROPDOWN_LIST:
            case Constants.IMAGES_2:
            case Constants.IMAGES_4:
            case Constants.IMAGES_6:
                ((Spinner) viewHolder.component).setSelection(ReadWriteDB.readPositionOption(question));
                break;
            case Constants.RADIO_GROUP_HORIZONTAL:
            case Constants.RADIO_GROUP_VERTICAL:
                Value value = question.getValueBySession();
                if (value != null) {
                    ((UncheckeableRadioButton) viewHolder.component.findViewWithTag(value.getOption())).setChecked(true);
                }
                break;
            default:
                break;
        }
    }


    /**
     * Do the logic after a DDL option change
     * @param viewHolder private class that acts like a cache to quickly access the different views
     * @param question the question that changes his value
     * @param option the option that has been selected
     */
    private void itemSelected(ViewHolder viewHolder, Question question, Option option) {
        // Write option to DB
        ReadWriteDB.saveValuesDDL(question, option);
    }

    private View initialiseView(int resource, ViewGroup parent, Question question, ViewHolder viewHolder, int position) {
        View rowView = lInflater.inflate(resource, parent, false);
        if (question.hasChildren())
            rowView.setBackgroundResource(R.drawable.background_parent);
        else
            rowView.setBackgroundResource(LayoutUtils.calculateBackgrounds(position));

        viewHolder.component = rowView.findViewById(R.id.answer);
        viewHolder.statement = (TextCard) rowView.findViewById(R.id.statement);
        viewHolder.statement.setText(question.getForm_name());

        return rowView;
    }

    private void initialiseScorableComponent(View rowView, ViewHolder viewHolder) {
        configureViewByPreference(viewHolder);
    }

    private void createRadioGroupComponent(Question question, ViewHolder viewHolder, int orientation) {
        ((RadioGroup) viewHolder.component).setOrientation(orientation);

        for (Option option : question.getAnswer().getOptions()) {
            UncheckeableRadioButton button = (UncheckeableRadioButton) lInflater.inflate(R.layout.uncheckeable_radiobutton, null);
            button.setOption(option);
            button.updateProperties(PreferencesState.getInstance().getScale(), this.context.getString(R.string.font_size_level1), this.context.getString(R.string.medium_font_name));
            ((RadioGroup) viewHolder.component).addView(button);
        }

        //Add Listener
        ((RadioGroup) viewHolder.component).setOnCheckedChangeListener(new RadioGroupListener(question, viewHolder));
    }

    /**
     * Set visibility of numerators and denominators depending on the user preference selected in the settings activity
     *
     * @param viewHolder view that holds the component to be more efficient
     */
    private void configureViewByPreference(ViewHolder viewHolder) {
        float statementWeight = 0.65f;
        float componentWeight = 0.35f;

        if (PreferencesState.getInstance().isShowNumDen()) {
            statementWeight = 0.45f;
            componentWeight = 0.25f;
        }

        ((RelativeLayout) viewHolder.statement.getParent()).setLayoutParams(new LinearLayout.LayoutParams(0, RelativeLayout.LayoutParams.WRAP_CONTENT, statementWeight));
        ((RelativeLayout) viewHolder.component.getParent().getParent()).setLayoutParams(new LinearLayout.LayoutParams(0, RelativeLayout.LayoutParams.WRAP_CONTENT, componentWeight));
    }

    //////////////////////////////////////
    /////////// LISTENERS ////////////////
    //////////////////////////////////////
    private class TextViewListener implements TextWatcher {
        private boolean viewCreated;
        private Question question;

        public TextViewListener(boolean viewCreated, Question question) {
            this.viewCreated = viewCreated;
            this.question = question;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (viewCreated) {
                ReadWriteDB.saveValuesText(question, s.toString());
            } else {
                viewCreated = true;
            }
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {

        private boolean viewCreated;
        private ViewHolder viewHolder;
        private Question question;

        public SpinnerListener(boolean viewCreated, Question question, ViewHolder viewHolder) {
            this.viewCreated = viewCreated;
            this.question = question;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (viewCreated) {
                itemSelected(viewHolder, question, (Option) ((Spinner) viewHolder.component).getItemAtPosition(pos));
            } else {
                viewCreated = true;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        private ViewHolder viewHolder;
        private Question question;

        public RadioGroupListener(Question question, ViewHolder viewHolder) {
            this.question = question;
            this.viewHolder = viewHolder;
        }


        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(!group.isShown()){
                return;
            }

            Option option = new Option(Constants.DEFAULT_SELECT_OPTION);
            if (checkedId != -1) {
                UncheckeableRadioButton uncheckeableRadioButton = (UncheckeableRadioButton) (this.viewHolder.component).findViewById(checkedId);
                option = (Option) uncheckeableRadioButton.getTag();
            }
            itemSelected(viewHolder, question, option);
        }
    }

}