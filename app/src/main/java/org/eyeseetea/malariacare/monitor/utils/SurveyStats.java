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

/**
 * Helper that tells if a survey has specific info
 * Created by arrizabalaga on 26/02/16.
 */
public class SurveyStats {

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
     * Id of AS/MQ treatment option
     */
    private final static Long ID_OPTION_TREATMENT_ASMQ=9l;

    /**
     * Id of DHA-PIP1 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP1=10l;

    /**
     * Id of DHA-PIP2 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP2=11l;

    /**
     * Id of DHA-PIP3 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP3=12l;

    /**
     * Id of DHA-PIP3 treatment option
     */
    private final static Long ID_OPTION_TREATMENT_DHA_PIP4=13l;

    /**
     * Id of referral treatment option
     */
    private final static Long ID_OPTION_TREATMENT_REFERRAL=14l;

    /**
     * Tells if the given survey is suspected (positive, negative or not tested)
     * @param survey
     * @return
     */
    public static boolean isSuspected(Survey survey){
        //Every sent survey is suspected
        return true;
    }

    /**
     * Tells if the given survey is positive
     * @param survey
     * @return
     */
    public static boolean isPositive(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_RDT,ID_OPTION_RDT_POSITIVE)!=null;
    }

    /**
     * Tells if the given survey is negative
     * @param survey
     * @return
     */
    public static boolean isNegative(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_RDT,ID_OPTION_RDT_NEGATIVE)!=null;
    }

    /**
     * Tells if the given survey is not tested
     * @param survey
     * @return
     */
    public static boolean isNotTested(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_RDT,ID_OPTION_RDT_NOT_TESTED)!=null;
    }

    /**
     * Tells if the given survey is referral
     * @param survey
     * @return
     */
    public static boolean isReferral(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_REFERRAL)!=null;
    }

    /**
     * Tells if the given survey has Pf specie
     * @param survey
     * @return
     */
    public static boolean isPf(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PF)!=null;
    }

    /**
     * Tells if the given survey has Pv specie
     * @param survey
     * @return
     */
    public static boolean isPv(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PV)!=null;
    }

    /**
     * Tells if the given survey has Pf/Pv specie
     * @param survey
     * @return
     */
    public static boolean isPfPv(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PFPV)!=null;
    }

    /**
     * Tells if the given survey has AS/MQ treatment
     * @param survey
     * @return
     */
    public static boolean isASMQ(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_ASMQ)!=null;
    }

    /**
     * Tells if the given survey has DHA-PIP 1 treatment
     * @param survey
     * @return
     */
    public static boolean isDHAPIP1(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_DHA_PIP1)!=null;
    }

    /**
     * Tells if the given survey has DHA-PIP 1 treatment
     * @param survey
     * @return
     */
    public static boolean isDHAPIP2(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_TREATMENT,ID_OPTION_TREATMENT_DHA_PIP2)!=null;
    }

    /**
     * Tells if the given survey has DHA-PIP 1 treatment
     * @param survey
     * @return
     */
    public static boolean isDHAPIP3(Survey survey){
        return findValue(survey.getId_survey(), ID_QUESTION_TREATMENT, ID_OPTION_TREATMENT_DHA_PIP3)!=null;
    }

    /**
     * Tells if the given survey has DHA-PIP1 or 4 (child|bigadult) treatment
     * @param survey
     * @return
     */
    public static boolean isDHAPIP1Or4(Survey survey){
        Value value=new Select().from(Value.class)
                .where(Condition.column(Value$Table.ID_SURVEY).eq(survey.getId_survey()))
                .and(Condition.column(Value$Table.ID_QUESTION).eq(ID_QUESTION_TREATMENT))
                .querySingle();

        //No value (not tested or negative) -> false
        if(value==null){
            return false;
        }

        //Positive (with value) -> check options
        Long idOption=value.getId_option();
        //Positive and Negative cases are RDT cases
        return ID_OPTION_TREATMENT_DHA_PIP1.equals(idOption) || ID_OPTION_TREATMENT_DHA_PIP4.equals(idOption);
    }

    /**
     * Tells if the given survey has DHA-PIP3 or 4 (adult|bigadult) treatment
     * @param survey
     * @return
     */
    public static boolean isDHAPIP3Or4(Survey survey){
        Value value=new Select().from(Value.class)
                .where(Condition.column(Value$Table.ID_SURVEY).eq(survey.getId_survey()))
                .and(Condition.column(Value$Table.ID_QUESTION).eq(ID_QUESTION_TREATMENT))
                .querySingle();
        //No value (not tested or negative) -> false
        if(value==null){
            return false;
        }

        //Positive (with value) -> check options
        Long idOption=value.getId_option();
        //Positive and Negative cases are RDT cases
        return ID_OPTION_TREATMENT_DHA_PIP3.equals(idOption) || ID_OPTION_TREATMENT_DHA_PIP4.equals(idOption);
    }

    /**
     * Tells if the given survey is a RDT survey (positive or negative)
     * @param survey
     * @return
     */
    public static boolean isRDT(Survey survey){
        Value value=new Select().from(Value.class)
                .where(Condition.column(Value$Table.ID_SURVEY).eq(survey.getId_survey()))
                .and(Condition.column(Value$Table.ID_QUESTION).eq(ID_QUESTION_RDT))
                .querySingle();
        Long idOption=value.getId_option();
        //Positive and Negative cases are RDT cases
        return ID_OPTION_RDT_POSITIVE.equals(idOption) || ID_OPTION_RDT_NEGATIVE.equals(idOption);
    }

    /**
     * Finds a value for the given survey, question and option
     * @param idSurvey
     * @param idQuestion
     * @param idOption
     * @return
     */
    private static Value findValue(Long idSurvey,Long idQuestion,Long idOption){
        return new Select().from(Value.class)
                .where(Condition.column(Value$Table.ID_SURVEY).eq(idSurvey))
                .and(Condition.column(Value$Table.ID_QUESTION).eq(idQuestion))
                .and(Condition.column(Value$Table.ID_OPTION).eq(idOption))
                .querySingle();
    }
}
