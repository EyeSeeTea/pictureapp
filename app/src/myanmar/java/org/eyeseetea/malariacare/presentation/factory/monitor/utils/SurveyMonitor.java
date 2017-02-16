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

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;

/**
 * Decorator that tells if a mSurvey has specific info
 * Created by arrizabalaga on 26/02/16.
 */
public class SurveyMonitor {
    /**
     * Id of treatment question
     */
    protected final static Long ID_QUESTION_TREATMENT = 15l;
    /**
     * Id of pq treatment question
     */
    protected final static Long ID_QUESTION_PQ_TREATMENT = 25l;
    /**
     * Id of act12 treatment question
     */
    protected final static Long ID_QUESTION_ACT12_TREATMENT = 24l;
    /**
     * Id of treatment question
     */
    protected final static Long ID_QUESTION_REFERRAL = 23l;
    /**
     * Id of counter question
     */
    protected final static Long ID_QUESTION_COUNTER = 14l;
    /**
     * Id of specie question(test result)
     */
    final static Long ID_QUESTION_RDT_TEST_RESULT = 12l;
    /**
     * Id of negative  of rdt tst result
     */
    final static Long ID_OPTION_TEST_NEGATIVE = 15l;
    /**
     * Id of pv specie option  of rdt tst result
     */
    final static Long ID_OPTION_SPECIE_PF = 16l;
    /**
     * Id of pv specie option  of rdt tst result
     */
    final static Long ID_OPTION_SPECIE_PV = 17l;
    /**
     * Id of pf/pv (mixed) specie option  of rdt tst result
     */
    final static Long ID_OPTION_SPECIE_PFPV = 18l;
    /**
     * Id of negative  of rdt tst result
     */
    final static Long ID_OPTION_INVALID = 19l;
    /**
     * Id of referral yes option  of referral treatment
     */
    final static Long ID_OPTION_REFERRAL_YES = 35l;
    /**
     * Id of pq yes option  of pq treatment
     */
    final static Long ID_OPTION_TREATMENT_PQ = 33l;

    /**
     * Id of Combined act treatment option
     */
    private final static Long ID_OPTION_TREATMENT_REFERER_HOSPITAL = 21l;
    /**
     * Id of ACT6x1 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ACT6 = 17l;
    /**
     * Id of  ACT6x2 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ACT12 = 31l;
    /**
     * Id of ACT6x3 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ACT18 = 19l;
    /**
     * Id of ACT6x4 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ACT24 = 20l;

    /**
     * Reference to inner mSurvey
     */
    private Survey mSurvey;

    public SurveyMonitor(Survey survey) {
        this.mSurvey = survey;
    }

    public Survey getSurvey() {
        return mSurvey;
    }

    /**
     * Tells if the given mSurvey is Rated(all was tested in myanmar).
     */
    public boolean isRated() {
        return isTested();
    }

    /**
     * Tells if the given mSurvey is tested and all are tested
     */
    public boolean isTested() {
        return isPositive() || isNegative();
    }

    /**
     * Tells if the given mSurvey test is negative
     */
    public boolean isNegative() {
        return Value.findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_TEST_NEGATIVE, mSurvey)
                != null;
    }

    /**
     * Tells if the given mSurvey rdt is tested but test is not negative
     */
    public boolean isPositive() {
        return (Value.findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PF, mSurvey) != null
                || Value.findValue(
                ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PV, mSurvey) != null
                || Value.findValue(
                ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PFPV, mSurvey) != null);
    }

    /**
     * Tells if the given mSurvey has Pf specie
     */
    public boolean isPf() {
        return Value.findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PF, mSurvey) != null;
    }

    /**
     * Tells if the given mSurvey has Pv specie
     */
    public boolean isPv() {
        return Value.findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PV, mSurvey) != null;
    }

    /**
     * Tells if the given mSurvey has Pf/Pv (mixed)  specie
     */
    public boolean isPfPv() {
        return Value.findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PFPV, mSurvey) != null;
    }

    /**
     * Tells if the given mSurvey has Pf/Pv (mixed) or Pv  specie
     */
    public boolean isReferral() {
        return (Value.findValue(ID_QUESTION_REFERRAL, ID_OPTION_REFERRAL_YES, mSurvey) != null);
    }

    /**
     * Tells if the given mSurvey is PV or PV+PF or referred to hospital
     */
    public boolean isTreatment() {
        if (Value.findValue(ID_QUESTION_TREATMENT, ID_OPTION_TREATMENT_REFERER_HOSPITAL, mSurvey)
                != null) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Tells if the given mSurvey is referred to hospital
     */
    public boolean isACTStockout() {
        //// TODO: set the correct idQuestion and IDOption
        //return findValue(ID_QUESTION_TREATMENT, ID_OPTION_TREATMENT_REFERER_HOSPITAL) != null;
        return false;
    }

    /**
     * Tells if the given mSurvey treatment is act6x4
     */
    public boolean isACT24() {
        //// TODO: set the correct idQuestion and IDOption

        //return findOption(ID_QUESTION_TREATMENT, ID_OPTION_TREATMENT_ACT24);
        return false;
    }

    /**
     * Tells if the given mSurvey treatment is act6x3
     */
    public boolean isACT18() {
        //// TODO: set the correct idQuestion and IDOption
        //return findOption(ID_QUESTION_TREATMENT, ID_OPTION_TREATMENT_ACT18);
        return false;
    }

    /**
     * Tells if the given mSurvey treatment is act6x2
     */
    public boolean isACT12() {
        return Option.findOption(ID_QUESTION_ACT12_TREATMENT, ID_OPTION_TREATMENT_ACT12, mSurvey);
    }

    /**
     * Tells if the given mSurvey treatment is act6x1
     */
    public boolean isACT6() {
        //// TODO: set the correct idQuestion and IDOption
        //return findOption(ID_QUESTION_TREATMENT, ID_OPTION_TREATMENT_ACT6);
        return false;
    }

    /**
     * Tells if the given mSurvey is a RDT tested
     */
    public boolean isRDTs() {
        //all the sent surveys has rdt test in myanmar
        return true;
    }

    /**
     * Returns the number of rtd tests for each mSurvey
     */
    public Integer countRDT() {
        int testCounter = testCounter();
        if (isInvalid()) {
            return testCounter > 0 ? testCounter : 1;
        } else if (isRDTs()) {
            //the rdt is a test too
            return testCounter + 1;
        } else {
            return 0;
        }
    }

    private boolean isInvalid() {
        return Option.findOption(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_INVALID,mSurvey);
    }

    /**
     * Returns the invalid count rdts for each mSurvey
     */
    public int testCounter() {
        Value value = Value.findValue(ID_QUESTION_COUNTER, mSurvey);
        if (value == null || value.getValue() == null) {
            return 0;
        }
        return Integer.parseInt(value.getValue());
    }


    public boolean isPq() {
        return Option.findOption(ID_QUESTION_PQ_TREATMENT, ID_OPTION_TREATMENT_PQ, mSurvey);
    }


    public boolean isCq() {
        //// TODO: set the correct idQuestion and IDOption
        return false;
    }
}
