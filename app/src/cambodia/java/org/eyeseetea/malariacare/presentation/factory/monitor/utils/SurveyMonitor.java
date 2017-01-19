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
package org.eyeseetea.malariacare.presentation.factory.monitor.utils;

import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;

/**
 * Decorator that tells if a survey has specific info
 * Created by arrizabalaga on 26/02/16.
 */
public class SurveyMonitor {

    /**
     * Id of first question (positive, negative, not tested)
     */
    final static Long ID_QUESTION_RDT = 1l;
    /**
     * Id of positive rdt option of question rdt
     */
    final static Long ID_OPTION_RDT_POSITIVE = 1l;
    /**
     * Id of negative rdt option of question rdt
     */
    final static Long ID_OPTION_RDT_NEGATIVE = 2l;
    /**
     * Id of specie question(test result)
     */
    final static Long ID_QUESTION_SPECIE = 5l;
    /**
     * Id of pv specie option of question specie
     */
    final static Long ID_OPTION_SPECIE_PF = 9l;
    /**
     * Id of pv specie option of question specie
     */
    final static Long ID_OPTION_SPECIE_PV = 10l;
    /**
     * Id of pf/pv (mixed) specie option of question specie
     */
    final static Long ID_OPTION_SPECIE_PFPV = 11l;
    /**
     * Id of treatment question
     */
    final static Long ID_QUESTION_TREATMENT = 6l;
    /**
     * Id of referral treatment optionof treatment question
     */
    final static Long ID_OPTION_TREATMENT_REFERRAL = 14l;
    /**
     * Id of not tested rdt option of question specie
     */
    private final static Long ID_OPTION_RDT_NOT_TESTED = 3l;
    /**
     * Id of ASMQ treatment optionof treatment question
     */
    private final static Long ID_OPTION_TREATMENT_ASMQ = 13l;
    /**
     * Id of DHA-PIP treatment option of treatment question
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP = 12l;

    private Survey mSurvey;

    public SurveyMonitor(Survey survey) {
        mSurvey = survey;
    }


    public Survey getSurvey() {
        return mSurvey;
    }

    /**
     * Tells if the given survey has Pf specie
     */
    public boolean isPf() {
        return Value.findValue(ID_QUESTION_SPECIE, ID_OPTION_SPECIE_PF, mSurvey) != null;
    }

    /**
     * Tells if the given survey has Pv specie
     */
    public boolean isPv() {
        return Value.findValue(ID_QUESTION_SPECIE, ID_OPTION_SPECIE_PV, mSurvey) != null;
    }

    /**
     * Tells if the given survey has Pf/Pv (mixed)  specie
     */
    public boolean isPfPv() {
        return Value.findValue(ID_QUESTION_SPECIE, ID_OPTION_SPECIE_PFPV, mSurvey) != null;
    }

    public boolean isRated() {
        return (isPositive() || isNegative());
    }

    /**
     * Tells if the given survey test  is positive
     */
    public boolean isPositive() {
        return Value.findValue(ID_QUESTION_RDT, ID_OPTION_RDT_POSITIVE, mSurvey) != null;
    }

    /**
     * Tells if the given survey is negative
     */
    public boolean isNegative() {
        return Value.findValue(ID_QUESTION_RDT, ID_OPTION_RDT_NEGATIVE, mSurvey) != null;
    }

    /**
     * Tells if the given survey is suspected (positive, negative or not tested).
     */
    public boolean isSuspected() {
        return (isPositive() || isNegative() || isNotTested());
    }

    /**
     * Tells if the given survey is not tested
     */
    public boolean isNotTested() {
        return Value.findValue(ID_QUESTION_RDT, ID_OPTION_RDT_NOT_TESTED, mSurvey) != null;
    }

    /**
     * Tells if the given survey is referral
     */
    public boolean isReferral() {
        return Value.findValue(SurveyMonitor.ID_QUESTION_TREATMENT,
                SurveyMonitor.ID_OPTION_TREATMENT_REFERRAL, mSurvey) != null;
    }

    /**
     * Tells if the given survey has DHA-PIP treatment
     */
    public boolean isDHAPIP() {
        return Option.findOption(ID_QUESTION_TREATMENT, ID_OPTION_TREATMENT_DHA_PIP, mSurvey);
    }

    /**
     * Tells if the given survey has Eurartesim treatment
     */
    public boolean isASMQ() {
        return Option.findOption(ID_QUESTION_TREATMENT, ID_OPTION_TREATMENT_ASMQ, mSurvey);
    }

    /**
     * Tells if the given survey is a RDT survey (positive or negative)
     */
    public boolean isRDT() {
        return Option.findOption(ID_QUESTION_RDT, ID_OPTION_RDT_POSITIVE, mSurvey)
                || Option.findOption(ID_QUESTION_RDT,
                ID_OPTION_RDT_NEGATIVE, mSurvey);
    }
}
