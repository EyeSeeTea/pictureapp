package org.eyeseetea.malariacare.domain.entity;

import android.content.Context;
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
import org.eyeseetea.malariacare.data.database.utils.QuestionStrategy;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;
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
            if (new QuestionStrategy().isACT24Question(question) || new QuestionStrategy().isACT18Question(question) || new QuestionStrategy().isACT12Question(question)
                    || new QuestionStrategy().isACT6Question(question)) {
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
            if (new QuestionStrategy().isAgeQuestion(question)) {
                ageMatches =
                        QuestionThreshold.getMatchesWithQuestionValue(
                                question.getId_question(), Integer.parseInt(value.getValue()));
                Log.d(TAG, "age size: " + ageMatches.size());
            } else if (new QuestionStrategy().isSexPregnantQuestion(question.getUid())) {
                pregnantMatches = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "pregnant size: " + pregnantMatches.size());
            } else if (new QuestionStrategy().isSevereSymtomsQuestion(question.getUid())) {
                severeMatches = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "severe size: " + severeMatches.size());
            } else if (new QuestionStrategy().isRdtQuestion(question.getUid())) {
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
        Question actHiddenQuestion = new QuestionStrategy().getDynamicTreatmentHideQuestion();
        List<Value> values = Session.getMalariaSurvey().getValuesFromDB();
        Value actValue = null;
        for (Value value : values) {
            if (value.getQuestion().equals(actHiddenQuestion)) {
                actValue = value;
            }
        }
        if (actValue == null) {
            actValue = new Value(new QuestionStrategy().getOptionTreatmentYesCode(),
                    actHiddenQuestion, Session.getMalariaSurvey());
        } else {
            actValue.setOption(new QuestionStrategy().getOptionTreatmentYesCode() );
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
                if (new QuestionStrategy().isPq(question.getUid())) {
                    question.setForm_name(new SurveyFragmentStrategy().getPqTitleDose(DrugCombination.getDose(treatment, drug)));
                } else if (new QuestionStrategy().isCq(question.getUid())) {
                    question.setForm_name(new SurveyFragmentStrategy().getCqTitleDose(DrugCombination.getDose(treatment, drug)));
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

    public HashMap<Long, Float> getOptionDose(
            org.eyeseetea.malariacare.data.database.model.Treatment mainTreatment) {
        List<org.eyeseetea.malariacare.data.database.model.Treatment> treatments =
                mainTreatment.getAlternativeTreatments();
        HashMap<Long, Float> optionDose = new HashMap<>();
        for (org.eyeseetea.malariacare.data.database.model.Treatment treatment : treatments) {
            List<Drug> drugs = treatment.getDrugsForTreatment();
            for (Drug drug : drugs) {
                if (isACT24(drug.getQuestion_code())) {
                    optionDose.put(new QuestionStrategy().getACT24Question().getId_question(),
                            DrugCombination.getDose(treatment, drug));
                } else if (isACT18(drug.getQuestion_code())) {
                    optionDose.put(new QuestionStrategy().getACT18Question().getId_question(),
                            DrugCombination.getDose(treatment, drug));
                } else if (isACT12(drug.getQuestion_code())) {
                    optionDose.put(new QuestionStrategy().getACT12Question().getId_question(),
                            DrugCombination.getDose(treatment, drug));
                } else if (isACT6(drug.getQuestion_code())) {
                    optionDose.put(new QuestionStrategy().getACT6Question().getId_question(),
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
        optionACT24.setId_option(new QuestionStrategy().getACT24Question().getId_question());
        optionACT24.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx24.png"));
        Option optionACT12 = new Option("ACT_x_12", "ACT_x_12", 0f, answer);
        optionACT12.setId_option(new QuestionStrategy().getACT12Question().getId_question());
        optionACT12.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx12.png"));
        Option optionACT6 = new Option("ACT_x_6", "ACT_x_6", 0f, answer);
        optionACT6.setId_option(new QuestionStrategy().getACT6Question().getId_question());
        optionACT6.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx6.png"));
        Option optionACT18 = new Option("ACT_x_18", "ACT_x_18", 0f, answer);
        optionACT18.setId_option(new QuestionStrategy().getACT18Question().getId_question());
        optionACT18.setOptionAttribute(
                new OptionAttribute("c8b8c7", "question_images/p5_actx18.png"));
        Question outStockQuestion = new QuestionStrategy().getOutOfStockQuestion();
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
                if (isACT24(drug.getQuestion_code())) {
                    optionACT24.setCode(treatment.getMessage().toString());
                    options.add(optionACT24);
                } else if (isACT18(drug.getQuestion_code())) {
                    optionACT18.setCode(treatment.getMessage().toString());
                    options.add(optionACT18);
                } else if (isACT12(drug.getQuestion_code())) {
                    optionACT12.setCode(treatment.getMessage().toString());
                    options.add(optionACT12);
                } else if (isACT6(drug.getQuestion_code())) {
                    optionACT6.setCode(treatment.getMessage().toString());
                    options.add(optionACT6);
                }
            }
        }
        options.add(optionOutStock);

        answer.setOptions(options);

        return answer;
    }

    private static boolean isACT24(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act24QuestionUID));
    }
    private static boolean isACT18(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act18QuestionUID));
    }
    private static boolean isACT12(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act12QuestionUID));
    }
    private static boolean isACT6(String uid_question) {
        return uid_question.equals(PreferencesState.getInstance().getContext().getString(R.string.act6QuestionUID));
    }

    public static boolean isACT(String uid_question) {
        return isACT6(uid_question) || isACT12(uid_question) || isACT18(uid_question) || isACT24(uid_question);
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

    private void saveTreatmentInTreatmentQuestion(
            org.eyeseetea.malariacare.data.database.model.Treatment treatment) {
        Question treatmentQuestionSend = new QuestionStrategy().getDynamicTreatmentQuestion();
        Question treatmentQuestionShow = new QuestionStrategy().getTreatmentDiagnosisVisibleQuestion();
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

    public List<Question> getNoTreatmentQuestions() {
        List<Question> questions = new ArrayList<>();

        Question treatmentQuestion = new Question();
        treatmentQuestion.setOutput(Constants.QUESTION_LABEL);
        treatmentQuestion.setForm_name("");
        treatmentQuestion.setHelp_text(new SurveyFragmentStrategy().getTreatmentError());
        treatmentQuestion.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
        treatmentQuestion.setHeader(Header.DYNAMIC_TREATMENT_HEADER_ID);
        questions.add(treatmentQuestion);

        return questions;
    }
}
