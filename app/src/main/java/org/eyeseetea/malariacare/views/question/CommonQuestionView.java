package org.eyeseetea.malariacare.views.question;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

    public void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager keyboard = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showKeyboard(Context context, View view) {
        view.requestFocus();
        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            keyboard.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /**
     * hide keyboard using a keyboardView variable view
     */
    protected void hideKeyboard(View view) {
        InputMethodManager keyboard = (InputMethodManager) getContext().getSystemService(
                INPUT_METHOD_SERVICE);
        if (view != null) {
            keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager keyboard = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (view != null) {
            keyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
