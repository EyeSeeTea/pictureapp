package org.eyeseetea.malariacare.layout.listeners.strategies;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

public abstract class ASwipeTouchListenerStrategy {
    public boolean onTouch(ScrollView scrollView, GestureDetector gestureDetector,
            MotionEvent event) {
        if (scrollView != null) {
            fixScrollEventY(event);
            scrollView.onTouchEvent(event);
        }
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * Fix the position of the touch removing the space of the navigation layout
     */
    protected void fixScrollEventY(MotionEvent event) {
        float x = event.getAxisValue(0);
        float y = event.getAxisValue(1);
        event.setLocation(x, y
                - PreferencesState.getInstance().getContext().getResources().getDimensionPixelSize(


                R.dimen.footer_navigation_size));
    }
}
