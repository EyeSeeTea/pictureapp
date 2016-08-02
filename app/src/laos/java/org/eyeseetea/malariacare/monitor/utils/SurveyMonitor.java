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
     * Id of reason question (pregnant, severe, denied, drug)
     */
    private final static Long ID_QUESTION_REASON=2l;

    /**
     * Id of specie question
     */
    private final static Long ID_QUESTION_SPECIE=5l;

    /**
     * Id of treatment question
     */
    private final static Long ID_QUESTION_TREATMENT=11l;

    /**
     * Id of treatment question
     */
    private final static Long ID_QUESTION_SUBMISION=12l;

    /**
     * Id of positive rdt option
     */
    private final static Long ID_OPTION_RDT_POSITIVE=1l;

    /**
     * Id of negative rdt option
     */
    private final static Long ID_OPTION_RDT_NEGATIVE=2l;

    /**
     * Id of pv specie option
     */
    private final static Long ID_OPTION_SPECIE_PF =10l;
    /**
     * Id of pv specie option
     */
    private final static Long ID_OPTION_SPECIE_PV =11l;

    /**
     * Id of pf/pv (mixed) specie option
     */
    private final static Long ID_OPTION_SPECIE_PFPV =12l;

    /**
     * Id of referral treatment option
     */
    private final static Long ID_OPTION_TREATMENT_REFERRAL=13l;

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
     * Tells if the given survey is suspected (positive, negative).
     * @return
     */
    public boolean isSuspected(){
        return (isPositive() || isNegative());
    }

    /**
     * Tells if the given survey is used in Posivility stats (positive and negative).
     * @return
     */
    public boolean isRated() {
        return (isPositive() || isNegative());
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
        return findValue(ID_QUESTION_SPECIE, ID_OPTION_SPECIE_PF)!=null;
    }

    /**
     * Tells if the given survey has Pv specie
     * @return
     */
    public boolean isPv(){
        return findValue(ID_QUESTION_SPECIE, ID_OPTION_SPECIE_PV)!=null;
    }

    /**
     * Tells if the given survey has Pf/Pv (mixed)  specie
     * @return
     */
    public boolean isPfPv(){
        return findValue(ID_QUESTION_SPECIE, ID_OPTION_SPECIE_PFPV)!=null;
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
        Value value = findValue(ID_QUESTION_TREATMENT);
        if(value==null){
            return false;
        }

        Long idOption=value.getId_option();
        return ID_OPTION_TREATMENT_COMBINEDACT.equals(idOption);
    }

    public boolean isACT6x4() {
        Value value = findValue(ID_QUESTION_TREATMENT);
        if(value==null){
            return false;
        }

        Long idOption=value.getId_option();
        return ID_OPTION_TREATMENT_ACT6X4.equals(idOption);
    }

    public boolean isACT6x3() {
        Value value = findValue(ID_QUESTION_TREATMENT);
        if(value==null){
            return false;
        }

        Long idOption=value.getId_option();
        return ID_OPTION_TREATMENT_ACT6X3.equals(idOption);
    }

    public boolean isACT6x2() {
        Value value = findValue(ID_QUESTION_TREATMENT);
        if(value==null){
            return false;
        }

        Long idOption=value.getId_option();
        return ID_OPTION_TREATMENT_ACT6X2.equals(idOption);
    }

    public boolean isACT6x1() {
        Value value = findValue(ID_QUESTION_TREATMENT);
        if(value==null){
            return false;
        }

        Long idOption=value.getId_option();
        return ID_OPTION_TREATMENT_ACT6X1.equals(idOption);
    }

    /**
     * Tells if the given survey is a RDT survey (positive or negative)
     * @return
     */
    public boolean isRDT(){
        Value value = findValue(ID_QUESTION_TREATMENT);
        if(value==null){
            return false;
        }

        Long idOption=value.getId_option();
        return ID_OPTION_TREATMENT_RDTS.equals(idOption);
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
