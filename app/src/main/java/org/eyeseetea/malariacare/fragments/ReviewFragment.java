package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.views.TextCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idelcano on 09/06/2016.
 */
public class ReviewFragment extends Fragment {

    public static final String TAG = ".ReviewFragment";

    LayoutInflater lInflater;

    public static ReviewFragment newInstance(int index) {
        ReviewFragment f = new ReviewFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        this.lInflater = LayoutInflater.from(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.review_layout,
                container, false);
        initValues(view);
        view.setOnTouchListener(new OnSwipeTouchListener(view.getContext()) {
            @Override
            public void onSwipeRight() {
                DashboardActivity.dashboardActivity.hideReview();
            }
        });
        return view;
    }

    /**
     * Inflate a TextCard inside a linearLayout for each value
     * @param view
     */
    private void initValues(View view) {
        Survey survey= Session.getSurvey();
        List<Value> values = survey.getValuesFromDB();
        LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.options_review_table);
        for(Value value:values) {
            drawValue(linearLayout, value);
        }
    }


    /**
     * Inflate the linearlayout to add the values
     * @param linearLayout
     * @param value
     */
    private void drawValue(LinearLayout linearLayout, Value value) {
        TextCard textCard=(TextCard) lInflater.inflate(R.layout.dynamic_review_row,linearLayout,false);
        textCard.setText(value.getValue());
        if(value.getQuestion()!=null) {
            if(value.getOption()!=null && value.getOption().getBackground_colour()!=null)
                textCard.setBackgroundColor(Color.parseColor("#" + value.getOption().getBackground_colour()));
            linearLayout.addView(textCard);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
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
