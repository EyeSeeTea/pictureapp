package org.eyeseetea.malariacare.views.option;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;

public class ImageRadioButtonOption extends LinearLayout {
    Option option;

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ImageRadioButtonOption imageRadioButton, boolean value);
    }

    OnCheckedChangeListener mOnCheckedChangeListener;

    ImageView mImageView;
    RadioButton mRadioButton;

    public ImageRadioButtonOption(Context context) {
        super(context);

        init(context);
    }

    public ImageRadioButtonOption(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
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

    public void setOnCheckedChangeListener (OnCheckedChangeListener onCheckedChangeListener){
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_image_radio_button_question_option, this);

        mImageView = (ImageView) findViewById(R.id.radio_image);

        mRadioButton = (RadioButton) findViewById(R.id.radio_button);

        mRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notifyCheckedChange(b);
            }
        });
    }

    public void notifyCheckedChange(Boolean value){
        if (mOnCheckedChangeListener != null)
            mOnCheckedChangeListener.onCheckedChanged(this,value);
    }
}
