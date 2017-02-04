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

package org.eyeseetea.malariacare.database.utils;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
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

    public static int readPositionOption(Question question) {
        int result = 0;

        Value value = question.getValueBySession();
        if (value != null) {

            List<Option> optionList = question.getAnswer().getOptions();
            optionList.add(0, new Option(Constants.DEFAULT_SELECT_OPTION));
            result = optionList.indexOf(value.getOption());
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

    public static void saveValuesDDL(Question question, Option option, Value value) {
        //No option, nothing to save
        if (option == null) {
            return;
        }
        if (question.isTreatmentQuestion() && value != null
                && !option.getId_option().equals(value.getId_option())) {
            deleteStockSurveyValues();
        }

        if (!option.getName().equals(Constants.DEFAULT_SELECT_OPTION)) {
            Survey survey = (question.isStockQuestion() ? Session.getStockSurvey()
                    : Session.getMalariaSurvey());
            if (value == null) {
                value = new Value(option, question, survey);
            } else {
                if (!value.getOption().equals(option) && question.hasChildren()) {
                    survey.removeChildrenValuesFromQuestionRecursively(question, false);
                }
                value.setOption(option);
                value.setValue(option.getName());
            }
            value.save();
        } else {
            if (value != null) value.delete();
        }
    }

    public static void saveValuesText(Question question, String answer) {
        Value value = question.getValueBySession();
        Survey survey = (question.isStockQuestion() ? Session.getStockSurvey()
                : Session.getMalariaSurvey());
        if (question.isTreatmentQuestion() && value != null && !value.getValue().equals(answer)) {
            deleteStockSurveyValues();
        }
        if (question.isStockQuestion() && value != null && answer.equals("-1")) {
            value.delete();
        } else {
            // If the value is not found we create one
            if (value == null) {
                value = new Value(answer, question, survey);
            } else {
                value.setOption((Long) null);
                value.setValue(answer);
            }
            value.save();
        }
    }

    public static void deleteValue(Question question) {

        Value value = question.getValueBySession();

        if (value != null) {
            value.delete();
        }
    }

    public static void deleteStockSurveyValues() {
        Survey survey = Session.getStockSurvey();
        List<Value> stockValues = survey.getValues();
        for (Value value : stockValues) {
            value.delete();
        }
        Survey malariaSurvey = Session.getMalariaSurvey();
        List<Value> malariaValues = malariaSurvey.getValues();
        for (Value value : malariaValues) {
            if (value.getQuestion().isOutStockQuestion()) {
                value.delete();
            }
        }
    }
}
