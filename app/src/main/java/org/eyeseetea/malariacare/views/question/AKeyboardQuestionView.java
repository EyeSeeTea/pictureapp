package org.eyeseetea.malariacare.views.question;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import org.eyeseetea.malariacare.views.question.multiquestion.DropdownMultiQuestionView;

public abstract class AKeyboardQuestionView extends CommonQuestionView {
    protected onAnswerChangedListener mOnAnswerChangedListener;

    public AKeyboardQuestionView(Context context) {
        super(context);
    }

    public void setOnAnswerChangedListener(onAnswerChangedListener onAnswerChangedListener) {
        mOnAnswerChangedListener = onAnswerChangedListener;
    }

    protected void notifyAnswerChanged(String newValue) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(this, newValue);
        }
    }

    public interface onAnswerChangedListener {
        void onAnswerChanged(View view, String newValue);
    }

    public abstract EditText getAnswerView();

    public static void setupNextFocus(IQuestionView questionView, final TableRow tableRow,
            final ViewGroup layout) {
        if (questionView instanceof AKeyboardQuestionView) {
            AKeyboardQuestionView.moveFocusToNext((AKeyboardQuestionView) questionView,
                    tableRow, layout);
        }
    }

    public static void moveFocusToNext(AKeyboardQuestionView questionView, final TableRow tableRow,
            final ViewGroup layout) {
        EditText editText = questionView.getAnswerView();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                View nextView = layout.getChildAt(layout.indexOfChild(tableRow) + 1);

                if (nextView == null) {
                    return false;
                }

                IQuestionView nextQuestionView = (IQuestionView) ((ViewGroup) nextView).getChildAt(
                        0);

                if (nextQuestionView instanceof AKeyboardQuestionView) {
                    return false;
                }

                if (actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_DONE) {
                    CommonQuestionView.hideKeyboard(textView.getContext(), textView);
                    textView.clearFocus();
                    if (nextQuestionView instanceof DropdownMultiQuestionView) {
                        ((DropdownMultiQuestionView) nextQuestionView).getSpinnerOptions
                                ().requestFocusFromTouch();
                    } else if (nextQuestionView instanceof IMultiQuestionView) {
                        ((IMultiQuestionView) nextQuestionView)
                                .requestAnswerFocus();
                    } else {
                        nextView.requestFocus();
                    }
                }
                return true;
            }
        });
    }
}
