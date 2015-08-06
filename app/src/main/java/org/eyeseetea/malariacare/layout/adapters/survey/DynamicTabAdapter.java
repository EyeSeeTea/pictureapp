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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SurveyActivity;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.survey.progress.ProgressTabStatus;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.EditCard;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.UncheckeableRadioButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 21/04/2015.
 */
public class DynamicTabAdapter extends BaseAdapter implements ITabAdapter {

    private final static String TAG=".DynamicTabAdapter";

    /**
     * Hold the progress of completion
     */
    public ProgressTabStatus progressTabStatus;

    /**
     * Flag that indicates if the swipe listener has been already added to the listview container
     */
    private boolean isSwipeAdded;

    //List of Headers and Questions. Each position contains an object to be showed in the listview
    List<Object> items;
    Tab tab;

    LayoutInflater lInflater;

    private final Context context;

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
        this.readOnly = !Session.getSurvey().isInProgress();
        this.isSwipeAdded=false;
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

    public void addOnSwipeListener(ListView listView){
        if(isSwipeAdded){
            return;
        }

        listView.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeRight() {
                Log.d(TAG,"onSwipeRight(previous)");
                previous();
            }

            public void onSwipeLeft() {
                Log.d(TAG,"onSwipeLeft(next)");
                if(readOnly || progressTabStatus.isNextAllowed()) {
                    next();
                }
            }
        });
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
        rowView.getLayoutParams().height=parent.getHeight();
        rowView.requestLayout();

        Question question=this.progressTabStatus.getCurrentQuestion();
        Value value=question.getValueBySession();

        //Question
        TextCard headerView=(TextCard) rowView.findViewById(R.id.question);
        headerView.setText(question.getForm_name());

        //Progress
        ProgressBar progressView=(ProgressBar)rowView.findViewById(R.id.dynamic_progress);
        progressView.setMax(progressTabStatus.getTotalPages());
        progressView.setProgress(progressTabStatus.getCurrentPage()+1);
        TextView progressText=(TextView)rowView.findViewById(R.id.dynamic_progress_text);
        progressText.setText(progressTabStatus.getStatusAsString());

        //Options
        TableLayout tableLayout=(TableLayout)rowView.findViewById(R.id.options_table);


        TableRow tableRow=null;
        int typeQuestion=question.getAnswer().getOutput();
        switch (typeQuestion){
            case Constants.IMAGES_2:
            case Constants.IMAGES_4:
            case Constants.IMAGES_6:
                List<Option> options = question.getAnswer().getOptions();
                for(int i=0;i<options.size();i++){
                    int mod=i%2;
                    //First item per row requires a new row
                    if(mod==0){
                        tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_row,tableLayout,false);
                        tableLayout.addView(tableRow);
                    }
                    ImageButton button = (ImageButton) tableRow.getChildAt(mod);

                    initOptionButton(button, options.get(i), value);
                }

                break;
            case Constants.PHONE:
                tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_phone_row, tableLayout, false);
                tableLayout.addView(tableRow);
                initPhoneValue(tableRow,value);
                break;
        }
        rowView.requestLayout();
        return rowView;
    }

    /**
     * Inits editText and button to view/edit the phone number
     * @param tableRow
     * @param value
     */
    private void initPhoneValue(TableRow tableRow, Value value){
        Button button=(Button)tableRow.findViewById(R.id.dynamic_phone_btn);
        EditText editText=(EditText)tableRow.findViewById(R.id.dynamic_phone_edit);

        //Has value? show it
        if(value!=null){
            editText.setText(value.getValue());
        }

        //Editable? add listener
        if(!readOnly){
            editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View parentView = (View) v.getParent();
                    EditText editText = (EditText) parentView.findViewById(R.id.dynamic_phone_edit);
                    String phoneValue = editText.getText().toString();

                    //Required, empty values rejected
                    if (phoneValue == null || "".equals(phoneValue)) {
                        editText.setError(context.getString(R.string.dynamic_error_phone_format));
                        return;
                    }

                    //TODO Use https://github.com/googlei18n/libphonenumber to check format
                    //TODO Take current region : getResources().getConfiguration().locale
//                    PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(phoneValue, regionCode);
//                    if(!phoneUtil.isValidNumber(phoneNumber)){
//                        editText.setError(context.getString(R.string.dynamic_error_phone_format));
//                        return;
//                    }


                    Question question = progressTabStatus.getCurrentQuestion();
                    ReadWriteDB.saveValuesText(question, phoneValue);
                    finishOrNext();
                }
            });
        }else{
            editText.setEnabled(false);
            button.setEnabled(false);
        }
    }

    /**
     * Attach an option with its button in view, adding the listener
     * @param button
     * @param option
     */
    private void initOptionButton(ImageButton button, Option option, Value value){
        //Highlight button
        if(value!=null && value.getValue().equals(option.getName())){
            Drawable selectedBackground=context.getResources().getDrawable(R.drawable.background_dynamic_clicked_option);
            if(android.os.Build.VERSION.SDK_INT> Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
                button.setBackground(selectedBackground);
            }else {
                button.setBackgroundDrawable(selectedBackground);
            }
        }
        //Put image
        try {
            InputStream inputStream = context.getAssets().open(option.getPath());
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            button.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Associate option
        button.setTag(option);

        //Readonly (not clickable, enabled)
        if(readOnly){
            button.setEnabled(false);
            return;
        }

        //Add listener
//        button.setOnClickListener(new View.OnClickListener() {
//              @Override
//              public void onClick(View v) {
//                  Log.d(TAG, "onClick");
//                  Option selectedOption=(Option)v.getTag();
//                  Question question=progressTabStatus.getCurrentQuestion();
//                  ReadWriteDB.saveValuesDDL(question, selectedOption);
//                  finishOrNext();
//              }
//            }
//        );
        button.setOnTouchListener(new OnSwipeTouchListener(button,context) {

            public void onClick(View view){
                Log.d(TAG, "onClick");
                Option selectedOption=(Option)view.getTag();
                Question question=progressTabStatus.getCurrentQuestion();
                ReadWriteDB.saveValuesDDL(question, selectedOption);
                finishOrNext();
            }

            public void onSwipeRight() {
                previous();
            }

            public void onSwipeLeft() {
                if (readOnly || progressTabStatus.isNextAllowed()) {
                    next();
                }
            }
        });
    }

    /**
     * Advance to the next question or finish survey according to question  and value.
     */
    private void finishOrNext(){
        Question question=progressTabStatus.getCurrentQuestion();
        Value value = question.getValueBySession();
        if(isDone(value)){
            showDone();
            return;
        }
        next();
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
                .setTitle(R.string.survey_title_completed)
                .setMessage(R.string.survey_info_completed)
                .setNeutralButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        ((SurveyActivity)activity).finishAndGo(DashboardActivity.class);
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

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        private final View view;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            view=null;
        }

        public OnSwipeTouchListener (View v, Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            view=v;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 50;
            private static final int SWIPE_VELOCITY_THRESHOLD = 50;

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event){
                Log.d(TAG, "onSingleTapConfirmed");
                onClick(view);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "onDown: "+e.getX());
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffX = e2.getX() - safeGetX(e1);
                    Log.d(TAG, "onFling: diffX: "+diffX);
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        /**
         * Sometimes event1 comes as NULL, this is a workaround to solve this
         * @param event
         * @return
         */
        private float safeGetX(MotionEvent event){
            if(event==null){
                WindowManager windowManager = (WindowManager) context
                        .getSystemService(Context.WINDOW_SERVICE);
                Display display = windowManager.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                return (float)size.x;
            }
            return event.getX();
        }

        public void onClick(View view){}

        public void onSwipeRight(){}

        public void onSwipeLeft(){}
    }

}