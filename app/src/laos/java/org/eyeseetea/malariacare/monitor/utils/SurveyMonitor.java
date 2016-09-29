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
     * Id of first question is tested? (positive, negative, not tested)
     */
    final static Long ID_QUESTION_IS_RDT_TESTED =1l;
    /**
     * Id of not tested
     */
    final static Long ID_OPTION_RDT_YES_TESTED =1l;
    /**
     * Id of not tested
     */
    final static Long ID_OPTION_RDT_NOT_TESTED =2l;


    /**
     * Id of reason question (pregnant, severe, denied, drug)
     */
    private final static Long ID_QUESTION_REASON=2l;

    /**
     * Id of rdt stockout reason option
     */
    private final static Long ID_OPTION_RDT_STOCKOUT =6l;


    /**
     * Id of specie question(test result)
     */
    final static Long ID_QUESTION_RDT_TEST_RESULT =5l;
    /**
     * Id of negative  of rdt tst result
     */
    final static Long ID_OPTION_TEST_NEGATIVE =9l;
    /**
     * Id of pv specie option  of rdt tst result
     */
    final static Long ID_OPTION_SPECIE_PF =10l;
    /**
     * Id of pv specie option  of rdt tst result
     */
    final static Long ID_OPTION_SPECIE_PV =11l;

    /**
     * Id of pf/pv (mixed) specie option  of rdt tst result
     */
    final static Long ID_OPTION_SPECIE_PFPV =12l;



    /**
     * Id of treatment question
     */
    protected final static Long ID_QUESTION_TREATMENT=11l;
    /**
     * Id of Combined act treatment option
     */
    private final static Long ID_OPTION_TREATMENT_REFERER_HOSPITAL=21l;
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
     * Id of counter question
     */
    protected final static Long ID_QUESTION_COUNTER=6l;

    /**
     * Tells if the given survey is tested
     * @return
     */
    public boolean isSuspected(){
        return (isTested() || isNotTested());
    }

    /**
     * Tells if the given survey is not tested
     * @return
     */
    public boolean isNotTested(){
        return findValue(ID_QUESTION_IS_RDT_TESTED,ID_OPTION_RDT_NOT_TESTED)!=null;
    }
    /**
     * Tells if the given survey is tested
     * @return
     */
    public boolean isTested(){
        return findValue(ID_QUESTION_IS_RDT_TESTED, ID_OPTION_RDT_YES_TESTED)!=null;
    }
    /**
     * Tells if the given survey is Rated(the same of is tested in Lao).
     * @return
     */
    public boolean isRated(){
        return isTested();
    }
    /**
     * Tells if the given survey test is negative
     * @return
     */
    public boolean isNegative(){
        return findValue(ID_QUESTION_RDT_TEST_RESULT,ID_OPTION_TEST_NEGATIVE)!=null;
    }
    /**
     * Tells if the given survey rdt is tested but test is not negative
     * @return
     */
    public boolean isPositive(){
        return (findValue(ID_QUESTION_RDT_TEST_RESULT,ID_OPTION_SPECIE_PF)!=null || findValue(ID_QUESTION_RDT_TEST_RESULT,ID_OPTION_SPECIE_PV)!=null || findValue(ID_QUESTION_RDT_TEST_RESULT,ID_OPTION_SPECIE_PFPV)!=null );
    }

    /**
     * Tells if the given survey has Pf specie
     * @return
     */
    public boolean isPf(){
        return findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PF)!=null;
    }

    /**
     * Tells if the given survey has Pv specie
     * @return
     */
    public boolean isPv(){
        return findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PV)!=null;
    }

    /**
     * Tells if the given survey has Pf/Pv (mixed)  specie
     * @return
     */
    public boolean isPfPv(){
        return findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PFPV)!=null;
    }
    /**
     * Tells if the given survey has Pf/Pv (mixed) or Pv  specie
     * @return
     */
    public boolean isReferral(){
        return (findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PFPV)!=null || findValue(ID_QUESTION_RDT_TEST_RESULT, ID_OPTION_SPECIE_PV)!=null) ;
    }
    /**
     * Tells if the given survey is not tested number of referrals(RDT testing)
     * @return
     */
    public boolean isRDTTesting() {
        return findValue(ID_QUESTION_IS_RDT_TESTED, ID_OPTION_RDT_NOT_TESTED)!=null;
    }

    /**
     * Tells if the given survey is PV or PV+PF or referred to hospital
     * @return
     */
    public boolean isTreatment() {
        if(isReferral() || findValue(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_REFERER_HOSPITAL)!=null) {
             return true;
        }
        else {
            return false;
        }
    }


    /**
     * Tells if the given survey is referred to hospital
     * @return
     */
    public boolean isACTStockout() {
        return findValue(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_REFERER_HOSPITAL)!=null;
    }

    /**
     * Tells if the given survey not tested by stockout
     * @return
     */
    public boolean isRDTStockout() {
        return findValue(ID_QUESTION_REASON, ID_OPTION_RDT_STOCKOUT)!=null;
    }
    /**
     * Tells if the given survey treatment is act6x4
     * @return
     */
    public boolean isACT6x4() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ACT6X4);
    }

    /**
     * Tells if the given survey treatment is act6x3
     * @return
     */
    public boolean isACT6x3() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ACT6X3);
    }

    /**
     * Tells if the given survey treatment is act6x2
     * @return
     */
    public boolean isACT6x2() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ACT6X2);
    }

    /**
     * Tells if the given survey treatment is act6x1
     * @return
     */
    public boolean isACT6x1() {
        return findOption(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ACT6X1);
    }

    /**
     * Tells if the given survey is a RDT tested
     * @return
     */
    public boolean isRDTs(){
        return findOption(ID_QUESTION_IS_RDT_TESTED, ID_OPTION_RDT_YES_TESTED);
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
        Value value = findValue(ID_QUESTION_COUNTER);
        if(value==null || value.getValue()==null){
            return 0;
        }
        return Integer.parseInt(value.getValue());
    }


}
