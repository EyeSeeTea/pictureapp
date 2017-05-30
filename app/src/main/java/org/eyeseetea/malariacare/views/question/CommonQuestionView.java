package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CommonQuestionView extends LinearLayout implements IValidationQuestion {
    boolean isActive;

    public CommonQuestionView(Context context) {
        super(context);
    }

    public CommonQuestionView(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonQuestionView(Context context,
            @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void setActive(Boolean value) {
        isActive = value;
    }
}
