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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SurveyActivity;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.survey.progress.ProgressTabStatus;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.TextCard;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    /**
     * Listener that detects taps on buttons & swipe
     */
    private OnSwipeTouchListener swipeTouchListener;

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

        swipeTouchListener=new OnSwipeTouchListener(context) {
            /**
             * Click listener for image option
             * @param view
             */
            public void onClick(View view) {
                Log.d(TAG, "onClick");
                Option selectedOption=(Option)view.getTag();
                Question question=progressTabStatus.getCurrentQuestion();
                ReadWriteDB.saveValuesDDL(question, selectedOption);
                finishOrNext();
            }

            /**
             * Swipe right listener moves to previous question
             */
            public void onSwipeRight() {
                Log.d(TAG,"onSwipeRight(previous)");
                previous();
            }

            /**
             * Swipe left listener moves to next question
             */
            public void onSwipeLeft() {
                Log.d(TAG,"onSwipeLeft(next)");
                if(readOnly || progressTabStatus.isNextAllowed()) {
                    next();
                }
            }
        };

        listView.setOnTouchListener(swipeTouchListener);
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
                swipeTouchListener.clearClickableViews();
                for(int i=0;i<options.size();i++){
                    int mod=i%2;
                    //First item per row requires a new row
                    if(mod==0){
                        tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_row,tableLayout,false);
                        tableLayout.addView(tableRow);
                    }
                    ImageView imageButton = (ImageView) tableRow.getChildAt(mod);
                    if(mod==0) {
                        imageButton.setBackgroundColor(Color.parseColor("#F0D100"));
                    }
                    else{
                        imageButton.setBackgroundColor(Color.parseColor("#00FF65"));
                    }
                    initOptionButton(imageButton, options.get(i), value);
                }

                break;
            case Constants.PHONE:
                tableRow=(TableRow)lInflater.inflate(R.layout.dynamic_tab_phone_row, tableLayout, false);
                tableLayout.addView(tableRow);
                initPhoneValue(tableRow, value);
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

                    // Check phone number format
                    Phonenumber.PhoneNumber phoneNumber = null;
                    try {
                        Locale locale = context.getResources().getConfiguration().locale;
                        phoneNumber = PhoneNumberUtil.getInstance().parse(phoneValue, locale.getCountry());
                    } catch (NumberParseException e) {
                        editText.setError(context.getString(R.string.dynamic_error_phone_format));
                        return;
                    }
                    if(!PhoneNumberUtil.getInstance().isValidNumber(phoneNumber)){
                        editText.setError(context.getString(R.string.dynamic_error_phone_format));
                        return;
                    }

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
    private void initOptionButton(ImageView button, Option option, Value value){
        //Highlight button
        if(value!=null && value.getValue().equals(option.getName())){
            Drawable selectedBackground=context.getResources().getDrawable(R.drawable.background_dynamic_clicked_option);
            if(android.os.Build.VERSION.SDK_INT> Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
                //button.setBackground(selectedBackground);
                //button.setBackgroundColor(Color.parseColor("#80ff5a595b"));
//                button.setImageAlpha(128);
                button.getBackground().setColorFilter(Color.parseColor("#805a595b"), PorterDuff.Mode.SRC_ATOP);
                button.setColorFilter(Color.parseColor("#805a595b"));
            }else {
//                button.setAlpha(128);
                //button.setBackgroundDrawable(selectedBackground);
                button.getBackground().setColorFilter(Color.parseColor("#805a595b"), PorterDuff.Mode.SRC_ATOP);
                button.setColorFilter(Color.parseColor("#805a595b"));
            }


        }
        else if (value != null){
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

        //Add button to listener
        swipeTouchListener.addClickableView(button);

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

    public class OnSwipeTouchListener implements View.OnTouchListener {

        /**
         * Custom gesture detector
         */
        private final GestureDetector gestureDetector;
        /**
         * List of clickable items inside the swipable view (buttons)
         */
        private final List<View> clickableViews;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            clickableViews =new ArrayList<>();
        }

        @Override
        /**
         * Delegates any touch into the our custom gesture detector
         */
        public boolean onTouch(View v, MotionEvent event) {
//            Log.d(TAG, "onTouch: " + v.toString() + "\t (" + this.toString() + ")");
            return gestureDetector.onTouchEvent(event);
        }

        /**
         * Adds a clickable view
         * @param view
         */
        public void addClickableView(View view){
            clickableViews.add(view);
        }

        /**
         * Clears the list of clickable items
         */
        public void clearClickableViews(){
            clickableViews.clear();
        }

        /**
         * Calculates de clickable view that has been 'clicked' in the given event
         * @param event
         * @return Returns de touched view or null otherwise
         */
        public View findViewByCoords(MotionEvent event){
            float x=event.getRawX();
            float y=event.getRawY();
            for(View v: clickableViews){
                Rect visibleRectangle = new Rect();
                v.getGlobalVisibleRect(visibleRectangle);
                //Image/Button clicked
//                Log.d(TAG,String.format("findViewByCoords(%d,%d,%d,%d)",visibleRectangle.left,visibleRectangle.top,visibleRectangle.right, visibleRectangle.bottom));
                if(x>=visibleRectangle.left && x<=visibleRectangle.right && y>=visibleRectangle.top && y<=visibleRectangle.bottom){
                    return v;
                }
            }

            return null;
        }

        public void onClick(View view){
//            Log.e(".DynamicTabAdapter", "empty onclick");
        }

        public void onSwipeRight(){
//            Log.e(TAG, "onSwipeRight(DEFAULT)");
        }

        public void onSwipeLeft(){
//            Log.e(TAG, "onSwipeLeft(DEFAULT)");
        }

        /**
         * Our own custom gesture detector that distinguishes between onFling and a SingleTap
         */
        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 50;
            private static final int SWIPE_VELOCITY_THRESHOLD = 50;

            private float lastX;

            @Override
            public boolean onSingleTapConfirmed(MotionEvent event){
//              Log.d(TAG, String.format("onSingleTapConfirmed: %f %f", event.getX(), event.getY()));

                //Find the clicked button
                View clickedView=findViewByCoords(event);

                //If found
                if(clickedView!=null) {
                    //delegate onClick
                    onClick(clickedView);
                    return true;
                }
                
                //Not found, not consumed
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                lastX=e.getX();
//                Log.d(TAG, "onDown: "+lastX);
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                try {
                    float diffX = e2.getX()-((e1==null)?lastX:e1.getX());
//                    Log.d(TAG, String.format("onFling (%f): diffX: %f, velocityX: %f",lastX, diffX, velocityX));
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    return true;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return false;
            }
        }

    }

}