/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.database.utils;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class ReadWriteDB {

    public static String readValueQuestion(Question question) {
        String result = null;

        Value value = question.getValueBySession();

        if (value != null) {
            result = value.getValue();
        }

        return result;
    }

    public static Option readOptionAnswered(Question question) {
        Option option = null;

        Value value = question.getValueBySession();

        if (value != null) {
            option = value.getOption();
        }

        return option;
    }

    public static Option getOptionAnsweredFromDB(Question question) {
        if (question != null) {
            Survey survey = (question.isStockQuestion() ? Session.getStockSurvey()
                    : Session.getMalariaSurvey());

            Value value = Value.findValue(question.getId_question(), survey);
            if (value != null) {
                return Option.findById(value.getId_option());
            }
        }
        return null;
    }

    public static void saveValuesDDL(Question question, Option option, Value value) {
        //No option, nothing to save
        if (option == null) {
            return;
        }
        if (question.isTreatmentQuestion() && value != null
                && !option.getId_option().equals(value.getId_option())) {
            deleteStockSurveyValues(question);
        }

        if (!option.getName().equals(Constants.DEFAULT_SELECT_OPTION)) {
            Survey survey =
                    ((question.isStockQuestion() || question.isPq() || question.isACT())
                            ? Session.getStockSurvey()
                            : Session.getMalariaSurvey());
            createOrSaveDDLValue(question, option, value, survey);
            for (Question propagateQuestion : question.getPropagationQuestions()) {
                createOrSaveDDLValue(propagateQuestion, option, value, Session.getMalariaSurvey());
            }
        } else {
            deleteValues(question, value);
        }
    }

    private static void createOrSaveDDLValue(Question question, Option option, Value value,
            Survey survey) {
        if (value == null) {
            value = new Value(option, question, survey);
        } else {
            if (!value.getOption().equals(option) && question.hasChildren()
                    && !question.isDynamicTreatmentQuestion()) {
                survey.removeChildrenValuesFromQuestionRecursively(question, false);
            }
            value.setOption(option);
            value.setValue(option.getName());
        }
        value.save();
    }

    public static void saveValuesText(Question question, String answer) {
        Value value = question.getValueBySession();
        Survey survey = (question.isStockQuestion() ? Session.getStockSurvey()
                : Session.getMalariaSurvey());
        if ((question.isTreatmentQuestion() || question.isPq() || question.isACT()) && value != null
                && !value.getValue().equals(answer)) {
            deleteStockSurveyValues(question);
        }
        if (question.isStockQuestion() && value != null && answer.equals("-1")) {
            deleteValues(question, value);
        } else {
            createOrSaveValue(question, answer, value, survey);
            for (Question propagateQuestion : question.getPropagationQuestions()) {
                createOrSaveValue(propagateQuestion, answer, value, Session.getMalariaSurvey());
            }
        }
    }

    private static void deleteValues(Question question, Value value) {
        if (value != null) {
            for (Question propagateQuestion : question.getPropagationQuestions()) {
                Value propagateValue = Value.findValue(propagateQuestion.getId_question(),
                        Session.getMalariaSurvey());
                if(propagateValue!=null) {
                    propagateValue.delete();
                }
            }
            value.delete();
        }
    }

    private static void createOrSaveValue(Question question, String answer, Value value,
            Survey survey) {
        // If the value is not found we create one
        if (value == null) {
            value = new Value(answer, question, survey);
        } else {
            value.setOption((Long) null);
            value.setValue(answer);
        }
        value.save();
    }

    public static void deleteValue(Question question) {
        Value value = question.getValueBySession();
        deleteValues(question, value);
    }

    public static void deleteStockSurveyValues(Question question) {
        Survey survey = Session.getStockSurvey();
        List<Value> stockValues = survey.getValues();
        for (Value value : stockValues) {
            if ((question.isACT() && value.getQuestion().isACT())) {
                deleteValues(question, value);
            }
        }
        Survey malariaSurvey = Session.getMalariaSurvey();
        List<Value> malariaValues = malariaSurvey.getValues();
        for (Value value : malariaValues) {
            if (value.getQuestion().isOutStockQuestion()) {
                value.delete();
            }
        }
    }


    public static Value insertValue(String value, Question question, Survey survey) {
        return new Value(value, question, survey);
    }
}
