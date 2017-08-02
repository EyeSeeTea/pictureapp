package org.eyeseetea.malariacare.data.database.utils.populatedb;

import static org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB
        .OPTION_ATTRIBUTES_CSV;
import static org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB.QUESTIONS_CSV;
import static org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB.QUOTECHAR;
import static org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB.SEPARATOR;
import static org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateRow.populateMatch;
import static org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateRow
        .populateQuestionRelation;

import android.content.Context;

import com.opencsv.CSVReader;

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
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class UpdateDB {
    public static void updateAndAddQuestions(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.QUESTIONS_CSV);
        List<QuestionDB> questionsDB = QuestionDB.getAllQuestions();
        HashMap<Long, HeaderDB> headerHashMap = RelationsIdCsvDB.getHeaderFKRelationCsvDB(
                context);
        HashMap<Long, AnswerDB> answerHashMap = RelationsIdCsvDB.getAnswerFKRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String line[];
        int i = 0;
        //Save new option name for each option
        while ((line = reader.readNext()) != null) {
            if (i < questionsDB.size()) {
                PopulateRow.populateQuestion(line, headerHashMap, answerHashMap,
                        questionsDB.get(i)).save();
            } else {
                PopulateRow.populateQuestion(line, headerHashMap, answerHashMap, null).insert();
            }
            i++;
        }
    }


    /**
     * Method to update the old answers and add new ones.
     *
     * @param context Needed to open the csv with the answers.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateAnswers(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.ANSWERS_CSV);
        List<AnswerDB> answerDBs = AnswerDB.getAllAnswers();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.ANSWERS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        //Save new answerDBs
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < answerDBs.size()) {
                PopulateRow.populateAnswer(line, answerDBs.get(i)).save();
            } else {
                PopulateRow.populateAnswer(line, null).insert();
            }
            i++;
        }
    }

    /**
     * Method to update the old headers and add new ones from the csv. Use before inserting all
     * tabs.
     *
     * @param context Needed to open the csv with the headers.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateHeaders(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.HEADERS_CSV);
        List<HeaderDB> headerDBs = HeaderDB.getAllHeaders();
        HashMap<Long, Tab> tabIds = RelationsIdCsvDB.getTabsIdRelationsCsvDB(context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.HEADERS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < headerDBs.size()) {
                PopulateRow.populateHeader(line, tabIds, headerDBs.get(i)).save();
            } else {
                PopulateRow.populateHeader(line, tabIds, null).insert();
            }
            i++;
        }
    }

    /**
     * Method to update the old programs and add new ones from the csv.
     *
     * @param context Needed to open the csv with the programs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updatePrograms(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.PROGRAMS_CSV);
        List<ProgramDB> programDBs = ProgramDB.getAllPrograms();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.PROGRAMS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < programDBs.size()) {
                PopulateRow.populateProgram(line, programDBs.get(i)).save();
            } else {
                PopulateRow.populateProgram(line, null).insert();
            }
            i++;
        }


    }


    /**
     * Method to update the old tabs and add new ones from the csv. Use before insert all programs.
     *
     * @param context Needed to open the csv with the tabs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateTabs(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.TABS_CSV);
        List<Tab> tabs = Tab.getAllTabs();
        HashMap<Long, ProgramDB> programIds = RelationsIdCsvDB.getProgramIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.TABS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < tabs.size()) {
                PopulateRow.populateTab(line, programIds, tabs.get(i)).save();
            } else {
                PopulateRow.populateTab(line, programIds, null).insert();
            }
            i++;
        }
    }

    public static void updateMatches(Context context, boolean updateCSV) throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.MATCHES);
        }
        List<MatchDB> matchDBs = MatchDB.listAll();
        HashMap<Long, QuestionRelationDB> questionsrelationIds =
                RelationsIdCsvDB.getQuestionRelationIdRelationCsvDB(context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.MATCHES)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < matchDBs.size()) {
                populateMatch(line, questionsrelationIds, matchDBs.get(i)).save();
            } else {
                MatchDB matchDB = populateMatch(line, questionsrelationIds, null);
                matchDB.insert();
            }
            i++;
        }
    }

    public static void updateQuestionRelation(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.QUESTION_RELATIONS_CSV);
        List<QuestionRelationDB> questionRelationDBs = QuestionRelationDB.listAll();
        HashMap<Long, QuestionDB> questionIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_RELATIONS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            boolean added = false;
            if (i < questionRelationDBs.size()) {
                populateQuestionRelation(line, questionIds, questionRelationDBs.get(i)).save();

            } else {
                QuestionRelationDB questionRelationDB = populateQuestionRelation(line, questionIds,
                        null);
                questionRelationDB.insert();
            }
            i++;
        }

    }

    public static void updateQuestionOption(Context context, boolean updateCSV) throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.QUESTION_OPTIONS_CSV);
        }
        List<QuestionOptionDB> questionOptionDBs = QuestionOptionDB.listAll();
        HashMap<Long, MatchDB> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
                context);
        HashMap<Long, QuestionDB> questionsIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                context);
        HashMap<Long, OptionDB> optionsIds = RelationsIdCsvDB.getOptionIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_OPTIONS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < questionOptionDBs.size()) {
                PopulateRow.populateQuestionOption(line, questionsIds, optionsIds, matchIds,
                        questionOptionDBs.get(i)).save();
            } else {
                QuestionOptionDB questionOptionDB = PopulateRow.populateQuestionOption(line,
                        questionsIds, optionsIds, matchIds, null);
                questionOptionDB.insert();
            }
            i++;
        }

    }

    public static void updateQuestionThresholds(Context context, boolean updateCSV)
            throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.QUESTION_THRESHOLDS_CSV);
        }
        List<QuestionThresholdDB> questionThresholdDBs = QuestionThresholdDB.getAllQuestionThresholds();
        HashMap<Long, MatchDB> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
                context);
        HashMap<Long, QuestionDB> questionsIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_THRESHOLDS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < questionThresholdDBs.size()) {
                PopulateRow.populateQuestionThreshold(line, matchIds, questionsIds,
                        questionThresholdDBs.get(i)).save();
            } else {
                QuestionThresholdDB questionThresholdDB = PopulateRow.populateQuestionThreshold(line,
                        matchIds, questionsIds, null);
                questionThresholdDB.insert();
            }
        }

    }


    /**
     * Method to update drugs form csvs.
     *
     * @param context Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateDrugs(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.DRUGS_CSV);
        List<DrugDB> drugDBs = DrugDB.getAllDrugs();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.DRUGS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < drugDBs.size()) {
                PopulateRow.populateDrugs(line, drugDBs.get(i)).save();
            } else {
                PopulateRow.populateDrugs(line, null).insert();
            }
            i++;
        }
    }

    /**
     * Method to update organisations from csvs.
     *
     * @param context Needed to open the csvs.
     * @param updateCSV
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateOrganisations(Context context, boolean updateCSV) throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.PARTNER_CSV);
        }
        List<PartnerDB> partnerDBs = PartnerDB.getAllOrganisations();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.PARTNER_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < partnerDBs.size()) {
                PopulateRow.populateOrganisations(line, partnerDBs.get(i)).save();
            } else {
                PopulateRow.populateOrganisations(line, null).insert();
            }
            i++;
        }
    }

    /**
     * Method to update treatments from csvs.
     *
     * @param context Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateTreatments(Context context, boolean updateCSV) throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.TREATMENT_CSV);
        }
        List<Treatment> treatments = Treatment.getAllTreatments();
        HashMap<Long, PartnerDB> organisationIds =
                RelationsIdCsvDB.getOrganisationIdRelationCsvDB(context);
        HashMap<Long, StringKeyDB> stringKeyIds = RelationsIdCsvDB.getStringKeyIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.TREATMENT_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < treatments.size()) {
                PopulateRow.populateTreatments(line, organisationIds, stringKeyIds,
                        treatments.get(i)).save();
            } else {
                PopulateRow.populateTreatments(line, organisationIds, stringKeyIds, null).insert();
            }
            i++;
        }
    }

    /**
     * Method to Update drugCombination from csvs.
     *
     * @param context Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateDrugCombination(Context context, boolean updateCSV)
            throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.DRUG_COMBINATIONS_CSV);
        }
        List<DrugCombinationDB> drugCombinationDBs = DrugCombinationDB.getAllDrugCombination();
        HashMap<Long, DrugDB> drugIds = RelationsIdCsvDB.getDrugIdRelationCsvDB(context);
        HashMap<Long, Treatment> treatmentIds = RelationsIdCsvDB.getTreatmentIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.DRUG_COMBINATIONS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < drugCombinationDBs.size()) {
                PopulateRow.populateDrugCombinations(line, drugIds, treatmentIds,
                        drugCombinationDBs.get(i)).save();
            } else {
                PopulateRow.populateDrugCombinations(line, drugIds, treatmentIds, null).insert();
            }
        }
    }

    /**
     * Method to update treatmentMatches from csvs.
     *
     * @param context Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateTreatmentMatches(Context context, boolean updateCSV)
            throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.TREATMENT_MATCHES_CSV);
        }
        List<TreatmentMatch> treatmentMatches = TreatmentMatch.getAllTreatmentMatches();
        HashMap<Long, Treatment> treatmentIds = RelationsIdCsvDB.getTreatmentIdRelationCsvDB(
                context);
        HashMap<Long, MatchDB> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.TREATMENT_MATCHES_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < treatmentMatches.size()) {
                PopulateRow.populateTreatmentMatches(line, treatmentIds, matchIds,
                        treatmentMatches.get(i)).save();
            } else {
                PopulateRow.populateTreatmentMatches(line, treatmentIds, matchIds, null).insert();
            }
            i++;
        }
    }

    public static void updateStringKeys(Context context, boolean updateCSV) throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.STRING_KEY_CSV);
        }
        List<StringKeyDB> stringKeyDBs = StringKeyDB.getAllStringKeys();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.STRING_KEY_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < stringKeyDBs.size()) {
                PopulateRow.populateStringKey(line, stringKeyDBs.get(i)).save();
            } else {
                PopulateRow.populateStringKey(line, null).insert();
            }
            i++;
        }
    }

    public static void updateTranslations(Context context, boolean updateCsv) throws IOException {
        if (updateCsv) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.TRANSLATION_CSV);
        }
        List<Translation> translations = Translation.getAllTranslations();
        HashMap<Long, StringKeyDB> stringKeyFK = RelationsIdCsvDB.getStringKeyIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.TRANSLATION_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < translations.size()) {
                PopulateRow.populateTranslation(line, stringKeyFK, translations.get(i)).save();
            } else {
                PopulateRow.populateTranslation(line, stringKeyFK, null).insert();
            }
            i++;
        }

    }



    public static void insertLastLines(int numeberLines, Context context)
            throws IOException {
        HashMap<Long, AnswerDB> answersIds = RelationsIdCsvDB.getAnswerFKRelationCsvDB(context);
        HashMap<Long, OptionAttributeDB> optionAttributeIds =
                RelationsIdCsvDB.getOptionAttributeIdRelationCsvDB(context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.OPTIONS_CSV)),
                SEPARATOR, QUOTECHAR);
        List<String[]> lines = reader.readAll();

        for (int i = lines.size() - numeberLines - 1; i < lines.size(); i++) {
            PopulateRow.populateOption(lines.get(i), answersIds, optionAttributeIds,
                    null).insert();
        }
    }

    public static void updateQuestionOrder(Context context) throws IOException {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestions();
        CSVReader reader = new CSVReader(
                        new InputStreamReader(new PopulateDBStrategy().openFile(context, QUESTIONS_CSV)),
                        SEPARATOR, QUOTECHAR);
        String line[];
        while ((line = reader.readNext()) != null) {
            for (QuestionDB questionDB : questionDBs) {
                if(questionDB.getUid().equals(line[5])) {
                    questionDB.setOrder_pos(Integer.valueOf(line[6]));
                    questionDB.save();
                }
            }
        }
    }


    public static void updateOptionAttributes(Context context) throws IOException {
        List<OptionAttributeDB> optionsAttributes = OptionAttributeDB.getAllOptionAttributes();
        CSVReader reader = new CSVReader(
                new InputStreamReader(new PopulateDBStrategy().openFile(context, OPTION_ATTRIBUTES_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        while ((line = reader.readNext()) != null) {
            for (OptionAttributeDB optionsAttribute : optionsAttributes) {
                if(optionsAttribute.getId_option_attribute()==Integer.valueOf(line[0])) {
                    optionsAttribute.setBackground_colour(line[1]);
                    optionsAttribute.save();
                }
            }
        }
    }
}
