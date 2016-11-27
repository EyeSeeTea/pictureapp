package org.eyeseetea.malariacare.layout.listeners.question;

import android.view.View;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.malariacare.views.question.AQuestionView;

public class SingleQuestionAnswerChangedListener extends AQuestionAnswerChangedListener implements
        AQuestionView.onAnswerChangedListener {

    private DynamicTabAdapter mDynamicTabAdapter;

    public SingleQuestionAnswerChangedListener(TableLayout tableLayout,
            DynamicTabAdapter dynamicTabAdapter) {
        super(tableLayout);

        this.mDynamicTabAdapter = dynamicTabAdapter;
    }

    @Override
    public void onAnswerChanged(View view, String newValue) {
        mDynamicTabAdapter.navigationController.isMovingToForward = true;
        saveValue(view, newValue);
        mDynamicTabAdapter.finishOrNext();
    }
}
