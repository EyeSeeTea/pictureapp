package org.eyeseetea.malariacare.domain.entity;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Drug;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
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
    private HashMap<Long, Integer> doseByQuestion;

    public Treatment(Survey malariaSurvey, Survey stockSurvey) {
        mMalariaSurvey = malariaSurvey;
        mStockSurvey = stockSurvey;
        doseByQuestion = new HashMap<>();
    }

    public boolean hasTreatment() {
        mTreatment = getTreatmentFromSurvey();
        if (mTreatment != null) {
            mQuestions = getQuestionsForTreatment(mTreatment);
            removeOldQuestionsFromSurvey();
        }

        return mTreatment != null;
    }


    private org.eyeseetea.malariacare.database.model.Treatment getTreatmentFromSurvey() {

        List<Value> values = mMalariaSurvey.getValues();

        List<Match> ageMatches = new ArrayList<>();
        List<Match> pregnantMatches = new ArrayList<>();
        List<Match> severeMatches = new ArrayList<>();
        List<Match> rdtMatches = new ArrayList<>();
        for (Value value : values) {
            Question question = value.getQuestion();
            Context context = PreferencesState.getInstance().getContext();
            if (question.getUid().equals(context.getString(R.string.ageQuestionUID))) {
                ageMatches =
                        QuestionThreshold.getMatchesWithQuestionValue(
                                question.getId_question(), Integer.parseInt(value.getValue()));
            } else if (question.getUid().equals(context.getString(R.string.sexPregnantQuestionUID))) {
                pregnantMatches = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
            } else if (question.getUid().equals(context.getString(R.string.severeSymtomsQuestionUID))) {
                severeMatches = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
            } else if (question.getUid().equals(context.getString(R.string.rdtQuestionUID))) {
                rdtMatches = QuestionOption.getMatchesWithQuestionOption(question.getId_question(),
                        value.getId_option());
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
        Context context = PreferencesState.getInstance().getContext();
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
        questions.add(Question.findByUID(context.getString(R.string.referralQuestionUID)));

        return questions;
    }


    public List<Question> getQuestions() {
        return mQuestions;
    }

    public HashMap<Long, Integer> getDoseByQuestion() {
        return doseByQuestion;
    }

    public org.eyeseetea.malariacare.database.model.Treatment getTreatment() {
        return mTreatment;
    }


    private boolean isPq(Question question) {
        Context context = PreferencesState.getInstance().getContext();
        if (question.getUid().equals(context.getString(R.string.pqQuestionUID))) {
            return true;
        }
        return false;
    }

    private boolean isCq(Question question) {
        Context context = PreferencesState.getInstance().getContext();
        if (question.getUid().equals(context.getString(R.string.cqQuestionUID))) {
            return true;
        }
        return false;
    }

    private String getPqTitleDose(int dose) {
        return getTitleDose(dose, "Pq");
    }

    private String getCqTitleDose(int dose) {
        return getTitleDose(dose, "Cq");
    }

    private String getTitleDose(int dose, String drug) {
        return String.format("drugs_%d_of_%s_review_title", dose, drug);
    }

    private void setAnswerYesOptionFactor(int dose, Question question) {
        List<Option> options = question.getAnswer().getOptions();
        for (Option option : options) {
            if (option.getName().equals("Yes")) {
                option.setFactor((float) dose);
            }
        }
    }

    private void removeOldQuestionsFromSurvey() {
        List<Value> values = mStockSurvey.getValues();
        for (Value value : values) {
            boolean isInQuestions = false;
            for (Question question : mQuestions) {
                if (value.getQuestion().getId_question() == question.getId_question()) {
                    isInQuestions = true;
                }
            }
            if (!isInQuestions) {
                values.remove(value);
                value.delete();
            }
        }

    }

}
