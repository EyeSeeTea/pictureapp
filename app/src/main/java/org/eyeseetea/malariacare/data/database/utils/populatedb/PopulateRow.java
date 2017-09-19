package org.eyeseetea.malariacare.data.database.utils.populatedb;

import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.DrugDB;
import org.eyeseetea.malariacare.data.database.model.DrugCombinationDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.PartnerDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.QuestionThresholdDB;
import org.eyeseetea.malariacare.data.database.model.StringKeyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.model.TranslationDB;
import org.eyeseetea.malariacare.data.database.model.TreatmentDB;
import org.eyeseetea.malariacare.data.database.model.TreatmentMatchDB;

import java.util.HashMap;

public class PopulateRow {
    static QuestionDB populateQuestion(String[] line, HashMap<Long, HeaderDB> headerFK,
            HashMap<Long, AnswerDB> answerFK, @Nullable QuestionDB questionDB) {
        if (questionDB == null) {
            questionDB = new QuestionDB();
        }
        questionDB.setCode(line[1]);
        questionDB.setDe_name(line[2]);
        questionDB.setHelp_text(line[3]);
        questionDB.setForm_name(line[4]);
        questionDB.setUid(line[5]);
        questionDB.setOrder_pos(Integer.valueOf(line[6]));
        questionDB.setNumerator_w(Float.valueOf(line[7]));
        questionDB.setDenominator_w(Float.valueOf(line[8]));
        questionDB.setHeader(headerFK.get(Long.valueOf(line[9])));
        if (!line[10].equals("")) {
            questionDB.setAnswer(answerFK.get(Long.valueOf(line[10])));
        }
        questionDB.setOutput(Integer.valueOf(line[12]));
        questionDB.setTotalQuestions(Integer.valueOf(line[13]));
        questionDB.setVisible(Integer.valueOf(line[14]));
        if (line.length > 15 && !line[15].equals("")) {
            questionDB.setPath((line[15]));
        }
        if (line.length > 16 && !line[16].equals("")) {
            questionDB.setCompulsory(Integer.valueOf(line[16]));
        } else {
            questionDB.setCompulsory(QuestionDB.QUESTION_NOT_COMPULSORY);
        }
        return questionDB;
    }

    static AnswerDB populateAnswer(String line[], @Nullable AnswerDB answerDB) {
        if (answerDB == null) {
            answerDB = new AnswerDB();
        }
        answerDB.setName(line[1]);
        return answerDB;
    }

    static HeaderDB populateHeader(String line[], HashMap<Long, TabDB> tabsFK,
            @Nullable HeaderDB headerDB) {
        if (headerDB == null) {
            headerDB = new HeaderDB();
        }
        headerDB.setShort_name(line[1]);
        headerDB.setName(line[2]);
        headerDB.setOrder_pos(Integer.valueOf(line[3]));
        headerDB.setTabDB(tabsFK.get(Long.valueOf(line[4])));
        return headerDB;
    }

    static ProgramDB populateProgram(String[] line, @Nullable ProgramDB programDB) {
        if (programDB == null) {
            programDB = new ProgramDB();
        }
        programDB.setUid(line[1]);
        programDB.setName(line[2]);
        return programDB;
    }

    static TabDB populateTab(String[] line, HashMap<Long, ProgramDB> programFK, @Nullable TabDB tabDB) {
        if (tabDB == null) {
            tabDB = new TabDB();
        }
        tabDB.setName(line[1]);
        tabDB.setOrder_pos(Integer.valueOf(line[2]));
        tabDB.setProgram(programFK.get(Long.valueOf(line[3])));
        tabDB.setType(Integer.valueOf(line[4]));
        return tabDB;
    }

    static MatchDB populateMatch(String line[],
            HashMap<Long, QuestionRelationDB> questionRelationFK, @Nullable MatchDB matchDB) {
        if (matchDB == null) {
            matchDB = new MatchDB();
        }
        matchDB.setQuestionRelationDB(questionRelationFK.get(Long.valueOf(line[1])));
        return matchDB;
    }


    static QuestionThresholdDB populateQuestionThreshold(String[] line,
            HashMap<Long, MatchDB> matchesFK, HashMap<Long, QuestionDB> quetiosnFK,
            @Nullable QuestionThresholdDB questionThresholdDB) {
        if (questionThresholdDB == null) {
            questionThresholdDB = new QuestionThresholdDB();
        }
        questionThresholdDB.setMatchDB(matchesFK.get(Long.valueOf(line[1])));
        questionThresholdDB.setQuestionDB(quetiosnFK.get(Long.valueOf(line[2])));
        if (!line[3].equals("")) {
            questionThresholdDB.setMinValue(Integer.valueOf(line[3]));
        }
        if (!line[4].equals("")) {
            questionThresholdDB.setMaxValue(Integer.valueOf(line[4]));
        }
        return questionThresholdDB;
    }

    static QuestionOptionDB populateQuestionOption(String[] line, HashMap<Long, QuestionDB> questionFK,
            HashMap<Long, OptionDB> optionFK, HashMap<Long, MatchDB> matchFK,
            @Nullable QuestionOptionDB questionOptionDB) {
        if (questionOptionDB == null) {
            questionOptionDB = new QuestionOptionDB();
        }
        questionOptionDB.setQuestion(questionFK.get(Long.valueOf(line[1])));
        questionOptionDB.setOption(optionFK.get(Long.valueOf(line[2])));
        if (!line[3].equals("")) {
            questionOptionDB.setMatch(matchFK.get(Long.valueOf(line[3])));
        }
        return questionOptionDB;
    }

