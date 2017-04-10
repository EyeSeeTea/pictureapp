package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;
import org.eyeseetea.malariacare.views.option.ImageRadioButtonOption;
import org.eyeseetea.malariacare.views.question.AKeyboardQuestionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class DynamicStockImageRadioButtonSingleQuestionView extends LinearLayout implements
        IQuestionView, ImageRadioButtonOption.OnCheckedChangeListener {

    protected AKeyboardQuestionView.onAnswerChangedListener mOnAnswerChangedListener;
    protected AOptionQuestionView.onAnswerChangedListener mOnAnswerOptionChangedListener;
    Question mQuestion;
    LinearLayout answersContainer;
    HashMap<Long, Float> optionDose;
    private Context context;

    public DynamicStockImageRadioButtonSingleQuestionView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_row_empty_question, this);

        answersContainer = (LinearLayout) findViewById(R.id.answer);
        setOrientation(LinearLayout.VERTICAL);
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

    public void setOptionDose(HashMap<Long, Float> optionDose) {
        this.optionDose = optionDose;
    }


    public void setOptions(List<Option> options) {
        for (Option option : options) {
            ImageRadioButtonOption imageRadioButtonOption = createOptionView(option);
            answersContainer.addView(imageRadioButtonOption);
        }
    }

    @NonNull
    private ImageRadioButtonOption createOptionView(Option option) {
        ImageRadioButtonOption imageRadioButtonOption = new ImageRadioButtonOption(
                getContext(), true);

        imageRadioButtonOption.setText(option.getInternationalizedName());
        putImageInImageRadioButton(option.getInternationalizedPath(), imageRadioButtonOption);
        imageRadioButtonOption.setOnCheckedChangeListener(this);
        imageRadioButtonOption.setOption(option, mQuestion);
        imageRadioButtonOption.setEnabled(super.isEnabled());
        imageRadioButtonOption.setTag(Question.findByID(option.getId_option()));
        return imageRadioButtonOption;
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

    public void setQuestion(Question question) {
        this.mQuestion = question;
    }

    @Override
    public void onCheckedChanged(ImageRadioButtonOption imageRadioButton, boolean value) {
        if (value == false) return;

        for (int i = 0; i < answersContainer.getChildCount(); i++) {
            ImageRadioButtonOption optionView =
                    (ImageRadioButtonOption) answersContainer.getChildAt(i);

            if (imageRadioButton != optionView && optionView.isChecked()) {
                optionView.setChecked(false);
                Question question = (Question) optionView.getTag();
                if (!TreatmentQueries.isOutStockQuestion(question.getUid())) {
                    notifyAnswerChanged(optionView, String.valueOf(-1));
                } else {
                    List<Option> options = question.getAnswer().getOptions();
                    for (Option option : options) {
                        if (option.getName().equals(
                                PreferencesState.getInstance().getContext().getString(
                                        R.string.false_option_id))) {
                            notifyAnsweOptionChange(optionView, option);
                        }
                    }
                }

            }
        }
        Question question = (Question) imageRadioButton.getTag();
        if (!TreatmentQueries.isOutStockQuestion(question.getUid())) {
            notifyAnswerChanged(imageRadioButton,
                    String.valueOf(optionDose.get(imageRadioButton.getOption().getId_option())));
        } else {
            List<Option> options = question.getAnswer().getOptions();
            for (Option option : options) {
                if (option.getName().equals(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.true_option_id))) {
                    notifyAnsweOptionChange(imageRadioButton, option);
                }
            }
        }
        //Setting a value for the stock question to get max total question correct
        View stockHideView = new View(context);
        stockHideView.setTag(TreatmentQueries.getDynamicStockQuestion());
        Question pqHideQuestion = TreatmentQueries.getStockPqQuestion();
        Option falseOption = Option.findById(41l);
        Value valuePq = pqHideQuestion.getValueBySession();
        if (valuePq != null) {
            falseOption = valuePq.getOption();
        }
        notifyAnsweOptionChange(stockHideView, falseOption);
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
    public void setValue(Value value) {
        if (value == null || value.getValue() == null) {
            return;
        }

        for (int i = 0; i < answersContainer.getChildCount(); i++) {
            ImageRadioButtonOption imageRadioButtonOption =
                    (ImageRadioButtonOption) answersContainer.getChildAt(i);
            Question question = (Question) imageRadioButtonOption.getTag();
            if (!TreatmentQueries.isOutStockQuestion(question.getUid())
                    && question.getId_question().equals(value.getQuestion().getId_question())
                    && Float.parseFloat(
                    value.getValue()) > 0) {
                imageRadioButtonOption.setChecked(true);
            } else if (question.getId_question().equals(value.getQuestion().getId_question())) {
                List<Option> options = question.getAnswer().getOptions();
                for (Option option : options) {
                    if ((option.getName().equals(
                            PreferencesState.getInstance().getContext().getString(
                                    R.string.true_option_id)))
                            && option.getId_option().equals(
                            value.getId_option())) {
                        imageRadioButtonOption.setChecked(true);
                    }
                }
            }
        }
    }


    protected void notifyAnswerChanged(View view, String newValue) {
        if (mOnAnswerChangedListener != null) {
            mOnAnswerChangedListener.onAnswerChanged(view, newValue);
        }
    }

    protected void notifyAnsweOptionChange(View view, Option option) {
        if (mOnAnswerOptionChangedListener != null) {
            mOnAnswerOptionChangedListener.onAnswerChanged(view, option);
        }
    }

    public void setOnAnswerChangedListener(
            AKeyboardQuestionView.onAnswerChangedListener onAnswerChangedListener) {
        mOnAnswerChangedListener = onAnswerChangedListener;
    }

    public void setOnAnswerOptionChangedListener(
            AOptionQuestionView.onAnswerChangedListener onAnswerOptionChangedListener) {
        mOnAnswerOptionChangedListener = onAnswerOptionChangedListener;
    }
}
