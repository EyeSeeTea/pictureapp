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
 * Decorator that tells if a survey has specific info
 * Created by arrizabalaga on 26/02/16.
 */
public class SurveyMonitor extends BaseSurveyMonitor{

    public SurveyMonitor(Survey survey){
        super(survey);
    }

    /**
     * Id of treatment question
     */
    final static Long ID_QUESTION_TREATMENT=6l;

    /**
     * Id of negative rdt option
     */
    final static Long ID_OPTION_RDT_NEGATIVE=2l;

    /**
     * Id of not tested rdt option
     */
    private final static Long ID_OPTION_RDT_NOT_TESTED=3l;

    /**
     * Id of pv specie option
     */
    final static Long ID_OPTION_SPECIE_PF =9l;
    /**
     * Id of pv specie option
     */
    final static Long ID_OPTION_SPECIE_PV =10l;

    /**
     * Id of pf/pv (mixed) specie option
     */
    final static Long ID_OPTION_SPECIE_PFPV =11l;

    /**
     * Id of referral treatment option
     */
    final static Long ID_OPTION_TREATMENT_REFERRAL=14l;

    /**
     * Id of ASMQ treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ASMQ =13l;

    /**
     * Id of DHA-PIP treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP=12l;


    public boolean isRated() {
        return (isPositive() || isNegative());
    }

    /**
     * Tells if the given survey test  is positive
     * @return
     */
    public boolean isPositive(){
        return findValue(ID_QUESTION_RDT,ID_OPTION_RDT_POSITIVE)!=null;
    }
    /**
     * Tells if the given survey is negative
     * @return
     */
    public boolean isNegative(){
        return findValue(ID_QUESTION_RDT,ID_OPTION_RDT_NEGATIVE)!=null;
    }

    /**
     * Tells if the given survey is suspected (positive, negative or not tested).
     * @return
     */
    public boolean isSuspected(){
        return (isPositive() || isNegative() || isNotTested());
    }

    /**
     * Tells if the given survey is not tested
     * @return
     */
    public boolean isNotTested(){
        return findValue(ID_QUESTION_RDT,ID_OPTION_RDT_NOT_TESTED)!=null;
    }

    /**
     * Tells if the given survey is referral
     * @return
     */
    public boolean isReferral(){
        return findValue(SurveyMonitor.ID_QUESTION_TREATMENT,SurveyMonitor.ID_OPTION_TREATMENT_REFERRAL)!=null;
    }
    /**
     * Tells if the given survey has DHA-PIP treatment
     * @return
     */
    public boolean isDHAPIP(){
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_DHA_PIP);
    }

    /**
     * Tells if the given survey has Eurartesim treatment
     * @return
     */
    public boolean isASMQ(){
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ASMQ);
    }

    /**
     * Tells if the given survey is a RDT survey (positive or negative)
     * @return
     */
    public boolean isRDT(){
        return findOption(ID_QUESTION_RDT,ID_OPTION_RDT_POSITIVE) ||findOption(ID_QUESTION_RDT,ID_OPTION_RDT_NEGATIVE);
    }
}
