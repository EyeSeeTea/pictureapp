package org.eyeseetea.malariacare.domain.entity;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Drug;
import org.eyeseetea.malariacare.database.model.DrugCombination;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
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
    private HashMap<Long, Float> doseByQuestion;

    public Treatment(Survey malariaSurvey, Survey stockSurvey) {
        mMalariaSurvey = malariaSurvey;
        mStockSurvey = stockSurvey;
        doseByQuestion = new HashMap<>();
    }

    public static Question getTreatmentQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.dynamicTreatmentQuestionUID));
    }

    public static Question getDynamicStockQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.dynamicStockQuestionUID));
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

    public HashMap<Long, Float> getDoseByQuestion() {
        return doseByQuestion;
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

    private org.eyeseetea.malariacare.database.model.Treatment getTreatmentFromSurvey() {

        List<Value> values = mMalariaSurvey.getValues();

        List<Match> ageMatches = new ArrayList<>();
        List<Match> pregnantMatches = new ArrayList<>();
        List<Match> severeMatches = new ArrayList<>();
        List<Match> rdtMatches = new ArrayList<>();
        for (Value value : values) {
            Question question = value.getQuestion();
            //Getting matches for questions of age, pregnant, severe and rdt.
            if (question.getUid().equals(getContext().getString(R.string.ageQuestionUID))) {
                ageMatches =
                        QuestionThreshold.getMatchesWithQuestionValue(
                                question.getId_question(), Integer.parseInt(value.getValue()));
                Log.d(TAG, "age size: " + ageMatches.size());
            } else if (question.getUid().equals(
                    getContext().getString(R.string.sexPregnantQuestionUID))) {
                pregnantMatches = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "pregnant size: " + pregnantMatches.size());
            } else if (question.getUid().equals(
                    getContext().getString(R.string.severeSymtomsQuestionUID))) {
                severeMatches = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "severe size: " + severeMatches.size());
            } else if (question.getUid().equals(getContext().getString(R.string.rdtQuestionUID))) {
                rdtMatches = QuestionOption.getMatchesWithQuestionOption(question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "rdt size: " + rdtMatches.size());
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
        treatmentQuestion.setForm_name(treatment.getDiagnosis().toString());
        treatmentQuestion.setHelp_text(treatment.getMessage().toString());
        treatmentQuestion.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
        treatmentQuestion.setHeader(Header.DYNAMIC_TREATMENT_HEADER_ID);
        questions.add(treatmentQuestion);

        for (Drug drug : drugs) {
            Question question = Question.findByUID(drug.getQuestion_code());
            if (question != null) {
                if (isPq(question)) {
                    question.setForm_name(getPqTitleDose(DrugCombination.getDose(treatment, drug)));
                } else if (isCq(question)) {
                    question.setForm_name(getCqTitleDose(DrugCombination.getDose(treatment, drug)));
                }
                doseByQuestion.put(question.getId_question(),
                        DrugCombination.getDose(treatment, drug));
                questions.add(question);
            }
            if (!questions.isEmpty()) {
                Log.d(TAG, "Question: " + questions.get(questions.size() - 1) + "\n");
            }
        }
        questions.add(Question.findByUID(getContext().getString(R.string.referralQuestionUID)));

        return questions;
    }

    public boolean isACT6Question(Question question) {
        if (question != null && question.getUid().equals(
                getContext().getString(R.string.act6QuestionUID))) {
            return true;
        }
        return false;
    }

    public boolean isACT12Question(Question question) {
        if (question != null && question.getUid().equals(
                getContext().getString(R.string.act12QuestionUID))) {
            return true;
        }
        return false;
    }

    public boolean isACT18Question(Question question) {
        if (question != null && question.getUid().equals(
                getContext().getString(R.string.act18QuestionUID))) {
            return true;
        }
        return false;
    }

    public boolean isACT24Question(Question question) {
        if (question != null && question.getUid().equals(
                getContext().getString(R.string.act24QuestionUID))) {
            return true;
        }
        return false;
    }

    public static boolean isACTQuestion(Question question) {
        Context context = PreferencesState.getInstance().getContext();
        if (question.getUid().equals(context.getString(R.string.act6QuestionUID))
                || question != null && question.getUid().equals(
                context.getString(R.string.act12QuestionUID))
                || question != null && question.getUid().equals(
                context.getString(R.string.act18QuestionUID))
                || question != null && question.getUid().equals(
                context.getString(R.string.act24QuestionUID))) {
            return true;
        }
        return false;
    }

    public boolean isPq(Question question) {
        if (question.getUid().equals(getContext().getString(R.string.pqQuestionUID))) {
            return true;
        }
        return false;
    }

    public boolean isCq(Question question) {
        if (question.getUid().equals(getContext().getString(R.string.cqQuestionUID))) {
            return true;
        }
        return false;
    }

    public HashMap<Long, Float> getOptionDose(Question question) {
        HashMap<Long, Float> optionDose = new HashMap<>();
        if (isACT6Question(question)) {
            optionDose.put(Question.getACT12Question().getId_question(), 0.5f);
            optionDose.put(Question.getACT18Question().getId_question(), 0.3f);
            optionDose.put(Question.getACT24Question().getId_question(), 0.25f);
        }
        if (isACT12Question(question)) {
            optionDose.put(Question.getACT6Question().getId_question(), 2f);
            optionDose.put(Question.getACT18Question().getId_question(), 0.6f);
            optionDose.put(Question.getACT24Question().getId_question(), 0.5f);
        }
        if (isACT18Question(question)) {
            optionDose.put(Question.getACT6Question().getId_question(), 3f);
            optionDose.put(Question.getACT12Question().getId_question(), 1.5f);
            optionDose.put(Question.getACT24Question().getId_question(), 0.75f);
        }
        if (isACT24Question(question)) {
            optionDose.put(Question.getACT6Question().getId_question(), 4f);
            optionDose.put(Question.getACT12Question().getId_question(), 2f);
            optionDose.put(Question.getACT18Question().getId_question(), 1.3f);
        }

        return optionDose;
    }

    public Answer getACTOptions(Question question) {
        List<Option> options = new ArrayList<>();
        Answer answer = new Answer("stock");
        answer.setId_answer(Answer.DYNAMIC_STOCK_ANSWER_ID);

        Option optionACT24 = new Option("ACT_x_24", "ACT_x_24", 0f, answer);
        optionACT24.setId_option(Question.getACT24Question().getId_question());
        optionACT24.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx24.png"));
        Option optionACT12 = new Option("ACT_x_12", "ACT_x_12", 0f, answer);
        optionACT12.setId_option(Question.getACT12Question().getId_question());
        optionACT12.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx12.png"));
        Option optionACT6 = new Option("ACT_x_6", "ACT_x_6", 0f, answer);
        optionACT6.setId_option(Question.getACT6Question().getId_question());
        optionACT6.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx6.png"));
        Option optionACT18 = new Option("ACT_x_18", "ACT_x_18", 0f, answer);
        optionACT18.setId_option(Question.getACT18Question().getId_question());
        optionACT18.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx18.png"));
        Option optionOutStock = new Option("out_stock_option", "out_stock_option", 0f,
                answer);
        optionOutStock.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p6_stockout.png"));
        optionOutStock.setId_option(Question.getOutOfStockQuestion().getId_question());
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

    public Context getContext() {
        return PreferencesState.getInstance().getContext();
    }

    private String getPqTitleDose(float dose) {
        return getTitleDose(dose, "Pq");
    }

    private String getCqTitleDose(float dose) {
        return getTitleDose(dose, "Cq");
    }

    private String getTitleDose(float dose, String drug) {
        return String.format(
                getContext().getResources().getString(R.string.drugs_dose_of_drug_review_title),
                dose, drug);
    }
}
