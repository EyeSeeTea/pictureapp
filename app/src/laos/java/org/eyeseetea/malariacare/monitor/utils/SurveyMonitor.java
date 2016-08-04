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

/**
 * Decorator that tells if a survey has specific info
 * Created by arrizabalaga on 26/02/16.
 */
public class SurveyMonitor extends BaseSurveyMonitor {

    public SurveyMonitor(Survey survey){
        super(survey);
    }

    /**
     * Id of treatment question
     */
    protected final static Long ID_QUESTION_TREATMENT=11l;

    /**
     * Id of reason question (pregnant, severe, denied, drug)
     */
    private final static Long ID_QUESTION_REASON=2l;

    /**
     * Id of treatment question
     */
    private final static Long ID_QUESTION_SUBMISION=12l;

    /**
     * Id of pv specie option
     */
    final static Long ID_OPTION_SPECIE_PF =10l;
    /**
     * Id of pv specie option
     */
    final static Long ID_OPTION_SPECIE_PV =11l;

    /**
     * Id of pf/pv (mixed) specie option
     */
    final static Long ID_OPTION_SPECIE_PFPV =12l;

    /**
     * Id of referral treatment option
     */
    final static Long ID_OPTION_TREATMENT_REFERRAL=13l;

    /**
     * Id of hospital treatment option
     */
    private final static Long ID_OPTION_HOSPITAL=23l;

    /**
     * Id of severe reason option
     */
    private final static Long ID_OPTION_SEVERE=3l;

    /**
     * Id of pregnant reason option
     */
    private final static Long ID_OPTION_PREGNANT=4l;

    /**
     * Id of refused reason option
     */
    private final static Long ID_OPTION_DENIED=5l;

    /**
     * Id of rdt stockout reason option
     */
    private final static Long ID_OPTION_DRUG=5l;

    /**
     * Id of Combined act treatment option
     */
    private final static Long ID_OPTION_TREATMENT_COMBINEDACT=22l;

    /**
     * Id of ACT6x1 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ACT6X1=17l;

    /**
     * Id of  ACT6x2 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ACT6X2=18l;

    /**
     * Id of ACT6x3 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ACT6X3=19l;

    /**
     * Id of ACT6x4 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ACT6X4=20l;

    /**
     * Id of ACT6x4 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_RDTS=21l;

    public boolean isSuspected(){
        return (isPositive() || isNegative());
    }

    public boolean isSubmission() {
        return findValue(ID_QUESTION_SUBMISION, ID_OPTION_HOSPITAL)!=null;
    }

    public boolean isSevere() {
        return findValue(ID_QUESTION_REASON, ID_OPTION_SEVERE)!=null;
    }


    public boolean isPregnant() {
        return findValue(ID_QUESTION_REASON, ID_OPTION_PREGNANT)!=null;
    }


    public boolean isDenied() {
        return findValue(ID_QUESTION_REASON, ID_OPTION_DENIED)!=null;
    }

    public boolean isDrug() {
        return findValue(ID_QUESTION_REASON, ID_OPTION_DRUG)!=null;
    }


    public boolean isCombinedACT() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_COMBINEDACT);
    }
    public boolean isACT6x4() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ACT6X4);
    }

    public boolean isACT6x3() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ACT6X3);
    }

    public boolean isACT6x2() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ACT6X2);
    }

    public boolean isACT6x1() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ACT6X1);
    }

    /**
     * Tells if the given survey is a RDT survey (positive or negative)
     * @return
     */
    public boolean isRDTs(){
        return findOption(SurveyMonitor.ID_QUESTION_TREATMENT, SurveyMonitor.ID_OPTION_TREATMENT_RDTS);
    }
}
