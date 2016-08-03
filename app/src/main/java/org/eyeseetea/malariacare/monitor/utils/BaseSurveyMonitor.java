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

    /**
     * Reference to inner survey
     */
    private Survey survey;

    public BaseSurveyMonitor(Survey survey){
        this.survey=survey;
    }

    /**
     * Returns wrapped survey
     * @return
     */
    public Survey getSurvey(){
        return this.survey;
    }

    public boolean isRated() {
        return (isPositive() || isNegative());
    }

    /**
     * Tells if the given survey is positive
     * @return
     */
    public boolean isPositive(){
        return findValue(SurveyMonitor.ID_QUESTION_RDT,SurveyMonitor.ID_OPTION_RDT_POSITIVE)!=null;
    }

    /**
     * Tells if the given survey is negative
     * @return
     */
    public boolean isNegative(){
        return findValue(SurveyMonitor.ID_QUESTION_RDT,SurveyMonitor.ID_OPTION_RDT_NEGATIVE)!=null;
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
        return findValue(SurveyMonitor.ID_QUESTION_SPECIE, SurveyMonitor.ID_OPTION_SPECIE_PF)!=null;
    }

    /**
     * Tells if the given survey has Pv specie
     * @return
     */
    public boolean isPv(){
        return findValue(SurveyMonitor.ID_QUESTION_SPECIE, SurveyMonitor.ID_OPTION_SPECIE_PV)!=null;
    }

    /**
     * Tells if the given survey has Pf/Pv (mixed)  specie
     * @return
     */
    public boolean isPfPv(){
        return findValue(SurveyMonitor.ID_QUESTION_SPECIE, SurveyMonitor.ID_OPTION_SPECIE_PFPV)!=null;
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
