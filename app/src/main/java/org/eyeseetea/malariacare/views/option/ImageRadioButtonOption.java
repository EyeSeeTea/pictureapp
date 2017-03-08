package org.eyeseetea.malariacare.views.option;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class ImageRadioButtonOption extends LinearLayout {
    Option mOption;
    OnCheckedChangeListener mOnCheckedChangeListener;
    ImageView mImageView;
    RadioButton mRadioButton;
    CustomTextView mCounter;

    public ImageRadioButtonOption(Context context) {
        super(context);

        init(context);
    }

    public ImageRadioButtonOption(Context context, boolean bigRadioButton) {
        super(context);

        init(context, bigRadioButton);
    }

    public ImageRadioButtonOption(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public Option getOption() {
        return mOption;
    }

    public void setOption(Option option, Question question) {
        this.mOption = option;

        setCounter(question);
    }

    public void setImageDrawable(Drawable drawable) {
        if (drawable.getIntrinsicHeight() < drawable.getIntrinsicWidth()) {
            mImageView.getLayoutParams().height = (int) Math.round(
                    mImageView.getLayoutParams().height / 2);
        }

        mImageView.setImageDrawable(drawable);
    }

    public void setText(CharSequence text) {
        mRadioButton.setText(text);
    }

    public boolean isChecked() {
        return mRadioButton.isChecked();
    }

    public void setChecked(boolean checked) {
        mRadioButton.setChecked(checked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
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

        mCounter.setText(counterTextValue + counterValue);
        mCounter.setVisibility(View.VISIBLE);
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_image_radio_button_question_option, this);

        mImageView = (ImageView) findViewById(R.id.radio_image);
        mRadioButton = (RadioButton) findViewById(R.id.radio_button);
        mCounter = (CustomTextView) findViewById(R.id.counter1);

        mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notifyCheckedChange(b);
            }
        });
    }

    private void init(final Context context, boolean bigText) {
        if (bigText) {
            inflate(context, R.layout.dynamic_image_big_radio_button_question_option, this);
        } else {
            inflate(context, R.layout.dynamic_image_radio_button_question_option, this);
        }

        mImageView = (ImageView) findViewById(R.id.radio_image);
        mRadioButton = (RadioButton) findViewById(R.id.radio_button);
        mCounter = (CustomTextView) findViewById(R.id.counter1);

        mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notifyCheckedChange(b);
            }
        });
    }

    public void notifyCheckedChange(Boolean value) {
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, value);
        }
    }

    /**
     * Method to change the relation of weight between the radioButton and the image
     *
     * @param radioButtonWeight The weight of the radio button the max is 1f and the min is 0
     */
    public void changeRadioButtonWeight(float radioButtonWeight) {
        if (radioButtonWeight > 1) {
            radioButtonWeight = 1f;
        }
        if (radioButtonWeight < 0) {
            radioButtonWeight = 0f;
        }
        float imageWeight = 1f - radioButtonWeight;
        LayoutParams imageParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT,
                imageWeight);
        mImageView.setLayoutParams(imageParams);
        LayoutParams radioButtonParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT,
                radioButtonWeight);
        mRadioButton.setLayoutParams(radioButtonParams);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mRadioButton.setEnabled(enabled);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ImageRadioButtonOption imageRadioButton, boolean value);
    }
}
