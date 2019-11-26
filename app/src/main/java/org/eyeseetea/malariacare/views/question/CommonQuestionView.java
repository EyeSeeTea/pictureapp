package org.eyeseetea.malariacare.views.question;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.domain.exception.RegExpValidationException;
import org.eyeseetea.malariacare.utils.Utils;

public class CommonQuestionView extends LinearLayout {
    boolean isActive = true;

    private TableRow mTableRow;
    private ViewGroup mLayout;
    private boolean jumpingNextQuestionActive;

    public Question question;


    public CommonQuestionView(Context context) {
        super(context);
    }

    public boolean isJumpingNextQuestionActive() {
        return jumpingNextQuestionActive;
    }

    public void setJumpingNextQuestionActive(boolean jumpingNextQuestionActive) {
        this.jumpingNextQuestionActive = jumpingNextQuestionActive;
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

    protected void setActive(Boolean value) {
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

    public void initContainers(TableRow tableRow, ViewGroup layout) {
        mTableRow = tableRow;
        mLayout = layout;
    }

    public void focusNextQuestion() {
        if (jumpingNextQuestionActive) {
            View nextView = getNextView();
            if (nextView == null) {
                return;
            }

            while (nextView.getVisibility() == View.GONE &&
                    mLayout.indexOfChild(nextView) + 1 < mLayout.getChildCount()) {
                nextView = mLayout.getChildAt(
                        mLayout.indexOfChild(nextView) + 1);
            }

            if (nextView.getVisibility() != View.GONE) {
                IQuestionView nextQuestionView =
                        (IQuestionView) ((TableRow) nextView).getChildAt(
                                0);

                if (nextQuestionView.isEnabled()) {
                    if (thisAndNextQuestionAreAKeyboardQuestionView(nextQuestionView)) {
                        // use standard Android requestFocus only between keyboard questions
                        nextView.requestFocus();
                    } else {
                        ((IMultiQuestionView) nextQuestionView).requestAnswerFocus();
                    }
                } else {
                    Log.d(this.getClass().getSimpleName(),
                            "No jump because the nextQuestionView is disabled");
                }
            }
        }
    }

    private boolean thisAndNextQuestionAreAKeyboardQuestionView(IQuestionView nextQuestion) {
        return ((this instanceof AKeyboardQuestionView)
                && (nextQuestion instanceof AKeyboardQuestionView))
                || !(nextQuestion instanceof IMultiQuestionView);
    }

    public View getNextView() {
        return mLayout.getChildAt(mLayout.indexOfChild(mTableRow) + 1);
    }

    public IQuestionView getNextQuestionView() {
        View nextView = mLayout.getChildAt(mLayout.indexOfChild(mTableRow) + 1);
        if (nextView == null) {
            return null;
        }
        return (IQuestionView) ((ViewGroup) nextView).getChildAt(0);
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public boolean validateQuestionRegExp(TextView view) {
        try {
            if (question == null || question.getRegExp() == null
                    || question.getRegExp().isEmpty()) {
                return true;
            }
            question.match(view.getText().toString());
            return true;
        } catch (RegExpValidationException e) {
            e.printStackTrace();
            String errorMessage = Utils.getInternationalizedString(question.getRegExpError(),
                    getContext());
            Validation.getInstance().addinvalidInput(view,
                    errorMessage);
            view.setError(errorMessage);
            return false;
        }
    }
}
