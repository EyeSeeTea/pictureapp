package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import org.eyeseetea.malariacare.views.question.multiquestion.RadioButtonMultiQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomRadioButton;

public class RadioButtonMultiquestionViewStrategy extends ARadioButtonMultiquestionViewStrategy {
    public RadioButtonMultiquestionViewStrategy(
            RadioButtonMultiQuestionView radioButtonMultiQuestionView) {
        super(radioButtonMultiQuestionView);
    }

    @Override
    public void fixRadioButtonWidth(CustomRadioButton radioButton) {
    }
}
