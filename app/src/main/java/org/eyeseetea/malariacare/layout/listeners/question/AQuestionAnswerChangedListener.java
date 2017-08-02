package org.eyeseetea.malariacare.layout.listeners.question;

import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
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
     * Returns the option selected for the given questionDB and boolean value or by position
     */
    public static OptionDB findSwitchOption(QuestionDB questionDB, boolean isChecked) {
        //Search option by position
        if (isChecked) {
            return questionDB.getAnswerDB().getOptionDBs().get(0);
        } else {
            return questionDB.getAnswerDB().getOptionDBs().get(1);
        }
    }

    //TODO: Duplicate code in DynamicTabAdapter line 1094
    //code in DynamicTabAdapter will be delete when DynamicTabAdapter refactoring will be completed

    /**
     * Returns the boolean selected for the given questionDB (by boolean value or position option,
     * position 1=true 0=false)
     */
    public static Boolean findSwitchBoolean(QuestionDB questionDB) {
        Value value = questionDB.getValueBySession();
        if (value.getValue().equals(questionDB.getAnswerDB().getOptionDBs().get(0).getCode())) {
            return true;
        } else if (value.getValue().equals(questionDB.getAnswerDB().getOptionDBs().get(1).getCode())) {
            return false;
        }
        return false;
    }

    protected void saveValue(View view, String newValue) {
        QuestionDB questionDB = (QuestionDB) view.findViewById(R.id.answer).getTag();
        questionDB.saveValuesText(newValue);

        showOrHideChildren(questionDB);
    }


    protected void saveValue(View view, OptionDB optionDB) {
        QuestionDB questionDB = (QuestionDB) view.findViewById(R.id.answer).getTag();
        questionDB.saveValuesDDL(optionDB, questionDB.getValueBySession());

        showOrHideChildren(questionDB);
    }

    /**
     * Hide or show the children questionDB from a given questionDB,  if is necessary  it reloads the
     * children questions values or refreshing the children questions answer component
     *
     * TODO: Duplicate code in DynamicTabAdapter line 1094
     * code in DynamicTabAdapter will be delete when DynamicTabAdapter refactoring will be
     * completed
     *
     * @param questionDB is the parent questionDB
     */
    protected void showOrHideChildren(QuestionDB questionDB) {
        if (!questionDB.hasChildren()) {
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
                QuestionDB rowQuestionDB = (QuestionDB) answerView.getTag();
                if (rowQuestionDB == null) {
                    continue;
                }
                List<QuestionDB> questionDBChildren = questionDB.getChildren();
                if (questionDBChildren != null && questionDBChildren.size() > 0) {
                    for (QuestionDB childQuestionDB : questionDBChildren) {
                        //if the table row questionDB is child of the modified questionDB...
                        toggleChild(row, rowQuestionDB, childQuestionDB);
                    }
                }
            }
        }
    }

    /**
     * find and toggle the child question
     *
     * @param row           is the child question view
     * @param rowQuestionDB   is the question in the view
     * @param childQuestionDB is the posible child
     */
    private boolean toggleChild(TableRow row, QuestionDB rowQuestionDB, QuestionDB childQuestionDB) {
        if (childQuestionDB.getId_question().equals(rowQuestionDB.getId_question())) {
            if (rowQuestionDB.isHiddenBySurveyAndHeader(Session.getMalariaSurveyDB())) {
                row.setVisibility(View.GONE);
                hideDefaultValue(rowQuestionDB);
            } else {
                row.setVisibility(View.VISIBLE);
                showDefaultValue(row, rowQuestionDB);
            }
            return true;
        }
        return false;
    }

    /**
     * removes or modify the value with a correct value when the question is hide
     *
     * @param rowQuestionDB is the question in the view
     */
    private void hideDefaultValue(QuestionDB rowQuestionDB) {
        switch (rowQuestionDB.getOutput()) {
            case Constants.PHONE:
            case Constants.POSITIVE_INT:
            case Constants.INT:
            case Constants.LONG_TEXT:
            case Constants.SHORT_TEXT:
            case Constants.DROPDOWN_LIST:
            case Constants.DROPDOWN_OU_LIST:
                rowQuestionDB.deleteValueBySession();
                break;
            case Constants.SWITCH_BUTTON:
                //the 0 option is the left option and is false in the switch, the 1 option is the
                // right option and is true
                boolean isChecked = false;
                if (rowQuestionDB.getAnswerDB().getOptionDBs().get(
                        1).getOptionAttributeDB().getDefaultOption() == 1) {
                    isChecked = true;
                }
                saveSwitchOption(rowQuestionDB, isChecked);
                break;
        }
    }

    /**
     * when a question is shown this method set the correct value.
     *
     * @param rowQuestionDB is the question in the view
     */
    private void showDefaultValue(TableRow tableRow, QuestionDB rowQuestionDB) {
        if (rowQuestionDB.getValueBySession() != null) {
            return;
        }
        switch (rowQuestionDB.getOutput()) {
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
                OptionDB selectedOptionDB = rowQuestionDB.getOptionBySession();
                if (selectedOptionDB == null) {
                    //the 0 option is the left option and is false in the switch, the 1 option is
                    // the right option and is true
                    boolean isChecked = false;
                    if (rowQuestionDB.getAnswerDB().getOptionDBs().get(
                            1).getOptionAttributeDB().getDefaultOption() == 1) {
                        isChecked = true;
                    }
                    saveSwitchOption(rowQuestionDB, isChecked);
                    switchView.setChecked(isChecked);
                    break;
                }
                switchView.setChecked(findSwitchBoolean(rowQuestionDB));
                break;
        }
    }

    /**
     * Save the switch option and check children questions
     *
     * @param questionDB  is the questionDB in the view
     * @param isChecked is the value to be saved
     */
    private void saveSwitchOption(QuestionDB questionDB, boolean isChecked) {
        //Take option
        OptionDB selectedOptionDB = findSwitchOption(questionDB, isChecked);
        if (selectedOptionDB == null) {
            return;
        }
        questionDB.saveValuesDDL(selectedOptionDB, questionDB.getValueBySession());
        showOrHideChildren(questionDB);
    }

    protected boolean isMultipleQuestionTab(int tabType) {
        return tabType == Constants.TAB_MULTI_QUESTION;
    }
}
