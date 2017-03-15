package org.eyeseetea.malariacare.data.database.utils; 

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;

import java.util.Date;

 interface IQuestionStrategy {

    boolean isRdtQuestion(String uid);

    boolean isSevereSymtomsQuestion(String uid);

    Question getDynamicStockQuestion();

    Question getTreatmentQuestionForTag(Object tag);

    boolean isTreatmentQuestion(String uid_question);

    boolean isOutStockQuestion(String uid_question);

    boolean isACT6(String uid_question);

    boolean isACT12(String uid_question);

    boolean isACT18(String uid_question);

    boolean isACT24(String uid_question);

    boolean isACT(String uid_question);
    
    boolean isPq(String uid_question);

    boolean isCq(String uid_question);

    boolean isDynamicTreatmentQuestion(String uid_question);

    Question getACT6Question();

    Question getACT12Question();

    Question getACT18Question();

    Question getACT24Question();

    Question getOutOfStockQuestion();

    boolean isStockQuestion(Question question);

    Question getDynamicTreatmentQuestion();

    Question getTreatmentDiagnosisVisibleQuestion();

    Question getStockPqQuestion();

    Question getPqQuestion();

    Question getAlternativePqQuestion();

    boolean isACT24Question(Question question);

    boolean isACT18Question(Question question);

    boolean isACT6Question(Question question);

    boolean isACT12Question(Question question);

    boolean isAgeQuestion(Question question);

    boolean isSexPregnantQuestion(String uid);

    Question getDynamicTreatmentHideQuestion();

    Option getOptionTreatmentYesCode();

}
