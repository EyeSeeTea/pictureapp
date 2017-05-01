package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class SwitchMultiQuestionView extends AOptionQuestionView implements IQuestionView,
        IMultiQuestionView, IImageQuestionView {
    //the 0 option is the right option and is true in the switch, the 1 option is the
    // left option and is false

    CustomTextView headerView;
    CustomTextView helpTextView;
    ImageView imageView;
    Switch switchView;

    CustomTextView switchTrueTextView;
    CustomTextView switchFalseTextView;

    Option trueOption;
    Option falseOption;

    public SwitchMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<Option> options) {
        switchTrueTextView = (CustomTextView) findViewById(R.id.row_switch_true);
        switchFalseTextView = (CustomTextView) findViewById(R.id.row_switch_false);

        trueOption = options.get(0);
        falseOption = options.get(1);

        switchTrueTextView.setText(Utils.getInternationalizedString(trueOption.getName()));
        switchFalseTextView.setText(Utils.getInternationalizedString(falseOption.getName()));
    }

    @Override
    public void setQuestion(Question question) {

    }

    @Override
    public void setHeader(String headerValue) {
        headerView.setText(headerValue);
    }

    @Override
    public void setEnabled(boolean enabled) {
        switchView.setEnabled(enabled);
    }

    @Override
    public void setImage(String path) {
        if (path != null && !path.equals("")) {
            LayoutUtils.makeImageVisible(path, imageView);
        }
    }

    @Override
    public void setValue(Value value) {
        if (value == null || value.getValue() == null) {
            setDefaultValue();
        } else {
            switchView.setChecked(value.getValue().equals(trueOption.getCode()));
        }
    }

    private void setDefaultValue() {
        boolean isDefaultOption = false;
        boolean switchValue = false;
        if (trueOption.getOptionAttribute().getDefaultOption() == 1) {
            isDefaultOption = true;
            switchValue = true;
        } else if (falseOption.getOptionAttribute().getDefaultOption() == 1) {
            isDefaultOption = true;
            switchValue = false;
        }
        if (isDefaultOption) {
            switchView.setChecked(switchValue);
            notifyAnswerChanged((switchValue) ? trueOption : falseOption);
        }
    }

    @Override
    public void setHelpText(String helpText) {
        helpTextView.setText(helpText);
    }

    @Override
    public boolean hasError() {
        return false;
    }

    private void init(Context context) {
        inflate(context, R.layout.multi_question_tab_switch_row, this);

        headerView = (CustomTextView) findViewById(R.id.row_header_text);
        helpTextView = (CustomTextView) findViewById(R.id.row_help_text);
        imageView = ((ImageView) findViewById(R.id.question_image_row));
        switchView = (Switch) findViewById(R.id.answer);

        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notifyAnswerChanged((isChecked) ? trueOption : falseOption);
            }
        });
    }
}
