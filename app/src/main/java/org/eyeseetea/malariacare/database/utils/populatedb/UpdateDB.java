package org.eyeseetea.malariacare.database.utils.populatedb;

import static org.eyeseetea.malariacare.database.utils.populatedb.PopulateRow.populateMatch;
import static org.eyeseetea.malariacare.database.utils.populatedb.PopulateRow
        .populateQuestionRelation;

import android.content.res.AssetManager;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Drug;
import org.eyeseetea.malariacare.database.model.DrugCombination;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.Organisation;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Treatment;
import org.eyeseetea.malariacare.database.model.TreatmentMatch;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * Created by manuel on 11/01/17.
 */

public class UpdateDB {


    public static void updateAndAddQuestions(AssetManager assetManager) throws IOException {
        List<Question> questionsDB = Question.getAllQuestions();
        HashMap<Long, Header> headerHashMap = RelationsIdCsvDB.getHeaderFKRelationCsvDB(
                assetManager);
        HashMap<Long, Answer> answerHashMap = RelationsIdCsvDB.getAnswerFKRelationCsvDB(
                assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.QUESTIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);

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
     * @param assetManager Needed to open the csv with the answers.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateAnswers(AssetManager assetManager) throws IOException {
        List<Answer> answers = Answer.getAllAnswers();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.ANSWERS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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
     * @param assetManager Needed to open the csv with the headers.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateHeaders(AssetManager assetManager) throws IOException {
        List<Header> headers = Header.getAllHeaders();
        HashMap<Long, Tab> tabIds = RelationsIdCsvDB.getTabsIdRelationsCsvDB(assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.HEADERS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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
     * @param assetManager Needed to open the csv with the programs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updatePrograms(AssetManager assetManager) throws IOException {
        List<Program> programs = Program.getAllPrograms();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.PROGRAMS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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
     * @param assetManager Needed to open the csv with the tabs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateTabs(AssetManager assetManager) throws IOException {
        List<Tab> tabs = Tab.getAllTabs();
        HashMap<Long, Program> programIds = RelationsIdCsvDB.getProgramIdRelationCsvDB(
                assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.TABS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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

    public static void updateMatches(AssetManager assetManager) throws IOException {
        List<Match> matches = Match.listAll();
        HashMap<Long, QuestionRelation> questionsrelationIds =
                RelationsIdCsvDB.getQuestionRelationIdRelationCsvDB(assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.MATCHES)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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

    public static void updateQuestionRelation(AssetManager assetManager) throws IOException {
        List<QuestionRelation> questionRelations = QuestionRelation.listAll();
        HashMap<Long, Question> questionIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.QUESTION_RELATIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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

    public static void updateQuestionOption(AssetManager assetManager) throws IOException {
        List<QuestionOption> questionOptions = QuestionOption.listAll();
        HashMap<Long, Match> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
                assetManager);
        HashMap<Long, Question> questionsIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                assetManager);
        HashMap<Long, Option> optionsIds = RelationsIdCsvDB.getOptionIdRelationCsvDB(
                assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.QUESTION_OPTIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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

    public static void updateQuestionThresholds(AssetManager assetManager) throws IOException {
        List<QuestionThreshold> questionThresholds = QuestionThreshold.getAllQuestionThresholds();
        HashMap<Long, Match> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
                assetManager);
        HashMap<Long, Question> questionsIds = RelationsIdCsvDB.getQuestionIdRelationCsvDB(
                assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.QUESTION_THRESHOLDS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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
     * @param assetManager Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateDrugs(AssetManager assetManager) throws IOException {
        List<Drug> drugs = Drug.getAllDrugs();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.DRUGS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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
     * @param assetManager Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateOrganisations(AssetManager assetManager) throws IOException {
        List<Organisation> organisations = Organisation.getAllOrganisations();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.ORGANISATIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < organisations.size()) {
                PopulateRow.populateOrganisations(line, organisations.get(i)).save();
            } else {
                PopulateRow.populateOrganisations(line, null).insert();
            }
            i++;
        }
    }

    /**
     * Method to update treatments from csvs.
     *
     * @param assetManager Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateTreatments(AssetManager assetManager) throws IOException {
        List<Treatment> treatments = Treatment.getAllTreatments();
        HashMap<Long, Organisation> organisationIds =
                RelationsIdCsvDB.getOrganisationIdRelationCsvDB(assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.TREATMENT_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < treatments.size()) {
                PopulateRow.populateTreatments(line, organisationIds, treatments.get(i)).save();
            } else {
                PopulateRow.populateTreatments(line, organisationIds, null).insert();
            }
            i++;
        }
    }

    /**
     * Method to Update drugCombination from csvs.
     *
     * @param assetManager Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateDrugCombination(AssetManager assetManager) throws IOException {
        List<DrugCombination> drugCombinations = DrugCombination.getAllDrugCombination();
        HashMap<Long, Drug> drugIds = RelationsIdCsvDB.getDrugIdRelationCsvDB(assetManager);
        HashMap<Long, Treatment> treatmentIds = RelationsIdCsvDB.getTreatmentIdRelationCsvDB(
                assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.DRUG_COMBINATIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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
     * @param assetManager Needed to open the csvs.
     * @throws IOException If there is a problem opening the csv.
     */
    public static void updateTreatmentMatches(AssetManager assetManager) throws IOException {
        List<TreatmentMatch> treatmentMatches = TreatmentMatch.getAllTreatmentMatches();
        HashMap<Long, Treatment> treatmentIds = RelationsIdCsvDB.getTreatmentIdRelationCsvDB(
                assetManager);
        HashMap<Long, Match> matchIds = RelationsIdCsvDB.getMatchIdRelationCsvDB(
                assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.TREATMENT_MATCHES_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
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

    public static void updateOptions(AssetManager assetManager) throws IOException {
        List<Option> optionToDelete = Question.getOptions("4YY1JoHmO94");
        for (Option option : optionToDelete) {
            option.delete();
        }
        List<Option> options = Option.getAllOptions();
        HashMap<Long, Answer> answersIds = RelationsIdCsvDB.getAnswerFKRelationCsvDB(assetManager);
        HashMap<Long, OptionAttribute> optionAttributeIds =
                RelationsIdCsvDB.getOptionAttributeIdRelationCsvDB(assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.OPTIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < options.size()) {
                PopulateRow.populateOption(line, answersIds, optionAttributeIds,
                        options.get(i)).save();
            } else {
                PopulateRow.populateOption(line, answersIds, optionAttributeIds,
                        null).insert();
            }
            i++;
        }

        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();
        for (OrgUnit orgUnit : orgUnits) {
            Option option = new Option();
            option.setCode(orgUnit.getName());
            option.setName(orgUnit.getUid());
            option.setFactor((float) 0);
            option.setId_option((long) 0);
            option.setAnswer(Question.getAnswer("4YY1JoHmO94"));
            option.save();
        }

    }

    public static void insertLastLines(int numeberLines, AssetManager assetManager)
            throws IOException {
        HashMap<Long, Answer> answersIds = RelationsIdCsvDB.getAnswerFKRelationCsvDB(assetManager);
        HashMap<Long, OptionAttribute> optionAttributeIds =
                RelationsIdCsvDB.getOptionAttributeIdRelationCsvDB(assetManager);
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.OPTIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        List<String[]> lines = reader.readAll();

        for (int i = lines.size() - numeberLines - 1; i < lines.size(); i++) {
            PopulateRow.populateOption(lines.get(i), answersIds, optionAttributeIds,
                    null).insert();
        }
    }


}