    static QuestionRelationDB populateQuestionRelation(String[] line,
            HashMap<Long, QuestionDB> questionFK,
            @Nullable QuestionRelationDB questionRelationDB) {
        if (questionRelationDB == null) {
            questionRelationDB = new QuestionRelationDB();
        }
        questionRelationDB.setOperation(Integer.valueOf(line[1]));
        questionRelationDB.setQuestionDB(questionFK.get(Long.valueOf(line[2])));
        return questionRelationDB;
    }

    /**
     * Method to populate each row of TreatmentMatches.csv, execute after populateTreatments and
     * populateMatches.
     *
     * @param line The row of the csv to populate.
     */
    static TreatmentMatchDB populateTreatmentMatches(String[] line,
            HashMap<Long, TreatmentDB> treatmentIds,
            HashMap<Long, MatchDB> matchesIds, TreatmentMatchDB treatmentMatchDB) {
        if (treatmentMatchDB == null) {
            treatmentMatchDB = new TreatmentMatchDB();
        }
        treatmentMatchDB.setTreatmentDB(treatmentIds.get(Long.parseLong(line[1])));
        treatmentMatchDB.setMatchDB(matchesIds.get(Long.parseLong(line[2])));
        return treatmentMatchDB;
    }

    /**
     * Method to populate each row of DrugCombinations.csv, execute after populateDrugs and
     * populateTreatments.
     *
     * @param line The row of the csv to populate.
     */
    static DrugCombinationDB populateDrugCombinations(String[] line, HashMap<Long, DrugDB> drugsFK,
            HashMap<Long, TreatmentDB> treatmentFK, @Nullable DrugCombinationDB drugCombinationDB) {
        if (drugCombinationDB == null) {
            drugCombinationDB = new DrugCombinationDB();
        }
        drugCombinationDB.setDrugDB(drugsFK.get(Long.parseLong(line[1])));
        drugCombinationDB.setTreatmentDB(treatmentFK.get(Long.parseLong(line[2])));
        drugCombinationDB.setDose(Float.parseFloat(line[3]));
        return drugCombinationDB;
    }

    /**
     * Method to populate each row of TreatmentDB.csv, execute after populateOrganisations.
     *
     * @param line The row of the csv to populate.
     * @param stringKeyList
     */
    static TreatmentDB populateTreatments(String[] line, HashMap<Long, PartnerDB> organisationFK,
            HashMap<Long, StringKeyDB> stringKeyList, @Nullable TreatmentDB treatmentDB) {
        if (treatmentDB == null) {
            treatmentDB = new TreatmentDB();
        }
        treatmentDB.setOrganisation(organisationFK.get(Long.parseLong(line[1])));
        treatmentDB.setDiagnosis(stringKeyList.get(Long.valueOf(line[2])).getId_string_key());
        treatmentDB.setMessage(stringKeyList.get(Long.valueOf(line[3])).getId_string_key());
        treatmentDB.setType(Integer.parseInt(line[4]));
        return treatmentDB;
    }

    /**
     * Method to populate each row of Partner.csv.
     *
     * @param line The row of the csv to populate.
     */
    static PartnerDB populateOrganisations(String[] line, @Nullable PartnerDB partnerDB) {
        if (partnerDB == null) {
            partnerDB = new PartnerDB();
        }
        partnerDB.setUid(line[1]);
        partnerDB.setName(line[2]);
        return partnerDB;
    }

    /**
     * Method to populate the Drugs.csv.
     *
     * @param line The row of the csv to add to db.
     */
    static DrugDB populateDrugs(String line[], @Nullable DrugDB drugDB) {
        if (drugDB == null) {
            drugDB = new DrugDB();
        }
        drugDB.setName(line[1]);
        drugDB.setQuestion_code(line[2]);
        return drugDB;
    }

    public static OptionDB populateOption(String[] line, HashMap<Long, AnswerDB> answerFK,
            HashMap<Long, OptionAttributeDB> optionAttributeFK, @Nullable OptionDB optionDB) {
        if (optionDB == null) {
            optionDB = new OptionDB();
        }
        optionDB.setName(line[1]);
        optionDB.setCode(line[2]);
        optionDB.setFactor(Float.valueOf(line[3]));
        optionDB.setAnswerDB(answerFK.get(Long.valueOf(line[4])));
        if (line[5] != null && !line[5].isEmpty()) {
            optionDB.setOptionAttributeDB(
                    optionAttributeFK.get(Long.valueOf(line[5])));
        }
        return optionDB;
    }

    static StringKeyDB populateStringKey(String[] line, @Nullable StringKeyDB stringKeyDB) {
        if (stringKeyDB == null) {
            stringKeyDB = new StringKeyDB();
        }
        stringKeyDB.setKey(line[1]);
        return stringKeyDB;
    }

    public static TranslationDB populateTranslation(String[] line,
            HashMap<Long, StringKeyDB> stringKeyFK,
            TranslationDB translationDB) {
        if (translationDB == null) {
            translationDB = new TranslationDB();
        }
        translationDB.setId_string_key(stringKeyFK.get(Long.valueOf(line[1])).getId_string_key());
        translationDB.setTranslation(line[2]);
        translationDB.setLanguage(line[3]);
        return translationDB;
    }
}
