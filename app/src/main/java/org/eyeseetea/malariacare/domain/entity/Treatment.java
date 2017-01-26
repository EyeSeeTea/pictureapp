package org.eyeseetea.malariacare.domain.entity;

import android.util.Log;

import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Drug;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Treatment {

    private static final String TAG = ".Treatment";
    private Survey mMalariaSurvey;
    private Survey mStockSurvey;
    private List<Question> mQuestions;
    private org.eyeseetea.malariacare.database.model.Treatment mTreatment;
    private HashMap<Long, Integer> doseByQuestion;
    public static final Long ID_OP_ACT6 = 101L, ID_OP_ACT12 = 102L, ID_OP_ACT18 = 103L,
            ID_OP_ACT24 = 104L, ID_OP_OUT_STOCK = 105L;
    private HashMap<Long, Float> optionDose;

    public Treatment(Survey malariaSurvey, Survey stockSurvey) {
        mMalariaSurvey = malariaSurvey;
        mStockSurvey = stockSurvey;
        doseByQuestion = new HashMap<>();
    }

    public boolean hasTreatment() {
        mTreatment = getTreatmentFromSurvey();
        if (mTreatment != null) {
            mQuestions = getQuestionsForTreatment(mTreatment);
        }
        return mTreatment != null;
    }



    public List<Question> getQuestions() {
        return mQuestions;
    }

    public HashMap<Long, Integer> getDoseByQuestion() {
        return doseByQuestion;
    }

    public HashMap<Long, Float> getOptionDose(Question question) {
        optionDose = new HashMap<>();
        if (isACT6Question(question)) {
            optionDose.put(ID_OP_ACT12, 0.5f);
            optionDose.put(ID_OP_ACT18, 0.3f);
            optionDose.put(ID_OP_ACT24, 0.25f);
        }
        if (isACT12Question(question)) {
            optionDose.put(ID_OP_ACT6, 2f);
            optionDose.put(ID_OP_ACT18, 0.6f);
            optionDose.put(ID_OP_ACT24, 0.5f);
        }
        if (isACT18Question(question)) {
            optionDose.put(ID_OP_ACT6, 3f);
            optionDose.put(ID_OP_ACT12, 1.5f);
            optionDose.put(ID_OP_ACT24, 0.75f);
        }
        if (isACT24Question(question)) {
            optionDose.put(ID_OP_ACT6, 4f);
            optionDose.put(ID_OP_ACT12, 2f);
            optionDose.put(ID_OP_ACT18, 1.3f);
        }

        return optionDose;
    }

    public org.eyeseetea.malariacare.database.model.Treatment getTreatment() {
        return mTreatment;
    }

    public Question getACTQuestionAnsweredNo() {
        List<Question> questions = mStockSurvey.getQuestionsFromValues();
        Question actQuestion = null;
        for (Question question : questions) {
            if (isACT24Question(question) || isACT18Question(question) || isACT12Question(question)
                    || isACT6Question(question)) {
                actQuestion = question;
                List<Value> values = mStockSurvey.getValuesFromDB();
                for (Value value : values) {
                    if (value.getQuestion().getId_question().equals(actQuestion.getId_question())) {
                        if (Float.parseFloat(value.getValue()) == 0) {
                            return actQuestion;
                        }
                    }
                }
            }
        }

        return null;
    }

    private boolean isACT6Question(Question question) {
        if (question != null && question.getUid().equals("ihlfWLBg7Nr")) {
            return true;
        }
        return false;
    }
    private boolean isACT24Question(Question question) {
        if (question != null && question.getUid().equals("RUqD8Kckt3B")) {
            return true;
        }
        return false;
    }

    private boolean isACT18Question(Question question) {
        if (question != null && question.getUid().equals("GqHQPu6yCfu")) {
            return true;
        }
        return false;
    }

    private boolean isACT12Question(Question question) {
        if (question != null && question.getUid().equals("nN4jwsyjmE9")) {
            return true;
        }
        return false;
    }

    public static boolean isACTQuestion(Question question) {
        if (question.getUid().equals("nN4jwsyjmE9") || question.getUid().equals("GqHQPu6yCfu")
                || question.getUid().equals("RUqD8Kckt3B") || question.getUid().equals(
                "ihlfWLBg7Nr")) {
            return true;
        }
        return false;
    }

    public static Question getTreatmentQuestion() {
        return Question.findByUID("9cV1JoHmO94");
    }

    private org.eyeseetea.malariacare.database.model.Treatment getTreatmentFromSurvey() {

        List<Value> values = mMalariaSurvey.getValues();

        List<Match> ageMatches = new ArrayList<>();
        List<Match> pregnantMatches = new ArrayList<>();
        List<Match> severeMatches = new ArrayList<>();
        List<Match> rdtMatches = new ArrayList<>();
        for (Value value : values) {
            Question question = value.getQuestion();
            if (question.getUid().equals("2XX1JoHmO94")) {
                ageMatches =
                        QuestionThreshold.getMatchesWithQuestionValue(
                                question.getId_question(), Integer.parseInt(value.getValue()));
                Log.d(TAG, "age size: "+ageMatches.size());
            } else if (question.getUid().equals("6VV1JoHmO94")) {
                pregnantMatches = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "pregnant size: "+pregnantMatches.size());
            } else if (question.getUid().equals("11V1JoHmO94")) {
                severeMatches = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "severe size: "+severeMatches.size());
            } else if (question.getUid().equals("12V1JoHmO94")) {
                rdtMatches = QuestionOption.getMatchesWithQuestionOption(question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "rdt size: "+rdtMatches.size());
            }
        }
        Log.d(TAG, "matches obtained");
        Match treatmentMatch = null;
        for (Match match : ageMatches) {
            if (pregnantMatches.contains(match) && severeMatches.contains(match)
                    && rdtMatches.contains(match)) {
                treatmentMatch = match;
                break;
            }
        }

        org.eyeseetea.malariacare.database.model.Treatment treatment = null;
        if (treatmentMatch != null) {
            Log.d(TAG, "match: " + treatmentMatch.toString());
            treatment = treatmentMatch.getTreatment();
            Log.d(TAG, "treatment: " + treatment.toString());
        }
        return treatment;
    }

    private List<Question> getQuestionsForTreatment(
            org.eyeseetea.malariacare.database.model.Treatment treatment) {
        List<Question> questions = new ArrayList<>();
        List<Drug> drugs = treatment.getDrugsForTreatment();

        Question treatmentQuestion = new Question();
        treatmentQuestion.setOutput(Constants.QUESTION_LABEL);
        treatmentQuestion.setForm_name(treatment.getDiagnosis());
        treatmentQuestion.setHelp_text(treatment.getMessage());
        treatmentQuestion.setCompulsory(0);
        treatmentQuestion.setHeader((long) 7);
        questions.add(treatmentQuestion);

        for (Drug drug : drugs) {
            Question question = Question.findByUID(drug.getQuestion_code());
            if (question != null) {
                if (isPq(question)) {
                    question.setForm_name(getPqTitleDose(drug.getDose()));
                } else if (isCq(question)) {
                    question.setForm_name(getCqTitleDose(drug.getDose()));
                }
                doseByQuestion.put(question.getId_question(), drug.getDose());
                questions.add(question);
            }
            if (!questions.isEmpty()) {
                Log.d(TAG, "Question: " + questions.get(questions.size() - 1) + "\n");
            }
        }
        questions.add(Question.findByUID("9fV1JoHmO94"));

        return questions;
    }


    private boolean isPq(Question question) {
        if (question.getUid().equals("Sttahtf0iHZ")) {
            return true;
        }
        return false;
    }

    private boolean isCq(Question question) {
        if (question.getUid().equals("jZvZ4Q39J6s")) {
            return true;
        }
        return false;
    }

    private String getPqTitleDose(int dose) {
        switch (dose) {
            case 2:
                return "drugs_2_of_Pq_review_title";
            case 4:
                return "drugs_4_of_Pq_review_title";
            case 6:
                return "drugs_6_of_Pq_review_title";
            case 16:
                return "drugs_16_of_Pq_review_title";
            case 32:
                return "drugs_32_of_Pq_review_title";
            case 48:
                return "drugs_48_of_Pq_review_title";
        }
        return "drugs_referral_Pq_review_title";
    }

    private String getCqTitleDose(int dose) {
        switch (dose) {
            case 4:
                return "drugs_4_of_Cq_review_title";
            case 5:
                return "drugs_5_of_Cq_review_title";
            case 7:
                return "drugs_7_of_Cq_review_title";
            case 10:
                return "drugs_10_of_Cq_review_title";
        }
        return "drugs_referral_Cq_review_title";
    }

    public static Question getQuestionFromOptionId(Long optionId) {
        String uid = "";
        if (optionId == ID_OP_ACT24) {
            uid = "RUqD8Kckt3B";
        } else if (optionId == ID_OP_ACT18) {
            uid = "GqHQPu6yCfu";
        } else if (optionId == ID_OP_ACT12) {
            uid = "nN4jwsyjmE9";
        } else if (optionId == ID_OP_ACT6) {
            uid = "ihlfWLBg7Nr";
        } else {
            uid = "ZEopAP6tQN4";
        }
        Question question = Question.findByUID(uid);
        question.getHeader();
        return Question.findByUID(uid);
    }


    public Answer getACTOptions(Question question) {
        List<Option> options = new ArrayList<>();
        Answer answer = new Answer("stock");
        answer.setId_answer((long) 204);

        Option optionACT24 = new Option("ACT_x_24", "ACT_x_24", (float) 0, answer);
        optionACT24.setId_option(ID_OP_ACT24);
        optionACT24.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx24.png"));
        Option optionACT12 = new Option("ACT_x_12", "ACT_x_12", (float) 0, answer);
        optionACT12.setId_option(ID_OP_ACT12);
        optionACT12.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx12.png"));
        Option optionACT6 = new Option("ACT_x_6", "ACT_x_6", (float) 0, answer);
        optionACT6.setId_option(ID_OP_ACT6);
        optionACT6.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx6.png"));
        Option optionACT18 = new Option("ACT_x_18", "ACT_x_18", (float) 0, answer);
        optionACT18.setId_option(ID_OP_ACT18);
        optionACT18.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx18.png"));
        Option optionOutStock = new Option("out_stock_option", "out_stock_option", (float) 0,
                answer);
        optionOutStock.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p6_out_stock.png"));
        optionOutStock.setId_option(ID_OP_OUT_STOCK);
        if (!isACT12Question(question)) {
            options.add(optionACT12);
        }
        if (!isACT6Question(question)) {
            options.add(optionACT6);
        }
        if (!isACT18Question(question)) {
            options.add(optionACT18);
        }
        if (!isACT24Question(question)) {
            options.add(optionACT24);
        }
        options.add(optionOutStock);

        answer.setOptions(options);

        return answer;
    }

}
