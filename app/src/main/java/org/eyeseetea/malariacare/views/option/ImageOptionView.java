package org.eyeseetea.malariacare.views.option;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.question.CommonQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ImageOptionView extends CommonQuestionView {
    public FrameLayout mOptionContainerView;
    public CustomTextView mOptionTextView;
    public ImageView mOptionImageView;
    public CustomTextView mOptionCounterTextView;
    Option mOption;
    OnOptionSelectedListener mOnOptionSelectedListener;
    private Boolean mSelectedOption;
    boolean isClicked = false;

    public ImageOptionView(Context context) {
        super(context);

        init(context);
    }

    public Option getOption() {
        return mOption;
    }

    public void setOption(Option option, Question question) {
        this.mOption = option;

        mOptionContainerView.setBackgroundColor(
                Color.parseColor("#" + option.getBackground_colour()));

        BaseLayoutUtils.putImageInImageView(option.getInternationalizedPath(),
                mOptionImageView);

        if (option.getOptionAttribute().hasHorizontalAlignment()
                && option.getOptionAttribute().hasVerticalAlignment()) {
            mOptionTextView.setText(Utils.getInternationalizedString(option.getName()));
            mOptionTextView.setGravity(option.getOptionAttribute().getGravity());
        } else {
            mOptionTextView.setVisibility(View.GONE);
        }

        setCounter(question);
    }

    public void setSelectedOption(boolean selected) {
        mSelectedOption = selected;

        if (mSelectedOption) {
            LayoutUtils.highlightSelection(mOptionContainerView, mOption);
        } else {
            LayoutUtils.overshadow(mOptionContainerView);
        }
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener onOptionSelectedListener) {
        mOnOptionSelectedListener = onOptionSelectedListener;
    }

    public void setCounter(Question question) {

        Question optionCounter = question.findCounterByOption(mOption);

        if (optionCounter == null) {
            return;
        }

        String counterValue = optionCounter.getQuestionValueBySession();
        if (counterValue == null || counterValue.isEmpty()) {
            return;
        }

        String counterTextValue = getContext().getResources().getString(R.string.option_counter);

        mOptionCounterTextView.setText(counterTextValue + counterValue);
        mOptionCounterTextView.setVisibility(View.VISIBLE);
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_image_question_option, this);

        mOptionContainerView = (FrameLayout) findViewById(R.id.imageOptionContainer);
        mOptionImageView = (ImageView) findViewById(R.id.optionImage);
        mOptionTextView = (CustomTextView) findViewById(R.id.optionText);
        mOptionCounterTextView = (CustomTextView) findViewById(R.id.optionCounterText);

        mOptionImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClicked) {
                    isClicked = true;
                    if (mOnOptionSelectedListener != null && isEnabled()) {
                        mSelectedOption = true;
                        mOnOptionSelectedListener.onOptionSelected(v, mOption);
                    }
                }
            }
        });
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(View view, Option option);
    }
}
