package org.eyeseetea.malariacare.layout.listeners.question;

import android.view.View;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;

public class MultiQuestionAnswerChangedListener extends AQuestionAnswerChangedListener implements
        AKeyboardQuestionView.onAnswerChangedListener, AOptionQuestionView.onAnswerChangedListener {

    public MultiQuestionAnswerChangedListener(TableLayout tableLayout) {
        super(tableLayout);
    }

    @Override
    public void onAnswerChanged(View view, String newValue) {
        saveValue(view, newValue);
    }

    @Override
    public void onAnswerChanged(View view, Option option) {
        saveValue(view, option);
    }
}
