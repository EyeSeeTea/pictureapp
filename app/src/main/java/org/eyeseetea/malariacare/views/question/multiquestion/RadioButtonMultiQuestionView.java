package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.CustomRadioButton;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class RadioButtonMultiQuestionView extends AOptionQuestionView implements IQuestionView,
        IMultiQuestionView ,IImageQuestionView {
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
        if (question.getOutput() == Constants.RADIO_GROUP_HORIZONTAL) {
            radioGroup.setOrientation(HORIZONTAL);
        } else {
            radioGroup.setOrientation(VERTICAL);
        }
        this.question = question;
    }

    @Override
    public void setOptions(List<Option> options) {
        LayoutInflater lInflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        for (Option option : options) {
            CustomRadioButton radioButton = (CustomRadioButton) lInflater.inflate(
                    R.layout.uncheckeable_radiobutton, null);
            radioButton.setOption(option);
            BaseLayoutUtils.setLayoutParamsAs50Percent(radioButton, context,
                    0);
            radioButton.updateProperties(PreferencesState.getInstance().getScale(),
                    context.getString(R.string.font_size_level1),
                    context.getString(R.string.specific_language_font));
            radioGroup.addView(radioButton);
            radioGroup.setOnCheckedChangeListener(
                    new RadioGroupListener((View) radioGroup.getParent(), question));
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

    @Override
    public void setValue(Value value) {
        if (value == null || value.getValue() == null) {
            return;
        }

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            CustomRadioButton customRadioButton = (CustomRadioButton) radioGroup.getChildAt(i);
            customRadioButton.setChecked(customRadioButton.getOption().equals(value.getOption()));
        }
    }


    private void init(final Context context) {
        View view = inflate(context, R.layout.multi_question_radio_button_row, this);
        header = (TextCard) view.findViewById(R.id.row_header_text);
        image = (ImageView) view.findViewById(R.id.question_image_row);
        radioGroup = (RadioGroup) view.findViewById(R.id.answer);
    }

    public class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        private View viewHolder;
        private Question question;

        public RadioGroupListener(View viewHolder, Question question) {
            this.question = question;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (!group.isShown()) {
                return;
            }

            Option selectedOption = new Option(Constants.DEFAULT_SELECT_OPTION);
            if (checkedId != -1) {
                CustomRadioButton customRadioButton = findRadioButtonById(checkedId);
                selectedOption = customRadioButton.getOption();
            }
            notifyAnswerChanged(selectedOption);
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
            for (int i = 0; i < ((RadioGroup) ((RadioGroup) radioGroup).getChildAt(
                    0)).getChildCount(); i++) {
                View button = ((RadioGroup) radioGroup).getChildAt(i);
                if (button.getId() == id) {
                    return (CustomRadioButton) button;
                }
            }
            return null;
        }
    }
}

