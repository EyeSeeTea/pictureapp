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

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Survey$Table;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.model.Value$Table;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.Date;
import java.util.List;

/**
 * Decorator that tells if a survey has specific info
 * Created by arrizabalaga on 26/02/16.
 */
public class SurveyMonitor {

    /**
     * Id of first question (positive, negative, not tested)
     */
    private final static Long ID_QUESTION_RDT=1l;

    /**
     * Id of specie question
     */
    private final static Long ID_QUESTION_SPECIE=4l;

    /**
     * Id of treatment question
     */
    private final static Long ID_QUESTION_TREATMENT=5l;

    /**
     * Id of positive rdt option
     */
    private final static Long ID_OPTION_RDT_POSITIVE=1l;

    /**
     * Id of negative rdt option
     */
    private final static Long ID_OPTION_RDT_NEGATIVE=2l;

    /**
     * Id of not tested rdt option
     */
    private final static Long ID_OPTION_RDT_NOT_TESTED=3l;

    /**
     * Id of pf/pv specie option
     */
    private final static Long ID_OPTION_SPECIE_PFPV=6l;

    /**
     * Id of pf specie option
     */
    private final static Long ID_OPTION_SPECIE_PF=7l;

    /**
     * Id of pf specie option
     */
    private final static Long ID_OPTION_SPECIE_PV=8l;

    /**
     * Id of DHA-PIP1 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP1=9l;

    /**
     * Id of DHA-PIP2 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP2=10l;

    /**
     * Id of DHA-PIP3 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP3=11l;

    /**
     * Id of DHA-PIP3 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP4=12l;

    /**
     * Id of AS/MQ treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ASMQ=13l;

    /**
     * Id of referral treatment option
     */
    private final static Long ID_OPTION_TREATMENT_REFERRAL=14l;

    /**
     * Reference to inner survey
     */
    final Survey survey;

    public SurveyMonitor(Survey survey){
        this.survey=survey;
    }

    /**
     * Returns wrapped survey
     * @return
     */
    public Survey getSurvey(){
        return this.survey;
    }

    /**
     * Tells if the given survey is suspected (positive, negative or not tested.
     * @return
     */
    public boolean isSuspected(){
        return (isPositive() || isNegative() || isNotTested());
    }

    /**
     * Tells if the given survey is positive
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
        return findValue(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_REFERRAL)!=null;
    }

    /**
     * Tells if the given survey has Pf specie
     * @return
     */
    public boolean isPf(){
        return findValue(ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PF)!=null;
    }

    /**
     * Tells if the given survey has Pv specie
     * @return
     */
    public boolean isPv(){
        return findValue(ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PV)!=null;
    }

    /**
     * Tells if the given survey has Pf/Pv specie
     * @return
     */
    public boolean isPfPv(){
        return findValue(ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PFPV)!=null;
    }

    /**
     * Tells if the given survey has AS/MQ treatment
     * @return
     */
    public boolean isASMQ(){
        return findValue(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ASMQ)!=null;
    }

    /**
     * Tells if the given survey has DHA-PIP 1 treatment
     * @return
     */
    public boolean isDHAPIP2(){
        return findValue(ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_DHA_PIP2)!=null;
    }

    /**
     * Tells if the given survey has DHA-PIP1 or 4 (child|bigadult) treatment
     * @return
     */
    public boolean isDHAPIP1Or4(){
        Value value = findValue(ID_QUESTION_TREATMENT);
        if(value==null){
            return false;
        }

        Long idOption=value.getId_option();
        return ID_OPTION_TREATMENT_DHA_PIP1.equals(idOption) || ID_OPTION_TREATMENT_DHA_PIP4.equals(idOption);
    }

    /**
     * Tells if the given survey has DHA-PIP3 or 4 (adult|bigadult) treatment
     * @return
     */
    public boolean isDHAPIP3Or4(){
        Value value = findValue(ID_QUESTION_TREATMENT);
        if(value==null){
            return false;
        }

        Long idOption=value.getId_option();
        return ID_OPTION_TREATMENT_DHA_PIP3.equals(idOption) || ID_OPTION_TREATMENT_DHA_PIP4.equals(idOption);
    }

    /**
     * Tells if the given survey is a RDT survey (positive or negative)
     * @return
     */
    public boolean isRDT(){
        Value value = findValue(ID_QUESTION_RDT);
        if(value==null){
            return false;
        }
        Long idOption=value.getId_option();
        //Positive and Negative cases are RDT cases
        return ID_OPTION_RDT_POSITIVE.equals(idOption) || ID_OPTION_RDT_NEGATIVE.equals(idOption);
    }

    /**
     * Looks for the value with the given question + option
     * @param idQuestion
     * @param idOption
     * @return
     */
    private Value findValue(Long idQuestion, Long idOption){
        for(Value value:this.survey.getValues()){
            if(value.matchesQuestionOption(idQuestion,idOption)){
                return value;
            }
        }
        //No matches -> null
        return null;
    }

    /**
     * Looks for the value with the given question
     * @param idQuestion
     * @return
     */
    private Value findValue(Long idQuestion){
        for(Value value:this.survey.getValues()){
            if(value.matchesQuestion(idQuestion)){
                return value;
            }
        }
        //No matches -> null
        return null;
    }

    /**
     * Returns the surveys that have been sent during the last 6 months in order to create monitor stats on top of them.
     * @return
     */
    public static List<Survey> findSentSurveysForMonitor() {
        Date minDateForMonitor = TimePeriodCalculator.getInstance().getMinDateForMonitor();
        return Survey.findSentSurveysAfterDate(minDateForMonitor);
    }

}
