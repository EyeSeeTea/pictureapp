package org.eyeseetea.malariacare.layout.listeners;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import static org.eyeseetea.malariacare.fragments.ReviewFragment.TAG;

/**
 * Created by idelcano on 14/06/2016.
 */
public class SwipeTouchListener implements View.OnTouchListener  {
    public static ScrollView scrollView;
    /**
     * Custom gesture detector
     */
    private final GestureDetector gestureDetector;

    /**
     * List of clickable items inside the swipable view (buttons)
     */
    private final List<View> clickableViews;

    public SwipeTouchListener(Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        clickableViews =new ArrayList<>();
    }

    @Override
    /**
     * Delegates any touch into the our custom gesture detector
     */
    public boolean onTouch(View v, MotionEvent event) {
        if(scrollView!=null)
            scrollView.onTouchEvent(event);
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
     * Adds a clickable view
     * @param view
     */
    public void addScrollView(ScrollView view){
        scrollView=view;
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
