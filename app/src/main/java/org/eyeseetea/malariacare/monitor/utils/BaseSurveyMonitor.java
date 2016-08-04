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
 * Created by idelcano on 03/08/2016.
 *
 * BaseSurveyMonitor contains the common SurveyMonitor methods to each variant
 */
public class BaseSurveyMonitor {

    public BaseSurveyMonitor(Survey survey){
        this.survey=survey;
    }

    /**
     * Reference to inner survey
     */
    private Survey survey;

    /**
     * Returns wrapped survey
     * @return
     */
    public Survey getSurvey(){
        return this.survey;
    }


    /**
     * Id of first question (positive, negative, not tested)
     */
    final static Long ID_QUESTION_RDT=1l;

    /**
     * Id of specie question
     */
    final static Long ID_QUESTION_SPECIE=5l;

    /**
     * Id of positive rdt option
     */
    final static Long ID_OPTION_RDT_POSITIVE=1l;

    /**
     * Id of negative rdt option
     */
    final static Long ID_OPTION_RDT_NEGATIVE=2l;


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
        return findValue(SurveyMonitor.ID_QUESTION_TREATMENT,SurveyMonitor.ID_OPTION_TREATMENT_REFERRAL)!=null;
    }

    /**
     * Tells if the given survey has Pf specie
     * @return
     */
    public boolean isPf(){
        return findValue(ID_QUESTION_SPECIE, SurveyMonitor.ID_OPTION_SPECIE_PF)!=null;
    }

    /**
     * Tells if the given survey has Pv specie
     * @return
     */
    public boolean isPv(){
        return findValue(ID_QUESTION_SPECIE, SurveyMonitor.ID_OPTION_SPECIE_PV)!=null;
    }

    /**
     * Tells if the given survey has Pf/Pv (mixed)  specie
     * @return
     */
    public boolean isPfPv(){
        return findValue(ID_QUESTION_SPECIE, SurveyMonitor.ID_OPTION_SPECIE_PFPV)!=null;
    }


    /**
     * Looks for the value with the given question + option
     * @param idQuestion
     * @param idOption
     * @return
     */
    public Value findValue(Long idQuestion, Long idOption){
        for(Value value:survey.getValues()){
            if(value.matchesQuestionOption(idQuestion,idOption)){
                return value;
            }
        }
        //No matches -> null
        return null;
    }

    /**
     * Looks for the value with the given question  is the provided option
     * @param idQuestion
     * @param idOption
     * @return
     */
    public boolean findOption(Long idQuestion, Long idOption){
        Value value = findValue(idQuestion);
        if(value==null){
            return false;
        }

        Long valueIdOption=value.getId_option();
        return idOption.equals(valueIdOption);
    }
    /**
     * Looks for the value with the given question
     * @param idQuestion
     * @return
     */
    public Value findValue(Long idQuestion){
        for(Value value:survey.getValues()){
            if(value.matchesQuestion(idQuestion)){
                return value;
            }
        }
        //No matches -> null
        return null;
    }
}
