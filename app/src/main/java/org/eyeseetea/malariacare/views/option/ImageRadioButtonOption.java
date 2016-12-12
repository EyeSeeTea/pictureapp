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
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.views.TextCard;

public class ImageRadioButtonOption extends LinearLayout {
    Option mOption;
    OnCheckedChangeListener mOnCheckedChangeListener;
    ImageView mImageView;
    RadioButton mRadioButton;
    TextCard mCounter;

    public ImageRadioButtonOption(Context context) {
        super(context);

        init(context);
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

        String counterValue = ReadWriteDB.readValueQuestion(optionCounter);
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
        mCounter = (TextCard) findViewById(R.id.counter1);

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

    @Override
    public void setEnabled(boolean enabled) {
        mRadioButton.setEnabled(enabled);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ImageRadioButtonOption imageRadioButton, boolean value);
    }
}
