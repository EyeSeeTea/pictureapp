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

import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Drug;
import org.eyeseetea.malariacare.data.database.model.DrugCombination;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Match;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OptionAttribute;
import org.eyeseetea.malariacare.data.database.model.Partner;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.data.database.model.StringKey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.model.Translation;
import org.eyeseetea.malariacare.data.database.model.Treatment;
import org.eyeseetea.malariacare.data.database.model.TreatmentMatch;
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class UpdateDB {
    public static void updateAndAddQuestions(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.QUESTIONS_CSV);
        List<Question> questionsDB = Question.getAllQuestions();
        HashMap<Long, Header> headerHashMap = RelationsIdCsvDB.getHeaderFKRelationCsvDB(
                context);
        HashMap<Long, Answer> answerHashMap = RelationsIdCsvDB.getAnswerFKRelationCsvDB(
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
        List<Answer> answers = Answer.getAllAnswers();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.ANSWERS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        //Save new answers
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < answers.size()) {
                PopulateRow.populateAnswer(line, answers.get(i)).save();
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
        List<Header> headers = Header.getAllHeaders();
        HashMap<Long, Tab> tabIds = RelationsIdCsvDB.getTabsIdRelationsCsvDB(context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.HEADERS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < headers.size()) {
                PopulateRow.populateHeader(line, tabIds, headers.get(i)).save();
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
        List<Program> programs = Program.getAllPrograms();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.PROGRAMS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < programs.size()) {
                PopulateRow.populateProgram(line, programs.get(i)).save();
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
        HashMap<Long, Program> programIds = RelationsIdCsvDB.getProgramIdRelationCsvDB(
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
        List<Match> matches = Match.listAll();
        HashMap<Long, QuestionRelation> questionsrelationIds =
                RelationsIdCsvDB.getQuestionRelationIdRelationCsvDB(context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.MATCHES)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < matches.size()) {
                populateMatch(line, questionsrelationIds, matches.get(i)).save();
            } else {
                Match match = populateMatch(line, questionsrelationIds, null);
                match.insert();
            }
            i++;
        }
    }

    public static void updateQuestionRelation(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.QUESTION_RELATIONS_CSV);
        List<QuestionRelation> questionRelations = QuestionRelation.listAll();
        HashMap<Long, Question> questionIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_RELATIONS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            boolean added = false;
            if (i < questionRelations.size()) {
                populateQuestionRelation(line, questionIds, questionRelations.get(i)).save();

            } else {
                QuestionRelation questionRelation = populateQuestionRelation(line, questionIds,
                        null);
                questionRelation.insert();
            }
            i++;
        }

    }

    public static void updateQuestionOption(Context context, boolean updateCSV) throws IOException {
        if (updateCSV) {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvFromAssetsToFile(PopulateDB.QUESTION_OPTIONS_CSV);
        }
        List<QuestionOption> questionOptions = QuestionOption.listAll();
        HashMap<Long, Match> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
                context);
        HashMap<Long, Question> questionsIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                context);
        HashMap<Long, Option> optionsIds = RelationsIdCsvDB.getOptionIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_OPTIONS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < questionOptions.size()) {
                PopulateRow.populateQuestionOption(line, questionsIds, optionsIds, matchIds,
                        questionOptions.get(i)).save();
            } else {
                QuestionOption questionOption = PopulateRow.populateQuestionOption(line,
                        questionsIds, optionsIds, matchIds, null);
                questionOption.insert();
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
        List<QuestionThreshold> questionThresholds = QuestionThreshold.getAllQuestionThresholds();
        HashMap<Long, Match> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
                context);
        HashMap<Long, Question> questionsIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_THRESHOLDS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < questionThresholds.size()) {
                PopulateRow.populateQuestionThreshold(line, matchIds, questionsIds,
                        questionThresholds.get(i)).save();
            } else {
                QuestionThreshold questionThreshold = PopulateRow.populateQuestionThreshold(line,
                        matchIds, questionsIds, null);
                questionThreshold.insert();
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
        List<Drug> drugs = Drug.getAllDrugs();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.DRUGS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < drugs.size()) {
                PopulateRow.populateDrugs(line, drugs.get(i)).save();
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
        List<Partner> partners = Partner.getAllOrganisations();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.PARTNER_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < partners.size()) {
                PopulateRow.populateOrganisations(line, partners.get(i)).save();
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
        HashMap<Long, Partner> organisationIds =
                RelationsIdCsvDB.getOrganisationIdRelationCsvDB(context);
        HashMap<Long, StringKey> stringKeyIds = RelationsIdCsvDB.getStringKeyIdRelationCsvDB(
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
        List<DrugCombination> drugCombinations = DrugCombination.getAllDrugCombination();
        HashMap<Long, Drug> drugIds = RelationsIdCsvDB.getDrugIdRelationCsvDB(context);
        HashMap<Long, Treatment> treatmentIds = RelationsIdCsvDB.getTreatmentIdRelationCsvDB(
                context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.DRUG_COMBINATIONS_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < drugCombinations.size()) {
                PopulateRow.populateDrugCombinations(line, drugIds, treatmentIds,
                        drugCombinations.get(i)).save();
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
        HashMap<Long, Match> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
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
        List<StringKey> stringKeys = StringKey.getAllStringKeys();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.STRING_KEY_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < stringKeys.size()) {
                PopulateRow.populateStringKey(line, stringKeys.get(i)).save();
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
        HashMap<Long, StringKey> stringKeyFK = RelationsIdCsvDB.getStringKeyIdRelationCsvDB(
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
        HashMap<Long, Answer> answersIds = RelationsIdCsvDB.getAnswerFKRelationCsvDB(context);
        HashMap<Long, OptionAttribute> optionAttributeIds =
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
        List<Question> questions = Question.getAllQuestions();
        CSVReader reader = new CSVReader(
                        new InputStreamReader(new PopulateDBStrategy().openFile(context, QUESTIONS_CSV)),
                        SEPARATOR, QUOTECHAR);
        String line[];
        while ((line = reader.readNext()) != null) {
            for (Question question : questions) {
                if(question.getUid().equals(line[5])) {
                    question.setOrder_pos(Integer.valueOf(line[6]));
                    question.save();
                }
            }
        }
    }


    public static void updateOptionAttributes(Context context) throws IOException {
        List<OptionAttribute> optionsAttributes = OptionAttribute.getAllOptionAttributes();
        CSVReader reader = new CSVReader(
                new InputStreamReader(new PopulateDBStrategy().openFile(context, OPTION_ATTRIBUTES_CSV)),
                SEPARATOR, QUOTECHAR);
        String line[];
        while ((line = reader.readNext()) != null) {
            for (OptionAttribute optionsAttribute : optionsAttributes) {
                if(optionsAttribute.getId_option_attribute()==Integer.valueOf(line[0])) {
                    optionsAttribute.setBackground_colour(line[1]);
                    optionsAttribute.save();
                }
            }
        }
    }
}
