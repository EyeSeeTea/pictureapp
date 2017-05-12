package org.eyeseetea.malariacare.domain.entity;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Drug;
import org.eyeseetea.malariacare.data.database.model.DrugCombination;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Match;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OptionAttribute;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Translation;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Treatment {

    private static final String TAG = ".Treatment";
    private Survey mMalariaSurvey;
    private Survey mStockSurvey;
    private List<Question> mQuestions;
    private org.eyeseetea.malariacare.data.database.model.Treatment mTreatment;
    private HashMap<Long, Float> doseByQuestion;

    public Treatment(Survey malariaSurvey, Survey stockSurvey) {
        mMalariaSurvey = malariaSurvey;
        mStockSurvey = stockSurvey;
        doseByQuestion = new HashMap<>();
    }

    public static Question getTreatmentQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.dynamicTreatmentHideQuestionUID));
    }

    public static Question getDynamicStockQuestion() {
        Context context = PreferencesState.getInstance().getContext();
        return Question.findByUID(context.getString(R.string.dynamicStockQuestionUID));
    }

    public static Question getTreatmentQuestionForTag(Object tag) {
        Resources resources = PreferencesState.getInstance().getContext().getResources();
        if (((Question) tag).isPq()) {
            return Question.findByUID(resources.getString(R.string.stockPqQuestionUID));
        } else {
            return Question.findByUID(
                    resources.getString(R.string.dynamicTreatmentHideQuestionUID));
        }
    }


    public List<Question> getQuestions() {
        return mQuestions;
    }

    public HashMap<Long, Float> getDoseByQuestion() {
        return doseByQuestion;
    }

    public org.eyeseetea.malariacare.data.database.model.Treatment getTreatment() {
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
                    if(value.getQuestion()==null){
                        continue;
                    }
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

    private org.eyeseetea.malariacare.data.database.model.Treatment getTreatmentFromSurvey() {

        List<Value> values = mMalariaSurvey.getValuesFromDB();

        List<Match> ageMatches = new ArrayList<>();
        List<Match> pregnantMatches = new ArrayList<>();
        List<Match> severeMatches = new ArrayList<>();
        List<Match> rdtMatches = new ArrayList<>();
        for (Value value : values) {
            Question question = value.getQuestion();
            //Getting matches for questions of age, pregnant, severe and rdt.
            if (question == null) {
                continue;
            }
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
        List<Match> treatmentMatches = new ArrayList<>();
        for (Match match : ageMatches) {
            if (pregnantMatches.contains(match) && severeMatches.contains(match)
                    && rdtMatches.contains(match)) {
                treatmentMatches.add(match);

            }
        }
        org.eyeseetea.malariacare.data.database.model.Treatment treatment = null;
        for (Match treatmentMatch : treatmentMatches) {
            if (Session.getCredentials().isDemoCredentials()) {
                return treatmentMatch.getTreatment();
            }
            if (treatmentMatch.getTreatment().getOrganisation().getId_organisation() ==
                    Session.getUser().getOrganisation()) {
                Log.d(TAG, "match: " + treatmentMatch.toString());
                treatment = treatmentMatch.getTreatment();
                Log.d(TAG, "treatment: " + treatment.toString());
                break;
            }
        }
        return treatment;
    }

    public boolean hasTreatment() {
        mTreatment = getTreatmentFromSurvey();
        if (mTreatment != null) {
            putACTDefaultYes();
            mQuestions = getQuestionsForTreatment(mTreatment);
            saveTreatmentInTreatmentQuestion(mTreatment);
        }
        return mTreatment != null;
    }

    private void putACTDefaultYes() {
        Question actHiddenQuestion = Question.findByUID(
                getContext().getString(R.string.dynamicTreatmentHideQuestionUID));
        List<Value> values = Session.getMalariaSurvey().getValuesFromDB();
        Value actValue = null;
        for (Value value : values) {
            if (value.getQuestion().equals(actHiddenQuestion)) {
                actValue = value;
            }
        }
        if (actValue == null) {
            actValue = new Value(
                    Option.findByCode(getContext().getString(R.string.dynamic_treatment_yes_code)),
                    actHiddenQuestion, Session.getMalariaSurvey());
        } else {
            actValue.setOption(
                    Option.findByCode(getContext().getString(R.string.dynamic_treatment_yes_code)));
        }
        actValue.save();
    }

    private List<Question> getQuestionsForTreatment(
            org.eyeseetea.malariacare.data.database.model.Treatment treatment) {
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

    public HashMap<Long, Float> getOptionDose(
            org.eyeseetea.malariacare.data.database.model.Treatment mainTreatment) {
        List<org.eyeseetea.malariacare.data.database.model.Treatment> treatments =
                mainTreatment.getAlternativeTreatments();
        HashMap<Long, Float> optionDose = new HashMap<>();
        for (org.eyeseetea.malariacare.data.database.model.Treatment treatment : treatments) {
            List<Drug> drugs = treatment.getDrugsForTreatment();
            for (Drug drug : drugs) {
                if (drug.isACT24()) {
                    optionDose.put(Question.getACT24Question().getId_question(),
                            DrugCombination.getDose(treatment, drug));
                } else if (drug.isACT18()) {
                    optionDose.put(Question.getACT18Question().getId_question(),
                            DrugCombination.getDose(treatment, drug));
                } else if (drug.isACT12()) {
                    optionDose.put(Question.getACT12Question().getId_question(),
                            DrugCombination.getDose(treatment, drug));
                } else if (drug.isACT6()) {
                    optionDose.put(Question.getACT6Question().getId_question(),
                            DrugCombination.getDose(treatment, drug));
                }
            }
        }
        return optionDose;
    }

    public Answer getACTOptions(
            org.eyeseetea.malariacare.data.database.model.Treatment mainTreatment) {
        List<Option> options = new ArrayList<>();
        Answer answer = new Answer("stock");
        answer.setId_answer(Answer.DYNAMIC_STOCK_ANSWER_ID);
        //this options are never saved
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
        Question outStockQuestion = Question.getOutOfStockQuestion();
        Option optionOutStock = new Option("out_stock_option", "out_stock_option", 0f,
                outStockQuestion.getAnswer());
        optionOutStock.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p6_stockout.png"));
        optionOutStock.setId_option(outStockQuestion.getId_question());

        List<org.eyeseetea.malariacare.data.database.model.Treatment> treatments =
                mainTreatment.getAlternativeTreatments();
        for (org.eyeseetea.malariacare.data.database.model.Treatment treatment : treatments) {
            List<Drug> alternativeDrugs = treatment.getDrugsForTreatment();
            for (Drug drug : alternativeDrugs) {
                if (drug.isACT24()) {
                    optionACT24.setCode(treatment.getMessage().toString());
                    options.add(optionACT24);
                } else if (drug.isACT18()) {
                    optionACT18.setCode(treatment.getMessage().toString());
                    options.add(optionACT18);
                } else if (drug.isACT12()) {
                    optionACT12.setCode(treatment.getMessage().toString());
                    options.add(optionACT12);
                } else if (drug.isACT6()) {
                    optionACT6.setCode(treatment.getMessage().toString());
                    options.add(optionACT6);
                }
            }
        }
        options.add(optionOutStock);

        answer.setOptions(options);

        return answer;
    }

    private List<Drug> getAlternativeDrugsForTreatment(
            org.eyeseetea.malariacare.data.database.model.Treatment mainTreatment) {
        List<org.eyeseetea.malariacare.data.database.model.Treatment> alternativeTreatments =
                mainTreatment.getAlternativeTreatments();
        List<Drug> alternativeDrugs = new ArrayList<>();
        for (org.eyeseetea.malariacare.data.database.model.Treatment treatment :
                alternativeTreatments) {
            alternativeDrugs.addAll(treatment.getDrugsForTreatment());
        }
        return alternativeDrugs;
    }


    public Context getContext() {
        return PreferencesState.getInstance().getContext();
    }

    private String getPqTitleDose(float dose) {
        return getTitleDose(dose,
                getContext().getResources().getString(R.string.drugs_referral_Pq_review_title));
    }

    private String getCqTitleDose(float dose) {
        return getTitleDose(dose,
                getContext().getResources().getString(R.string.drugs_referral_Cq_review_title));
    }

    private void saveTreatmentInTreatmentQuestion(
            org.eyeseetea.malariacare.data.database.model.Treatment treatment) {
        Question treatmentQuestionSend = Question.findByUID(
                getContext().getResources().getString(R.string.dynamicTreatmentQuestionUID));
        Question treatmentQuestionShow = Question.findByUID(
                getContext().getResources().getString(R.string.treatmentDiagnosisVisibleQuestion));
        Survey malariaSurvey = Session.getMalariaSurvey();
        List<Value> values =
                malariaSurvey.getValues();//this values should be get from memory because the
        // treatment options are in memory
        boolean questionInSurvey = false;
        boolean questionShowInSurvey = false;
        String diagnosisMessage = Utils.getInternationalizedString(
                String.valueOf(treatment.getDiagnosis()));
        String defaultDiagnosisMessage = Translation.getLocalizedString(treatment.getDiagnosis(),
                Translation.DEFAULT_LANGUAGE);
        for (Value value : values) {
            if (value.getQuestion() == null) {
                continue;
            }
            if (value.getQuestion().equals(treatmentQuestionSend)) {
                value.setValue(defaultDiagnosisMessage);
                questionInSurvey = true;
                value.save();
            }
            if (value.getQuestion().equals(treatmentQuestionShow)) {
                value.setValue(diagnosisMessage);
                questionShowInSurvey = true;
                value.save();
            }
        }
        if (!questionShowInSurvey) {
            Value value = new Value(diagnosisMessage, treatmentQuestionShow,
                    malariaSurvey);
            value.insert();
        }
        if (!questionInSurvey) {
            Value value = new Value(defaultDiagnosisMessage, treatmentQuestionSend,
                    malariaSurvey);
            value.insert();
        }
    }

    private String getTitleDose(float dose, String drug) {
        return String.format(
                getContext().getResources().getString(R.string.drugs_dose_of_drug_review_title),
                dose, drug);
    }

    public List<Question> getNoTreatmentQuestions() {
        List<Question> questions = new ArrayList<>();

        Question treatmentQuestion = new Question();
        treatmentQuestion.setOutput(Constants.QUESTION_LABEL);
        treatmentQuestion.setForm_name("");
        treatmentQuestion.setHelp_text(
                getContext().getResources().getResourceName(R.string.error_no_treatment));
        treatmentQuestion.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
        treatmentQuestion.setHeader(Header.DYNAMIC_TREATMENT_HEADER_ID);
        questions.add(treatmentQuestion);

        return questions;
    }
}
