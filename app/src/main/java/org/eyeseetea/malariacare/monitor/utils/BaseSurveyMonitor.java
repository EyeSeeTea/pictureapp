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
package org.eyeseetea.malariacare.monitor.utils;

import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;

/**
 * Created by idelcano on 03/08/2016.
 *
 * BaseSurveyMonitor contains the common SurveyMonitor methods to each variant
 */
public class BaseSurveyMonitor {

    /**
     * Reference to inner survey
     */
    private Survey survey;

    public BaseSurveyMonitor(Survey survey) {
        this.survey = survey;
    }

    /**
     * Returns wrapped survey
     */
    public Survey getSurvey() {
        return this.survey;
    }

    /**
     * Looks for the value with the given question + option
     */
    public Value findValue(Long idQuestion, Long idOption) {
        for (Value value : survey.getValues()) {
            if (value.matchesQuestionOption(idQuestion, idOption)) {
                return value;
            }
        }
        //No matches -> null
        return null;
    }

    /**
     * Looks for the value with the given question  is the provided option
     */
    public boolean findOption(Long idQuestion, Long idOption) {
        Value value = findValue(idQuestion);
        if (value == null) {
            return false;
        }

        Long valueIdOption = value.getId_option();
        return idOption.equals(valueIdOption);
    }

    /**
     * Looks for the value with the given question
     */
    public Value findValue(Long idQuestion) {
        for (Value value : survey.getValues()) {
            if (value.matchesQuestion(idQuestion)) {
                return value;
            }
        }
        //No matches -> null
        return null;
    }
}
