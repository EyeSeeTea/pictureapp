package org.eyeseetea.malariacare.views.option;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
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
    OptionDB mOptionDB;
    OnOptionSelectedListener mOnOptionSelectedListener;
    private Boolean mSelectedOption;
    boolean isClicked = false;

    public ImageOptionView(Context context) {
        super(context);

        init(context);
    }

    public OptionDB getOptionDB() {
        return mOptionDB;
    }

    public void setOption(OptionDB optionDB, Question question) {
        this.mOptionDB = optionDB;

        mOptionContainerView.setBackgroundColor(
                Color.parseColor("#" + optionDB.getBackground_colour()));

        BaseLayoutUtils.putImageInImageView(optionDB.getInternationalizedPath(),
                mOptionImageView);

        if (optionDB.getOptionAttributeDB().hasHorizontalAlignment()
                && optionDB.getOptionAttributeDB().hasVerticalAlignment()) {
            mOptionTextView.setText(Utils.getInternationalizedString(optionDB.getName()));
            mOptionTextView.setGravity(optionDB.getOptionAttributeDB().getGravity());
        } else {
            mOptionTextView.setVisibility(View.GONE);
        }

        setCounter(question);
    }

    public void setSelectedOption(boolean selected) {
        mSelectedOption = selected;

        if (mSelectedOption) {
            LayoutUtils.highlightSelection(mOptionContainerView, mOptionDB);
        } else {
            LayoutUtils.overshadow(mOptionContainerView);
        }
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener onOptionSelectedListener) {
        mOnOptionSelectedListener = onOptionSelectedListener;
    }

    public void setCounter(Question question) {

        Question optionCounter = question.findCounterByOption(mOptionDB);

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
                        mOnOptionSelectedListener.onOptionSelected(v, mOptionDB);
                    }
                }
            }
        });
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(View view, OptionDB optionDB);
    }
}
