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
public class SurveyMonitor extends BaseSurveyMonitor {

    public SurveyMonitor(Survey survey){
        super(survey);
    }

    /**
     * Id of treatment question
     */
    protected final static Long ID_QUESTION_TREATMENT=11l;
    /**
     * Id of counter question
     */
    protected final static Long ID_QUESTION_COUNTER=6l;

    /**
     * Id of reason question (pregnant, severe, denied, drug)
     */
    private final static Long ID_QUESTION_REASON=2l;

    /**
     * Id of not tested
     */
    final static Long ID_OPTION_RDT_NOT_TESTED =2l;
    /**
     * Id of negative  specie option
     */
    final static Long ID_OPTION_TEST_NEGATIVE =9l;
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
    private final static Long ID_OPTION_RDT_STOCKOUT =5l;

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
     * Tells if the given survey is tested
     * @return
     */
    public boolean isSuspected(){
        return (isPositive() || isNegative());
    }

    /**
     * Tells if the given survey is tested
     * @return
     */
    public boolean isRated() {
        return findValue(ID_QUESTION_RDT,ID_OPTION_RDT_POSITIVE)!=null;
    }

    /**
     * Tells if the given survey is not tested
     * @return
     */
    public boolean isNotTested(){
        return findValue(ID_QUESTION_RDT,ID_OPTION_RDT_NOT_TESTED)!=null;
    }
    /**
     * Tells if the given survey is negative
     * @return
     */
    public boolean isNegative(){
        return findValue(ID_QUESTION_SPECIE,ID_OPTION_TEST_NEGATIVE)!=null;
    }
    /**
     * Tells if the given survey is positive
     * @return
     */
    public boolean isPositive(){
        return (findValue(ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PF)!=null || findValue(ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PV)!=null || findValue(ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PFPV)!=null );
    }
    /**
     * Tells if the given survey has Pf/Pv (mixed) or Pv  specie
     * @return
     */
    public boolean isReferral(){
        return (findValue(ID_QUESTION_SPECIE, SurveyMonitor.ID_OPTION_SPECIE_PFPV)!=null || findValue(ID_QUESTION_SPECIE, SurveyMonitor.ID_OPTION_SPECIE_PV)!=null) ;
    }
    /**
     * Tells if the given survey is not tested
     * @return
     */
    public boolean isSubmission() {
        return findValue(ID_QUESTION_RDT, ID_OPTION_RDT_NOT_TESTED)!=null;
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

    public boolean isRDTStockout() {
        return findValue(ID_QUESTION_REASON, ID_OPTION_RDT_STOCKOUT)!=null;
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
     * Tells if the given survey is a RDT positive/negative
     * @return
     */
    public boolean isRDTs(){
        return findOption(SurveyMonitor.ID_QUESTION_RDT, SurveyMonitor.ID_OPTION_RDT_POSITIVE);
    }
    /**
     * Returns the number of rtd tests for each survey
     * @return
     */
    public Integer countRDT(){
        if(isRDTs()){
            return testCounter()+1;
        }
        else return 0;
    }
    /**
     * Returns the invalid count rdts for each survey
     * @return
     */
    public int testCounter() {
        Value value = findValue(SurveyMonitor.ID_QUESTION_COUNTER);
        if(value==null || value.getValue()==null){
            return 0;
        }
        return Integer.parseInt(value.getValue());
    }


}
