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
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.model.Translation;
import org.eyeseetea.malariacare.data.database.model.Treatment;
import org.eyeseetea.malariacare.data.database.model.TreatmentMatch;

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

    static HeaderDB populateHeader(String line[], HashMap<Long, Tab> tabsFK,
            @Nullable HeaderDB headerDB) {
        if (headerDB == null) {
            headerDB = new HeaderDB();
        }
        headerDB.setShort_name(line[1]);
        headerDB.setName(line[2]);
        headerDB.setOrder_pos(Integer.valueOf(line[3]));
        headerDB.setTab(tabsFK.get(Long.valueOf(line[4])));
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

    static Tab populateTab(String[] line, HashMap<Long, ProgramDB> programFK, @Nullable Tab tab) {
        if (tab == null) {
            tab = new Tab();
        }
        tab.setName(line[1]);
        tab.setOrder_pos(Integer.valueOf(line[2]));
        tab.setProgram(programFK.get(Long.valueOf(line[3])));
        tab.setType(Integer.valueOf(line[4]));
        return tab;
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
    static TreatmentMatch populateTreatmentMatches(String[] line,
            HashMap<Long, Treatment> treatmentIds,
            HashMap<Long, MatchDB> matchesIds, TreatmentMatch treatmentMatch) {
        if (treatmentMatch == null) {
            treatmentMatch = new TreatmentMatch();
        }
        treatmentMatch.setTreatment(treatmentIds.get(Long.parseLong(line[1])));
        treatmentMatch.setMatchDB(matchesIds.get(Long.parseLong(line[2])));
        return treatmentMatch;
    }

    /**
     * Method to populate each row of DrugCombinations.csv, execute after populateDrugs and
     * populateTreatments.
     *
     * @param line The row of the csv to populate.
     */
    static DrugCombinationDB populateDrugCombinations(String[] line, HashMap<Long, DrugDB> drugsFK,
            HashMap<Long, Treatment> treatmentFK, @Nullable DrugCombinationDB drugCombinationDB) {
        if (drugCombinationDB == null) {
            drugCombinationDB = new DrugCombinationDB();
        }
        drugCombinationDB.setDrugDB(drugsFK.get(Long.parseLong(line[1])));
        drugCombinationDB.setTreatment(treatmentFK.get(Long.parseLong(line[2])));
        drugCombinationDB.setDose(Float.parseFloat(line[3]));
        return drugCombinationDB;
    }

    /**
     * Method to populate each row of Treatment.csv, execute after populateOrganisations.
     *
     * @param line The row of the csv to populate.
     * @param stringKeyList
     */
    static Treatment populateTreatments(String[] line, HashMap<Long, PartnerDB> organisationFK,
            HashMap<Long, StringKeyDB> stringKeyList, @Nullable Treatment treatment) {
        if (treatment == null) {
            treatment = new Treatment();
        }
        treatment.setOrganisation(organisationFK.get(Long.parseLong(line[1])));
        treatment.setDiagnosis(stringKeyList.get(Long.valueOf(line[2])).getId_string_key());
        treatment.setMessage(stringKeyList.get(Long.valueOf(line[3])).getId_string_key());
        treatment.setType(Integer.parseInt(line[4]));
        return treatment;
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

    public static Translation populateTranslation(String[] line,
            HashMap<Long, StringKeyDB> stringKeyFK,
            Translation translation) {
        if (translation == null) {
            translation = new Translation();
        }
        translation.setId_string_key(stringKeyFK.get(Long.valueOf(line[1])).getId_string_key());
        translation.setTranslation(line[2]);
        translation.setLanguage(line[3]);
        return translation;
    }
}
