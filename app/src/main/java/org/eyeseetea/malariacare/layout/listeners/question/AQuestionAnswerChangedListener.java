package org.eyeseetea.malariacare.layout.listeners.question;

import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.sdk.presentation.views.CustomEditText;

import java.util.List;

public abstract class AQuestionAnswerChangedListener {
    protected TableLayout mTableLayout;

    public AQuestionAnswerChangedListener(TableLayout tableLayout) {
        this.mTableLayout = tableLayout;
    }

    /**
     * Returns the option selected for the given question and boolean value or by position
     */
    public static Option findSwitchOption(Question question, boolean isChecked) {
        //Search option by position
        if (isChecked) {
            return question.getAnswer().getOptions().get(0);
        } else {
            return question.getAnswer().getOptions().get(1);
        }
    }

    //TODO: Duplicate code in DynamicTabAdapter line 1094
    //code in DynamicTabAdapter will be delete when DynamicTabAdapter refactoring will be completed

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

    protected void saveValue(View view, String newValue) {
        Question question = (Question) view.findViewById(R.id.answer).getTag();
        question.saveValuesText(newValue);

        showOrHideChildren(question);
    }


    protected void saveValue(View view, Option option) {
        Question question = (Question) view.findViewById(R.id.answer).getTag();
        question.saveValuesDDL(option, question.getValueBySession());

        showOrHideChildren(question);
    }

    /**
     * Hide or show the children question from a given question,  if is necessary  it reloads the
     * children questions values or refreshing the children questions answer component
     *
     * TODO: Duplicate code in DynamicTabAdapter line 1094
     * code in DynamicTabAdapter will be delete when DynamicTabAdapter refactoring will be
     * completed
     *
     * @param question is the parent question
     */
    protected void showOrHideChildren(Question question) {
        if (!question.hasChildren()) {
            return;
        }

        for (int i = 0, j = mTableLayout.getChildCount(); i < j; i++) {
            View view = mTableLayout.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                View answerView = view.findViewById(R.id.answer);
                if (answerView == null) {
                    continue;
                }
                Question rowQuestion = (Question) answerView.getTag();
                if (rowQuestion == null) {
                    continue;
                }
                List<Question> questionChildren = question.getChildren();
                if (questionChildren != null && questionChildren.size() > 0) {
                    for (Question childQuestion : questionChildren) {
                        //if the table row question is child of the modified question...
                        toggleChild(row, rowQuestion, childQuestion);
                    }
                }
            }
        }
    }

    /**
     * find and toggle the child question
     *
     * @param row           is the child question view
     * @param rowQuestion   is the question in the view
     * @param childQuestion is the posible child
     */
    private boolean toggleChild(TableRow row, Question rowQuestion, Question childQuestion) {
        if (childQuestion.getId_question().equals(rowQuestion.getId_question())) {
            if (rowQuestion.isHiddenBySurveyAndHeader(Session.getMalariaSurvey())) {
                row.setVisibility(View.GONE);
                hideDefaultValue(rowQuestion);
            } else {
                row.setVisibility(View.VISIBLE);
                showDefaultValue(row, rowQuestion);
            }
            return true;
        }
        return false;
    }

    /**
     * removes or modify the value with a correct value when the question is hide
     *
     * @param rowQuestion is the question in the view
     */
    private void hideDefaultValue(Question rowQuestion) {
        switch (rowQuestion.getOutput()) {
            case Constants.PHONE:
            case Constants.POSITIVE_INT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.SHORT_TEXT:
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                rowQuestion.deleteValueBySession();
                break;
            case Constants.SWITCH_BUTTON:
                //the 0 option is the left option and is false in the switch, the 1 option is the
                // right option and is true
                boolean isChecked = false;
                if (rowQuestion.getAnswer().getOptions().get(
                        1).getOptionAttribute().getDefaultOption() == 1) {
                    isChecked = true;
                }
                saveSwitchOption(rowQuestion, isChecked);
                break;
        }
    }

    /**
     * when a question is shown this method set the correct value.
     *
     * @param rowQuestion is the question in the view
     */
    private void showDefaultValue(TableRow tableRow, Question rowQuestion) {
        if (rowQuestion.getValueBySession() != null) {
            return;
        }
        switch (rowQuestion.getOutput()) {
            case Constants.PHONE:
            case Constants.POSITIVE_INT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.SHORT_TEXT:
                final CustomEditText editCard = (CustomEditText) tableRow.findViewById(R.id.answer);
                editCard.setText("");
                break;
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                Spinner dropdown = (Spinner) tableRow.findViewById(R.id.answer);
                dropdown.setSelection(0);
                break;
            case Constants.SWITCH_BUTTON:
                Switch switchView = (Switch) tableRow.findViewById(R.id.answer);
                Option selectedOption = rowQuestion.getOptionBySession();
                if (selectedOption == null) {
                    //the 0 option is the left option and is false in the switch, the 1 option is
                    // the right option and is true
                    boolean isChecked = false;
                    if (rowQuestion.getAnswer().getOptions().get(
                            1).getOptionAttribute().getDefaultOption() == 1) {
                        isChecked = true;
                    }
                    saveSwitchOption(rowQuestion, isChecked);
                    switchView.setChecked(isChecked);
                    break;
                }
                switchView.setChecked(findSwitchBoolean(rowQuestion));
                break;
        }
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
        question.saveValuesDDL(selectedOption, question.getValueBySession());
        showOrHideChildren(question);
    }

    protected boolean isMultipleQuestionTab(int tabType) {
        return tabType == Constants.TAB_MULTI_QUESTION;
    }
}
