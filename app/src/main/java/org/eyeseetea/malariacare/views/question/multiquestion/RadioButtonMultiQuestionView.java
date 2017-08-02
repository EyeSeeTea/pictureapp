package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomRadioButton;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class RadioButtonMultiQuestionView extends AOptionQuestionView implements IQuestionView,
        IMultiQuestionView, IImageQuestionView {
    CustomTextView header;
    ImageView image;
    RadioGroup radioGroup;
    Context context;
    QuestionDB mQuestionDB;

    public RadioButtonMultiQuestionView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public void setQuestionDB(QuestionDB questionDB) {
        if (questionDB.getOutput() == Constants.RADIO_GROUP_HORIZONTAL) {
            radioGroup.setOrientation(HORIZONTAL);
        } else {
            radioGroup.setOrientation(VERTICAL);
        }
        this.mQuestionDB = questionDB;
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        LayoutInflater lInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        for (OptionDB optionDB : optionDBs) {
            CustomRadioButton radioButton =
                    (CustomRadioButton) lInflater.inflate(
                            R.layout.uncheckeable_radiobutton, null);
            radioButton.setTag(optionDB);
            radioButton.setText(optionDB.getInternationalizedName());
            fixRadioButtonWidth(radioButton);

            radioButton.setEnabled(radioGroup.isEnabled());

            radioGroup.addView(radioButton);
            radioGroup.setOnCheckedChangeListener(
                    new RadioGroupListener((View) radioGroup.getParent(), mQuestionDB));
        }
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setImage(String path) {
        if (path != null && !path.equals("")) {
            BaseLayoutUtils.makeImageVisible(path, image);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        radioGroup.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setValue(Value value) {
        if (value == null || value.getValue() == null) {
            return;
        }

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            CustomRadioButton customRadioButton = (CustomRadioButton) radioGroup.getChildAt(i);
            customRadioButton.setChecked(
                    ((OptionDB) customRadioButton.getTag()).equals(value.getOptionDB()));
        }
    }

    @Override
    public boolean hasError() {
        if (mQuestionDB.isCompulsory() && radioGroup.getCheckedRadioButtonId() == -1) {
            return true;
        }
        return false;
    }

    private void init(final Context context) {
        View view = inflate(context, R.layout.multi_question_radio_button_row, this);
        header = (CustomTextView) view.findViewById(R.id.row_header_text);
        image = (ImageView) view.findViewById(R.id.question_image_row);
        radioGroup = (RadioGroup) view.findViewById(R.id.answer);
    }

    private void fixRadioButtonWidth(
            CustomRadioButton radioButton) {
        Drawable radioButtonIcon = getResources().getDrawable(R.drawable.radio_on);
        BaseLayoutUtils.setLayoutParamsAs50Percent(radioButton, context,
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


    public class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        private View viewHolder;
        private QuestionDB mQuestionDB;

        public RadioGroupListener(View viewHolder, QuestionDB questionDB) {
            this.mQuestionDB = questionDB;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (!group.isShown()) {
                return;
            }

            OptionDB selectedOptionDB = new OptionDB(Constants.DEFAULT_SELECT_OPTION);
            if (checkedId != -1) {
                CustomRadioButton customRadioButton = findRadioButtonById(checkedId);
                selectedOptionDB = (OptionDB) customRadioButton.getTag();
            }
            notifyAnswerChanged(selectedOptionDB);
        }

        /**
         * Fixes a bug in older apis where a RadioGroup cannot find its children by id
         */
        public CustomRadioButton findRadioButtonById(int id) {
            //No component -> done
            if (radioGroup == null || !(radioGroup instanceof RadioGroup)) {
                return null;
            }

            //Modern api -> delegate in its method
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                return (CustomRadioButton) radioGroup.findViewById(id);
            }

            //Find button manually
            for (int i = 0; i < ((RadioGroup) radioGroup).getChildCount(); i++) {
                View button = ((RadioGroup) radioGroup).getChildAt(i);
                if (button.getId() == id) {
                    return (CustomRadioButton) button;
                }
            }
            return null;
        }
    }
}

