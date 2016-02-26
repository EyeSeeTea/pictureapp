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


    // TODO: 26/02/16  Review options for treatments since they are not OK
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
     * Id of referral treatment option
     */
    private final static Long ID_OPTION_TREATMENT_REFERRAL=12l;

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
     * Tells if the given survey has Pf treatment
     * @param survey
     * @return
     */
    public static boolean isPf(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PF)!=null;
    }

    /**
     * Tells if the given survey has Pv treatment
     * @param survey
     * @return
     */
    public static boolean isPv(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PV)!=null;
    }

    /**
     * Tells if the given survey has Pf/Pv treatment
     * @param survey
     * @return
     */
    public static boolean isPfPv(Survey survey){
        return findValue(survey.getId_survey(),ID_QUESTION_SPECIE,ID_OPTION_SPECIE_PFPV)!=null;
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
