package org.eyeseetea.malariacare.layout.listeners;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idelcano on 14/06/2016.
 */
public class SwipeTouchListener implements View.OnTouchListener {
    public static ScrollView scrollView;
    /**
     * Custom gesture detector
     */
    private final GestureDetector gestureDetector;

    /**
     * List of clickable items inside the swipable view (buttons)
     */
    private final List<View> clickableViews;

    public SwipeTouchListener(Context ctx) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        clickableViews = new ArrayList<>();
    }

    @Override
    /**
     * Delegates any touch into the our custom gesture detector
     */
    public boolean onTouch(View v, MotionEvent event) {
        if (scrollView != null) {
            fixScrollEventY(event);
            scrollView.onTouchEvent(event);
        }
        return gestureDetector.onTouchEvent(event);
    }


    /**
     * Fix the position of the touch removing the space of the navigation layout
     */
    private void fixScrollEventY(MotionEvent event) {
        float x = event.getAxisValue(0);
        float y = event.getAxisValue(1);
        event.setLocation(x, y
                - PreferencesState.getInstance().getContext().getResources().getDimensionPixelSize(


                R.dimen.footer_navigation_size));
    }

    /**
     * Adds a clickable view
     */
    public void addClickableView(View view) {
        clickableViews.add(view);
    }

    /**
     * Adds a clickable view
     */
    public void addTouchableView(View view) {
        view.setOnTouchListener(this);
    }

    /**
     * Adds a clickable view
     */
    public void addScrollView(ScrollView view) {
        scrollView = view;
    }

    /**
     * Clears the list of clickable items
     */
    public void clearClickableViews() {
        clickableViews.clear();
    }

    /**
     * Calculates de clickable view that has been 'clicked' in the given event
     *
     * @return Returns de touched view or null otherwise
     */
    public View findViewByCoords(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        for (View v : clickableViews) {
            Rect visibleRectangle = new Rect();
            v.getGlobalVisibleRect(visibleRectangle);
            //Image/Button clicked
            if (x >= visibleRectangle.left && x <= visibleRectangle.right
                    && y >= visibleRectangle.top && y <= visibleRectangle.bottom) {
                return v;
            }
        }

        return null;
    }

    public void onClick(View view) {
        Log.e(".DynamicTabAdapter", "empty onclick");
    }

    public void onSwipeRight() {
        Log.e(".DynamicTabAdapter", "onSwipeRight(DEFAULT)");
    }

    public void onSwipeLeft() {
        Log.e(".DynamicTabAdapter", "onSwipeLeft(DEFAULT)");
    }

    /**
     * Our own custom gesture detector that distinguishes between onFling and a SingleTap
     */
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 50;

        private float lastX;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
//              Log.d(TAG, String.format("onSingleTapConfirmed: %f %f", event.getX(), event.getY
// ()));

            //Find the clicked button
            View clickedView = findViewByCoords(event);

            //If found
            if (clickedView != null) {
                //delegate onClick
                onClick(clickedView);
                return true;
            }

            //Not found, not consumed
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            lastX = e.getX();
//                Log.d(TAG, "onDown: "+lastX);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                int dx = (int) (e2.getX() - ((e1 == null) ? lastX : e1.getX()));

                // don't accept the fling if it's too short
                // as it may conflict with a button push
                if (Math.abs(dx) > SWIPE_THRESHOLD && Math.abs(velocityX) > Math.abs(velocityY)) {
                    if (velocityX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                    return true;
                } else {
                    return false;
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }
    }

}
