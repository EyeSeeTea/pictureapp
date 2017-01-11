package org.eyeseetea.malariacare.database.utils.populatedb;

import android.content.res.AssetManager;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Tab;

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
        HashMap<Long, Header> headerHashMap = CreateRelationsIdCsvDB.getHeaderFKRelationCsvDB(
                assetManager);
        HashMap<Long, Answer> answerHashMap = CreateRelationsIdCsvDB.getAnswerFKRelationCsvDB(
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
        HashMap<Long, Tab> tabIds = CreateRelationsIdCsvDB.getTabsIdRelationsCsvDB(assetManager);
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
        HashMap<Long, Program> programIds = CreateRelationsIdCsvDB.getProgramIdRelationCsvDB(
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


}
