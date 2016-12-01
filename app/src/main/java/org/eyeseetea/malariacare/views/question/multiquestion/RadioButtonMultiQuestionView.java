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
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.database.utils.Session;
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
        this.context=context;
        init(context);
    }

    @Override
    public void setQuestion(Question question){
        this.question=question;
    }

    @Override
    public void setOptions(List<Option> options) {
        LayoutInflater lInflater = (LayoutInflater) context.getSystemService
            (Context.LAYOUT_INFLATER_SERVICE);
        for (Option option : options) {
            CustomRadioButton button = (CustomRadioButton) lInflater.inflate(R.layout.uncheckeable_radiobutton, null);
            button.setOption(option);
            button.updateProperties(PreferencesState.getInstance().getScale(), context.getString(R.string.font_size_level1), context.getString(R.string.specific_language_font));
            radioGroup.addView(button);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroupListener(question, radioGroup));
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setImage(String path) {
        if(path!=null && !path.equals(""))
        BaseLayoutUtils.makeImageVisible(path, image);
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
            }
            else
            {
                ((CustomRadioButton) radioGroup.getChildAt(i)).setChecked(false);
            }
        }
    }
    /**
     * Initialize the default switch value or load the saved value
     *
     * @param question       is the question in the view
     */
    private void initSwitchOption(Question question) {

        //Take option
        Option selectedOption = question.getOptionBySession();
        if (selectedOption == null) {
            //the 0 option is the right option and is true in the switch, the 1 option is the
            // left option and is false
            boolean isDefaultOption = false;
            boolean switchValue = false;
            if (question.getAnswer().getOptions().get(0).getOptionAttribute().getDefaultOption()
                    == 1) {
                selectedOption = question.getAnswer().getOptions().get(0);
                isDefaultOption = true;
                switchValue = true;
            } else if (question.getAnswer().getOptions().get(
                    1).getOptionAttribute().getDefaultOption() == 1) {
                selectedOption = question.getAnswer().getOptions().get(1);
                isDefaultOption = true;
                switchValue = false;
            }
            if (isDefaultOption) {
                //radioButton.setChecked(switchValue);
                ReadWriteDB.saveValuesDDL(question, selectedOption, null);
            }
        } else {
            //radioButton.setChecked(findSwitchBoolean(question));
        }
    }

    /**
     * Returns the option selected for the given question and boolean value or by position
     */
    public static Option findSwitchOption(Question question, boolean isChecked) {
        //Search option by position
        return question.getAnswer().getOptions().get((isChecked) ? 0 : 1);
    }
    /**
     * Save the switch option and check children questions
     *
     * @param question  is the question in the view
     * @param isChecked is the value to be saved
     */
    private void saveSwitchOption(Question question, boolean isChecked) {
        //Take option
        Option selectedOption = findSwitchOption(question, isChecked);
        if (selectedOption == null) {
            return;
        }
        ReadWriteDB.saveValuesDDL(question, selectedOption, question.getValueBySession());
        //showOrHideChildren(question);
    }


    public static void createRadioGroupComponent(Question question, RadioGroup radioGroup, int orientation, LayoutInflater lInflater, Context context) {
        radioGroup.setOrientation(orientation);

        for (Option option : question.getAnswer().getOptions()) {
            CustomRadioButton button = (CustomRadioButton) lInflater.inflate(R.layout.uncheckeable_radiobutton, null);
            button.setOption(option);
            button.updateProperties(PreferencesState.getInstance().getScale(), context.getString(R.string.font_size_level1), context.getString(R.string.medium_font_name));
            radioGroup.addView(button);
        }
    }


    private void init(final Context context) {
        View view = inflate(context, R.layout.multi_question_radio_buttons, this);
        header = (TextCard) view.findViewById(R.id.row_header_text);
        image = (ImageView) view.findViewById(R.id.question_image_row);
        radioGroup = (RadioGroup) view.findViewById(R.id.answer);
        radioGroup.setOrientation(HORIZONTAL);
    }

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener  {
        private RadioGroup radioGroup = null;
        private Question question;

        public RadioGroupListener(Question question, RadioGroup radioGroup) {
            this.question = question;
            this.radioGroup = radioGroup;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(!group.isShown()){
                return;
            }

            Option selectedOption = new Option(Constants.DEFAULT_SELECT_OPTION);
            if (checkedId != -1) {
                CustomRadioButton customRadioButton = findRadioButtonById(checkedId);
                selectedOption = (Option) customRadioButton.getTag();
                if(question.getOptionBySurvey(Session.getSurvey())!=null && question.getOptionBySurvey(Session.getSurvey()).equals(selectedOption)){
                    //if is already active ignore it( it is to ignore the first click of two)
                    return;
                }
            }

           // notifyAnswerChanged(String.valueOf(selectedOption.getv()));
            //AutoTabSelectedItem autoTabSelectedItem = autoTabSelectedItemFactory.buildSelectedItem(question,selectedOption,viewHolder, idSurvey, module);
            //AutoTabLayoutUtils.itemSelected(autoTabSelectedItem, idSurvey, module);
            //autoTabSelectedItemFactory.notifyDataSetChanged();
        }
        /**
         * Fixes a bug in older apis where a RadioGroup cannot find its children by id
         *
         * @param id
         * @return
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

