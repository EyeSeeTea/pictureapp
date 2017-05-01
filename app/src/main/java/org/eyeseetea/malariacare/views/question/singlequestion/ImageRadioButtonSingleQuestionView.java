package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.views.option.ImageRadioButtonOption;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImageRadioButtonSingleQuestionView extends AOptionQuestionView implements
        IQuestionView, ImageRadioButtonOption.OnCheckedChangeListener {
    Question mQuestion;

    LinearLayout answersContainer;
    private boolean optionSetBySavedValue = false;

    public ImageRadioButtonSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    public ImageRadioButtonOption getSelectedOptionView() {

        ImageRadioButtonOption selectedOptionView = null;

        for (int i = 0; i < answersContainer.getChildCount(); i++) {
            ImageRadioButtonOption imageRadioButtonOption =
                    (ImageRadioButtonOption) answersContainer.getChildAt(i);

            if (imageRadioButtonOption.isChecked()) {
                selectedOptionView = imageRadioButtonOption;
            }
        }

        return selectedOptionView;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        answersContainer.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setOptions(List<Option> options) {
        for (Option option : options) {
            ImageRadioButtonOption imageRadioButtonOption = createOptionView(option);
            answersContainer.addView(imageRadioButtonOption);
        }
    }

    @Override
    public void setQuestion(Question question) {
        this.mQuestion = question;
    }

    @Override
    public void setValue(Value value) {
        if (value == null || value.getValue() == null) {
            return;
        }

        for (int i = 0; i < answersContainer.getChildCount(); i++) {
            ImageRadioButtonOption imageRadioButtonOption =
                    (ImageRadioButtonOption) answersContainer.getChildAt(i);

            if (imageRadioButtonOption.getOption().equals(value.getOption())) {
                optionSetBySavedValue = true;
                imageRadioButtonOption.setChecked(true);
            }
        }

    }

    @NonNull
    private ImageRadioButtonOption createOptionView(Option option) {
        ImageRadioButtonOption imageRadioButtonOption = new ImageRadioButtonOption(
                getContext());
        imageRadioButtonOption.setText(option.getInternationalizedName());
        putImageInImageRadioButton(option.getInternationalizedPath(), imageRadioButtonOption);
        imageRadioButtonOption.setOnCheckedChangeListener(this);
        imageRadioButtonOption.setOption(option, mQuestion);
        imageRadioButtonOption.setEnabled(super.isEnabled());
        return imageRadioButtonOption;
    }


    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_row_empty_question, this);

        answersContainer = (LinearLayout) findViewById(R.id.answer);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void putImageInImageRadioButton(String path,
            ImageRadioButtonOption imageRadioButtonOption) {
        if (path == null || path.equals("")) {
            return;
        }
        try {
            InputStream ims = getContext().getAssets().open(path);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDensity = DisplayMetrics.DENSITY_HIGH;
            Drawable drawable = Drawable.createFromResourceStream(getResources(), null, ims, null,
                    opts);
            imageRadioButtonOption.setImageDrawable(drawable);
            ims.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCheckedChanged(ImageRadioButtonOption imageRadioButton, boolean value) {
        if (value == false) return;

        for (int i = 0; i < answersContainer.getChildCount(); i++) {
            ImageRadioButtonOption optionView =
                    (ImageRadioButtonOption) answersContainer.getChildAt(i);

            if (imageRadioButton != optionView && optionView.isChecked()) {
                optionView.setChecked(false);
            }
        }
        if (!optionSetBySavedValue) {
            notifyAnswerChanged(imageRadioButton.getOption());
        } else {
            optionSetBySavedValue = false;
        }

        //TODO: Review architecture listeners
        //This question type not save in database from listeners when is answered because for
        // something
        //is necessary confirm counter for invalid option
        //notifyAnswerChanged(imageRadioButton.getOption());
    }
}
