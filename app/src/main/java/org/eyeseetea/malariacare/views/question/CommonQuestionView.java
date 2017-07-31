package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;

public class CommonQuestionView extends LinearLayout {
    boolean isActive = true;

    public CommonQuestionView(Context context) {
        super(context);
    }

    public boolean isActive() {
        return isActive;
    }

    public void activateQuestion() {
        setActive(true);
        Object inputView = this.findViewById(R.id.answer);
        if (inputView != null) {
            Validation.getInstance().addInput(inputView);
        }
    }

    public void deactivateQuestion() {
        setActive(false);
        Object inputView = this.findViewById(R.id.answer);
        if (inputView != null) {
            Validation.getInstance().removeInputError(inputView);
        }
    }

    private void setActive(Boolean value) {
        isActive = value;
    }
}
