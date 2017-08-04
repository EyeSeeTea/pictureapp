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
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionThresholdDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TranslationDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Treatment {

    private static final String TAG = ".Treatment";
    private SurveyDB mMalariaSurvey;
    private SurveyDB mStockSurvey;
    private List<QuestionDB> mQuestionDBs;
    private org.eyeseetea.malariacare.data.database.model.TreatmentDB mTreatment;
    private HashMap<Long, Float> doseByQuestion;

    public Treatment(SurveyDB malariaSurvey, SurveyDB stockSurvey) {
        mMalariaSurvey = malariaSurvey;
        mStockSurvey = stockSurvey;
        doseByQuestion = new HashMap<>();
    }


    public List<QuestionDB> getQuestionDBs() {
        return mQuestionDBs;
    }

    public HashMap<Long, Float> getDoseByQuestion() {
        return doseByQuestion;
    }

    public org.eyeseetea.malariacare.data.database.model.TreatmentDB getTreatment() {
        return mTreatment;
    }

    public QuestionDB getACTQuestionAnsweredNo() {
        List<QuestionDB> questionDBs = mStockSurvey.getQuestionsFromValues();
        QuestionDB actQuestionDB = null;
        for (QuestionDB questionDB : questionDBs) {
            if (TreatmentQueries.isACT24Question(questionDB) || TreatmentQueries.isACT18Question(
                    questionDB) || TreatmentQueries.isACT12Question(questionDB)
                    || TreatmentQueries.isACT6Question(questionDB)) {
                actQuestionDB = questionDB;
                List<ValueDB> values = mStockSurvey.getValuesFromDB();
                for (ValueDB value : values) {
                    if(value.getQuestionDB()==null){
                        continue;
                    }
                    if (value.getQuestionDB().getId_question().equals(actQuestionDB.getId_question())) {
                        if (Float.parseFloat(value.getValue()) == 0) {
                            return actQuestionDB;
                        }
                    }
                }
            }
        }

        return null;
    }

    private org.eyeseetea.malariacare.data.database.model.TreatmentDB getTreatmentFromSurvey() {

        List<ValueDB> values = mMalariaSurvey.getValuesFromDB();

        List<MatchDB> ageMatchDBs = new ArrayList<>();
        List<MatchDB> pregnantMatchDBs = new ArrayList<>();
        List<MatchDB> severeMatchDBs = new ArrayList<>();
        List<MatchDB> rdtMatchDBs = new ArrayList<>();
        for (ValueDB value : values) {
            QuestionDB questionDB = value.getQuestionDB();
            //Getting matches for questions of age, pregnant, severe and rdt.
            if (questionDB == null) {
                continue;
            }
            if (TreatmentQueries.isAgeQuestion(questionDB)) {
                ageMatchDBs =
                        QuestionThresholdDB.getMatchesWithQuestionValue(
                                questionDB.getId_question(), Integer.parseInt(value.getValue()));
                Log.d(TAG, "age size: " + ageMatchDBs.size());
            } else if (TreatmentQueries.isSexPregnantQuestion(questionDB.getUid())) {
                pregnantMatchDBs = QuestionOptionDB.getMatchesWithQuestionOption(
                        questionDB.getId_question(),
                        value.getId_option());
                Log.d(TAG, "pregnant size: " + pregnantMatchDBs.size());
            } else if (TreatmentQueries.isSevereSymtomsQuestion(questionDB.getUid())) {
                severeMatchDBs = QuestionOptionDB.getMatchesWithQuestionOption(
                        questionDB.getId_question(),
                        value.getId_option());
                Log.d(TAG, "severe size: " + severeMatchDBs.size());
            } else if (TreatmentQueries.isRdtQuestion(questionDB.getUid())) {
                rdtMatchDBs = QuestionOptionDB.getMatchesWithQuestionOption(questionDB.getId_question(),
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
        org.eyeseetea.malariacare.data.database.model.TreatmentDB treatment = null;
        for (MatchDB treatmentMatchDB : treatmentMatchDBs) {
            if (Session.getCredentials().isDemoCredentials()) {
                return treatmentMatchDB.getTreatment();
            }
            if (treatmentMatchDB.getTreatment().getPartnerDB().getId_partner() ==
                    Session.getUserDB().getOrganisation()) {
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
            mQuestionDBs = getQuestionsForTreatment(mTreatment);
            saveTreatmentInTreatmentQuestion(mTreatment);
        }
        return mTreatment != null;
    }

    private void putACTDefaultYes() {
        QuestionDB actHiddenQuestionDB = TreatmentQueries.getDynamicTreatmentHideQuestion();
        List<ValueDB> values = Session.getMalariaSurveyDB().getValuesFromDB();
        ValueDB actValue = null;
        for (ValueDB value : values) {
            if (value.getQuestionDB().equals(actHiddenQuestionDB)) {
                actValue = value;
            }
        }
        if (actValue == null) {
            actValue = new ValueDB(TreatmentQueries.getOptionTreatmentYesCode(),
                    actHiddenQuestionDB, Session.getMalariaSurveyDB());
        } else {
            actValue.setOptionDB(TreatmentQueries.getOptionTreatmentYesCode());
        }
        actValue.save();
    }

    private List<QuestionDB> getQuestionsForTreatment(
            org.eyeseetea.malariacare.data.database.model.TreatmentDB treatment) {
        List<QuestionDB> questionDBs = new ArrayList<>();
        List<DrugDB> drugs = treatment.getDrugsForTreatment();

        QuestionDB treatmentQuestionDB = new QuestionDB();
        treatmentQuestionDB.setOutput(Constants.QUESTION_LABEL);
        treatmentQuestionDB.setForm_name(treatment.getDiagnosis().toString());
        treatmentQuestionDB.setHelp_text(treatment.getMessage().toString());
        treatmentQuestionDB.setCompulsory(QuestionDB.QUESTION_NOT_COMPULSORY);
        treatmentQuestionDB.setHeaderDB(HeaderDB.DYNAMIC_TREATMENT_HEADER_ID);
        questionDBs.add(treatmentQuestionDB);

        for (DrugDB drug : drugs) {
            QuestionDB questionDB = QuestionDB.findByUID(drug.getQuestion_code());
            if (questionDB != null) {
                if (TreatmentQueries.isPq(questionDB.getUid())) {
                    questionDB.setForm_name(TreatmentQueries.getPqTitleDose(
                            DrugCombinationDB.getDose(treatment, drug)));
                } else if (TreatmentQueries.isCq(questionDB.getUid())) {
                    questionDB.setForm_name(TreatmentQueries.getCqTitleDose(
                            DrugCombinationDB.getDose(treatment, drug)));
                }
                doseByQuestion.put(questionDB.getId_question(),
                        DrugCombinationDB.getDose(treatment, drug));
                questionDBs.add(questionDB);
            }
            if (!questionDBs.isEmpty()) {
                Log.d(TAG, "Question: " + questionDBs.get(questionDBs.size() - 1) + "\n");
            }
        }

        return questionDBs;
    }

    public HashMap<Long, Float> getOptionDose(
            org.eyeseetea.malariacare.data.database.model.TreatmentDB mainTreatment) {
        List<org.eyeseetea.malariacare.data.database.model.TreatmentDB> treatments =
                mainTreatment.getAlternativeTreatments();
        HashMap<Long, Float> optionDose = new HashMap<>();
        for (org.eyeseetea.malariacare.data.database.model.TreatmentDB treatment : treatments) {
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
            org.eyeseetea.malariacare.data.database.model.TreatmentDB mainTreatment) {
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
        QuestionDB outStockQuestionDB = TreatmentQueries.getOutOfStockQuestion();
        OptionDB optionOutStock = new OptionDB("out_stock_option", "out_stock_option", 0f,
                outStockQuestionDB.getAnswerDB());
        optionOutStock.setOptionAttributeDB(
                new OptionAttributeDB("c8b8c7", "question_images/p6_stockout.png"));
        optionOutStock.setId_option(outStockQuestionDB.getId_question());

        List<org.eyeseetea.malariacare.data.database.model.TreatmentDB> treatments =
                mainTreatment.getAlternativeTreatments();
        for (org.eyeseetea.malariacare.data.database.model.TreatmentDB treatment : treatments) {
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
            org.eyeseetea.malariacare.data.database.model.TreatmentDB mainTreatment) {
        List<org.eyeseetea.malariacare.data.database.model.TreatmentDB> alternativeTreatments =
                mainTreatment.getAlternativeTreatments();
        List<DrugDB> alternativeDrugs = new ArrayList<>();
        for (org.eyeseetea.malariacare.data.database.model.TreatmentDB treatment :
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
            org.eyeseetea.malariacare.data.database.model.TreatmentDB treatment) {
        QuestionDB treatmentQuestionDBSend = TreatmentQueries.getDynamicTreatmentQuestion();
        QuestionDB treatmentQuestionDBShow = TreatmentQueries.getTreatmentDiagnosisVisibleQuestion();
        SurveyDB malariaSurvey = Session.getMalariaSurveyDB();
        List<ValueDB> values =
                malariaSurvey.getValueDBs();//this values should be get from memory because the
        // treatment options are in memory
        boolean questionInSurvey = false;
        boolean questionShowInSurvey = false;
        String diagnosisMessage = Utils.getInternationalizedString(
                String.valueOf(treatment.getDiagnosis()));
        String defaultDiagnosisMessage = TranslationDB.getLocalizedString(treatment.getDiagnosis(),
                TranslationDB.DEFAULT_LANGUAGE);
        for (ValueDB value : values) {
            if (value.getQuestionDB() == null) {
                continue;
            }
            if (value.getQuestionDB().equals(treatmentQuestionDBSend)) {
                value.setValue(defaultDiagnosisMessage);
                questionInSurvey = true;
                value.save();
            }
            if (value.getQuestionDB().equals(treatmentQuestionDBShow)) {
                value.setValue(diagnosisMessage);
                questionShowInSurvey = true;
                value.save();
            }
        }
        if (!questionShowInSurvey) {
            ValueDB value = new ValueDB(diagnosisMessage, treatmentQuestionDBShow,
                    malariaSurvey);
            value.insert();
        }
        if (!questionInSurvey) {
            ValueDB value = new ValueDB(defaultDiagnosisMessage, treatmentQuestionDBSend,
                    malariaSurvey);
            value.insert();
        }
    }

    public List<QuestionDB> getNoTreatmentQuestions() {
        List<QuestionDB> questionDBs = new ArrayList<>();

        QuestionDB treatmentQuestionDB = new QuestionDB();
        treatmentQuestionDB.setOutput(Constants.QUESTION_LABEL);
        treatmentQuestionDB.setForm_name("");
        treatmentQuestionDB.setHelp_text(TreatmentQueries.getTreatmentError());
        treatmentQuestionDB.setCompulsory(QuestionDB.QUESTION_NOT_COMPULSORY);
        treatmentQuestionDB.setHeaderDB(HeaderDB.DYNAMIC_TREATMENT_HEADER_ID);
        questionDBs.add(treatmentQuestionDB);

        return questionDBs;
    }
}
