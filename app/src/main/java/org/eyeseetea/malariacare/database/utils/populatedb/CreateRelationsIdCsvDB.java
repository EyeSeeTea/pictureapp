package org.eyeseetea.malariacare.database.utils.populatedb;

import android.content.res.AssetManager;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Tab;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by manuel on 11/01/17.
 */

public class CreateRelationsIdCsvDB {
    static HashMap<Long, Header> getHeaderFKRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Header> headerFK = new HashMap<>();
        List<Header> headers = Header.getAllHeaders();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(PopulateDB.HEADERS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < headers.size(); i++) {
            headerFK.put(csvIds.get(i), headers.get(i));
        }
        return headerFK;
    }

    static HashMap<Long, Answer> getAnswerFKRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Answer> answerFK = new HashMap<>();
        List<Answer> answers = Answer.getAllAnswers();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(PopulateDB.ANSWERS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < answers.size(); i++) {
            answerFK.put(csvIds.get(i), answers.get(i));
        }
        return answerFK;
    }
    static HashMap<Long,Tab> getTabsIdRelationsCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Tab> tabFK = new HashMap<>();
        List<Tab> headers = Tab.getAllTabs();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(PopulateDB.TABS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < headers.size(); i++) {
            tabFK.put(csvIds.get(i), headers.get(i));
        }
        return tabFK;
    }

    static HashMap<Long,Program> getProgramIdRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Program> programFK = new HashMap<>();
        List<Program> headers = Program.getAllPrograms();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(PopulateDB.PROGRAMS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < headers.size(); i++) {
            programFK.put(csvIds.get(i), headers.get(i));
        }
        return programFK;
    }
}
