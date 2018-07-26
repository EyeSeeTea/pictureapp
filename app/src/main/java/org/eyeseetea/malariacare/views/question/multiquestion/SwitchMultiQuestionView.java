package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
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

    OptionDB mTrueOptionDB;
    OptionDB mFalseOptionDB;

    public SwitchMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        switchTrueTextView = (CustomTextView) findViewById(R.id.row_switch_true);
        switchFalseTextView = (CustomTextView) findViewById(R.id.row_switch_false);

        mTrueOptionDB = optionDBs.get(0);
        mFalseOptionDB = optionDBs.get(1);

        switchTrueTextView.setText(Utils.getInternationalizedString(mTrueOptionDB.getName()));
        switchFalseTextView.setText(Utils.getInternationalizedString(mFalseOptionDB.getName()));
    }

    public void setQuestionDB(QuestionDB questionDB) {

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
    public void setValue(ValueDB valueDB) {
        if (valueDB == null || valueDB.getValue() == null) {
            setDefaultValue();
        } else {
            switchView.setChecked(valueDB.getValue().equals(mTrueOptionDB.getCode()));
        }
    }

    private void setDefaultValue() {
        boolean isDefaultOption = false;
        boolean switchValue = false;
        if (mTrueOptionDB.getOptionAttributeDB().getDefaultOption() == 1) {
            isDefaultOption = true;
            switchValue = true;
        } else if (mFalseOptionDB.getOptionAttributeDB().getDefaultOption() == 1) {
            isDefaultOption = true;
            switchValue = false;
        }
        if (isDefaultOption) {
            switchView.setChecked(switchValue);
            notifyAnswerChanged((switchValue) ? mTrueOptionDB : mFalseOptionDB);
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

    @Override
    public void requestAnswerFocus() {
        switchView.requestFocus();
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
                notifyAnswerChanged((isChecked) ? mTrueOptionDB : mFalseOptionDB);
            }
        });
        switchView.setFocusable(true);
        switchView.setFocusableInTouchMode(true);
    }
}
