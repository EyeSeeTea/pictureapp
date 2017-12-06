package org.eyeseetea.malariacare.layout.listeners.strategies;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class SwipeTouchListenerStrategy extends ASwipeTouchListenerStrategy {

    @Override
    public boolean onTouch(ScrollView scrollView, GestureDetector gestureDetector,
            MotionEvent event) {
        super.onTouch(scrollView, gestureDetector, event);
        return false;
    }
}
