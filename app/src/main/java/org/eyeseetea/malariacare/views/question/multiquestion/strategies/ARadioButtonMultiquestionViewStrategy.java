package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.graphics.drawable.Drawable;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.malariacare.views.question.multiquestion.RadioButtonMultiQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomRadioButton;

public abstract class ARadioButtonMultiquestionViewStrategy {

    RadioButtonMultiQuestionView mRadioButtonMultiQuestionView;

    public ARadioButtonMultiquestionViewStrategy(
            RadioButtonMultiQuestionView radioButtonMultiQuestionView) {
        mRadioButtonMultiQuestionView = radioButtonMultiQuestionView;
    }

    public void fixRadioButtonWidth(CustomRadioButton radioButton) {
        Drawable radioButtonIcon = mRadioButtonMultiQuestionView.getResources().getDrawable(
                R.drawable.radio_on);
        BaseLayoutUtils.setLayoutParamsAs50Percent(radioButton,
                mRadioButtonMultiQuestionView.getContext(),
                calculateFixedWidth(radioButtonIcon));
    }

    private int calculateFixedWidth(Drawable radioButtonIcon) {
        int width = radioButtonIcon.getIntrinsicWidth();
        int height = radioButtonIcon.getIntrinsicHeight();
        int fixedHeight =
                PreferencesState.getInstance().getContext().getResources()
                        .getDimensionPixelSize(
                                R.dimen.image_size);
        int newHeightPercent = (fixedHeight * 100) / height;
        return (newHeightPercent * width) / 100;
    }
}
