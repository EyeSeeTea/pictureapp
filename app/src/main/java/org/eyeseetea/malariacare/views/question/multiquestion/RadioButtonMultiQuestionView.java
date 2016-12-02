package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.CustomRadioButton;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class RadioButtonMultiQuestionView extends AOptionQuestionView implements IQuestionView,
        IMultiQuestionView {
    TextCard header;
    ImageView image;
    RadioGroup radioGroup;
    Context context;
    Question question;

    public RadioButtonMultiQuestionView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    @Override
    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public void setOptions(List<Option> options) {
        LayoutInflater lInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        for (Option option : options) {
            LinearLayout linearLayout = (LinearLayout) lInflater.inflate(
                    R.layout.uncheckeable_radiobutton, null);

            //This method is used to correct the 50% space in the radiobutton with two buttons.
            BaseLayoutUtils.setLayoutParamsAs50Percent(linearLayout, context);

            CustomRadioButton button = (CustomRadioButton) linearLayout.findViewById(
                    R.id.radio_button);
            button.setOption(option);
            TextCard textCard = (TextCard) linearLayout.findViewById(R.id.radio_text);
            textCard.setText(option.getName());
            if (question.getOptionBySession() != null && question.getOptionBySession().equals(
                    option)) {
                button.setChecked(true);
            }
            button.setOnCheckedChangeListener(new RadioButtonListener(question));
            radioGroup.addView(linearLayout);
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
    public boolean hasError() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        radioGroup.setEnabled(enabled);
    }

    /**
     * Returns the boolean selected for the given question (by boolean value or position option,
     * position 1=true 0=false)
     */
    public static Boolean findSwitchBoolean(Question question) {
        Value value = question.getValueBySession();
        if (value.getValue().equals(question.getAnswer().getOptions().get(0).getCode())) {
            return true;
        } else if (value.getValue().equals(question.getAnswer().getOptions().get(1).getCode())) {
            return false;
        }
        return false;
    }

    @Override
    public void setValue(Value value) {

        if (value == null || value.getValue() == null) {
            return;
        }

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            CustomRadioButton customRadioButton = (CustomRadioButton) radioGroup.getChildAt(i);
            Option option = customRadioButton.getOption();
            if (option.equals(value.getOption())) {
                ((CustomRadioButton) radioGroup.getChildAt(i)).setChecked(true);
            } else {
                ((CustomRadioButton) radioGroup.getChildAt(i)).setChecked(false);
            }
        }
    }


    private void init(final Context context) {
        View view = inflate(context, R.layout.multi_question_radio_button_row, this);
        header = (TextCard) view.findViewById(R.id.row_header_text);
        image = (ImageView) view.findViewById(R.id.question_image_row);
        radioGroup = (RadioGroup) view.findViewById(R.id.answer);
        radioGroup.setOrientation(HORIZONTAL);
    }

    class RadioButtonListener implements RadioGroup.OnCheckedChangeListener,
            CompoundButton.OnCheckedChangeListener {
        private Question question;

        public RadioButtonListener(Question question) {
            this.question = question;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isShown()) {
                return;
            }

            CustomRadioButton customRadioButton = (CustomRadioButton) buttonView;

            Option optionToBeRemoved = new Option(Constants.DEFAULT_SELECT_OPTION);
            Option selectedOption = (Option) customRadioButton.getTag();
            Value value = question.getValueBySession();
            if (value != null && value.getOption() != null && value.getOption().equals(
                    selectedOption)) {
                selectedOption = optionToBeRemoved;
            }

            notifyAnswerChanged(selectedOption);
        }
    }
}

