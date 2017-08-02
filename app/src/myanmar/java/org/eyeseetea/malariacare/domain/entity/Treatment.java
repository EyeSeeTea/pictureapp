package org.eyeseetea.malariacare.domain.entity;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.DrugCombinationDB;
import org.eyeseetea.malariacare.data.database.model.DrugDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
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
            if (TreatmentQueries.isACT24Question(question) || TreatmentQueries.isACT18Question(
                    question) || TreatmentQueries.isACT12Question(question)
                    || TreatmentQueries.isACT6Question(question)) {
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

        List<MatchDB> ageMatchDBs = new ArrayList<>();
        List<MatchDB> pregnantMatchDBs = new ArrayList<>();
        List<MatchDB> severeMatchDBs = new ArrayList<>();
        List<MatchDB> rdtMatchDBs = new ArrayList<>();
        for (Value value : values) {
            Question question = value.getQuestion();
            //Getting matches for questions of age, pregnant, severe and rdt.
            if (question == null) {
                continue;
            }
            if (TreatmentQueries.isAgeQuestion(question)) {
                ageMatchDBs =
                        QuestionThreshold.getMatchesWithQuestionValue(
                                question.getId_question(), Integer.parseInt(value.getValue()));
                Log.d(TAG, "age size: " + ageMatchDBs.size());
            } else if (TreatmentQueries.isSexPregnantQuestion(question.getUid())) {
                pregnantMatchDBs = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "pregnant size: " + pregnantMatchDBs.size());
            } else if (TreatmentQueries.isSevereSymtomsQuestion(question.getUid())) {
                severeMatchDBs = QuestionOption.getMatchesWithQuestionOption(
                        question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "severe size: " + severeMatchDBs.size());
            } else if (TreatmentQueries.isRdtQuestion(question.getUid())) {
                rdtMatchDBs = QuestionOption.getMatchesWithQuestionOption(question.getId_question(),
                        value.getId_option());
                Log.d(TAG, "rdt size: " + rdtMatchDBs.size());
            }
        }
        Log.d(TAG, "matches obtained");
        List<MatchDB> treatmentMatchDBs = new ArrayList<>();
        for (MatchDB matchDB : ageMatchDBs) {
            if (pregnantMatchDBs.contains(matchDB) && severeMatchDBs.contains(matchDB)
                    && rdtMatchDBs.contains(matchDB)) {
                treatmentMatchDBs.add(matchDB);

            }
        }
        org.eyeseetea.malariacare.data.database.model.Treatment treatment = null;
        for (MatchDB treatmentMatchDB : treatmentMatchDBs) {
            if (Session.getCredentials().isDemoCredentials()) {
                return treatmentMatchDB.getTreatment();
            }
            if (treatmentMatchDB.getTreatment().getPartnerDB().getId_partner() ==
                    Session.getUser().getOrganisation()) {
                Log.d(TAG, "match: " + treatmentMatchDB.toString());
                treatment = treatmentMatchDB.getTreatment();
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
        Question actHiddenQuestion = TreatmentQueries.getDynamicTreatmentHideQuestion();
        List<Value> values = Session.getMalariaSurvey().getValuesFromDB();
        Value actValue = null;
        for (Value value : values) {
            if (value.getQuestion().equals(actHiddenQuestion)) {
                actValue = value;
            }
        }
        if (actValue == null) {
            actValue = new Value(TreatmentQueries.getOptionTreatmentYesCode(),
                    actHiddenQuestion, Session.getMalariaSurvey());
        } else {
            actValue.setOptionDB(TreatmentQueries.getOptionTreatmentYesCode());
        }
        actValue.save();
    }

    private List<Question> getQuestionsForTreatment(
            org.eyeseetea.malariacare.data.database.model.Treatment treatment) {
        List<Question> questions = new ArrayList<>();
        List<DrugDB> drugs = treatment.getDrugsForTreatment();

        Question treatmentQuestion = new Question();
        treatmentQuestion.setOutput(Constants.QUESTION_LABEL);
        treatmentQuestion.setForm_name(treatment.getDiagnosis().toString());
        treatmentQuestion.setHelp_text(treatment.getMessage().toString());
        treatmentQuestion.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
        treatmentQuestion.setHeaderDB(HeaderDB.DYNAMIC_TREATMENT_HEADER_ID);
        questions.add(treatmentQuestion);

        for (DrugDB drug : drugs) {
            Question question = Question.findByUID(drug.getQuestion_code());
            if (question != null) {
                if (TreatmentQueries.isPq(question.getUid())) {
                    question.setForm_name(TreatmentQueries.getPqTitleDose(
                            DrugCombinationDB.getDose(treatment, drug)));
                } else if (TreatmentQueries.isCq(question.getUid())) {
                    question.setForm_name(TreatmentQueries.getCqTitleDose(
                            DrugCombinationDB.getDose(treatment, drug)));
                }
                doseByQuestion.put(question.getId_question(),
                        DrugCombinationDB.getDose(treatment, drug));
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
            List<DrugDB> drugs = treatment.getDrugsForTreatment();
            for (DrugDB drug : drugs) {
                if (TreatmentQueries.isACT24(drug.getQuestion_code())) {
                    optionDose.put(TreatmentQueries.getACT24Question().getId_question(),
                            DrugCombinationDB.getDose(treatment, drug));
                } else if (TreatmentQueries.isACT18(drug.getQuestion_code())) {
                    optionDose.put(TreatmentQueries.getACT18Question().getId_question(),
                            DrugCombinationDB.getDose(treatment, drug));
                } else if (TreatmentQueries.isACT12(drug.getQuestion_code())) {
                    optionDose.put(TreatmentQueries.getACT12Question().getId_question(),
                            DrugCombinationDB.getDose(treatment, drug));
                } else if (TreatmentQueries.isACT6(drug.getQuestion_code())) {
                    optionDose.put(TreatmentQueries.getACT6Question().getId_question(),
                            DrugCombinationDB.getDose(treatment, drug));
                }
            }
        }
        return optionDose;
    }

    public AnswerDB getACTOptions(
            org.eyeseetea.malariacare.data.database.model.Treatment mainTreatment) {
        List<OptionDB> options = new ArrayList<>();
        AnswerDB answer = new AnswerDB("stock");
        answer.setId_answer(AnswerDB.DYNAMIC_STOCK_ANSWER_ID);
        //this options are never saved
        OptionDB optionACT24 = new OptionDB("ACT_x_24", "ACT_x_24", 0f, answer);
        optionACT24.setId_option(TreatmentQueries.getACT24Question().getId_question());
        optionACT24.setOptionAttributeDB(
                new OptionAttributeDB("c8b8c7", "question_images/p5_actx24.png"));
        OptionDB optionACT12 = new OptionDB("ACT_x_12", "ACT_x_12", 0f, answer);
        optionACT12.setId_option(TreatmentQueries.getACT12Question().getId_question());
        optionACT12.setOptionAttributeDB(
                new OptionAttributeDB("c8b8c7", "question_images/p5_actx12.png"));
        OptionDB optionACT6 = new OptionDB("ACT_x_6", "ACT_x_6", 0f, answer);
        optionACT6.setId_option(TreatmentQueries.getACT6Question().getId_question());
        optionACT6.setOptionAttributeDB(
                new OptionAttributeDB("c8b8c7", "question_images/p5_actx6.png"));
        OptionDB optionACT18 = new OptionDB("ACT_x_18", "ACT_x_18", 0f, answer);
        optionACT18.setId_option(TreatmentQueries.getACT18Question().getId_question());
        optionACT18.setOptionAttributeDB(
                new OptionAttributeDB("c8b8c7", "question_images/p5_actx18.png"));
        Question outStockQuestion = TreatmentQueries.getOutOfStockQuestion();
        OptionDB optionOutStock = new OptionDB("out_stock_option", "out_stock_option", 0f,
                outStockQuestion.getAnswerDB());
        optionOutStock.setOptionAttributeDB(
                new OptionAttributeDB("c8b8c7", "question_images/p6_stockout.png"));
        optionOutStock.setId_option(outStockQuestion.getId_question());

        List<org.eyeseetea.malariacare.data.database.model.Treatment> treatments =
                mainTreatment.getAlternativeTreatments();
        for (org.eyeseetea.malariacare.data.database.model.Treatment treatment : treatments) {
            List<DrugDB> alternativeDrugs = treatment.getDrugsForTreatment();
            for (DrugDB drug : alternativeDrugs) {
                if (TreatmentQueries.isACT24(drug.getQuestion_code())) {
                    optionACT24.setName(treatment.getMessage().toString());
                    options.add(optionACT24);
                } else if (TreatmentQueries.isACT18(drug.getQuestion_code())) {
                    optionACT18.setName(treatment.getMessage().toString());
                    options.add(optionACT18);
                } else if (TreatmentQueries.isACT12(drug.getQuestion_code())) {
                    optionACT12.setName(treatment.getMessage().toString());
                    options.add(optionACT12);
                } else if (TreatmentQueries.isACT6(drug.getQuestion_code())) {
                    optionACT6.setName(treatment.getMessage().toString());
                    options.add(optionACT6);
                }
            }
        }
        options.add(optionOutStock);

        answer.setOptionDBs(options);

        return answer;
    }

    private List<DrugDB> getAlternativeDrugsForTreatment(
            org.eyeseetea.malariacare.data.database.model.Treatment mainTreatment) {
        List<org.eyeseetea.malariacare.data.database.model.Treatment> alternativeTreatments =
                mainTreatment.getAlternativeTreatments();
        List<DrugDB> alternativeDrugs = new ArrayList<>();
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

            //TODO: set value to hide question is realized without match relation
            // because is a question of type label. This is unique and isolated case
            // when exists one more case we should refactor this to more generic code
            org.eyeseetea.malariacare.data.database.model.Treatment treatment) {
        Question treatmentQuestionSend = TreatmentQueries.getDynamicTreatmentQuestion();
        Question treatmentQuestionShow = TreatmentQueries.getTreatmentDiagnosisVisibleQuestion();
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
        treatmentQuestion.setHelp_text(TreatmentQueries.getTreatmentError());
        treatmentQuestion.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
        treatmentQuestion.setHeaderDB(HeaderDB.DYNAMIC_TREATMENT_HEADER_ID);
        questions.add(treatmentQuestion);

        return questions;
    }
}
