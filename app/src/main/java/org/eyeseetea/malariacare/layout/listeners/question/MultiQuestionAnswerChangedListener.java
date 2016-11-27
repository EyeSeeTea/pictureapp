package org.eyeseetea.malariacare.layout.listeners.question;

import android.view.View;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.malariacare.views.question.AQuestionView;

public class MultiQuestionAnswerChangedListener extends AQuestionAnswerChangedListener implements
        AQuestionView.onAnswerChangedListener {

    public MultiQuestionAnswerChangedListener(TableLayout tableLayout) {
        super(tableLayout);
    }

    @Override
    public void onAnswerChanged(View view, String newValue) {
        saveValue(view, newValue);
    }

}
