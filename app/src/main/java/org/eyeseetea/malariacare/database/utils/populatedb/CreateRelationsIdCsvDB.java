package org.eyeseetea.malariacare.database.utils.populatedb;

import android.content.res.AssetManager;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Drug;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Organisation;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Treatment;

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
        for (int i = 0; i < headers.size() && i < csvIds.size(); i++) {
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
        for (int i = 0; i < answers.size() && i < csvIds.size(); i++) {
            answerFK.put(csvIds.get(i), answers.get(i));
        }
        return answerFK;
    }
    static HashMap<Long,Tab> getTabsIdRelationsCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Tab> tabFK = new HashMap<>();
        List<Tab> tabs = Tab.getAllTabs();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(PopulateDB.TABS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < tabs.size() && i < csvIds.size(); i++) {
            tabFK.put(csvIds.get(i), tabs.get(i));
        }
        return tabFK;
    }

    static HashMap<Long,Program> getProgramIdRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Program> programFK = new HashMap<>();
        List<Program> programs = Program.getAllPrograms();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(PopulateDB.PROGRAMS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < programs.size() && i < csvIds.size(); i++) {
            programFK.put(csvIds.get(i), programs.get(i));
        }
        return programFK;
    }

    static HashMap<Long, QuestionRelation> getQuestionRelationIdRelationCsvDB(
            AssetManager assetManager)
            throws IOException {
        HashMap<Long, QuestionRelation> questionRelationsFK = new HashMap<>();
        List<QuestionRelation> questionRelations = QuestionRelation.listAll();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.QUESTION_RELATIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < questionRelations.size() && i < csvIds.size(); i++) {
            questionRelationsFK.put(csvIds.get(i), questionRelations.get(i));
        }
        return questionRelationsFK;
    }

    static HashMap<Long, Question> getQuestionIdRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Question> questionFK = new HashMap<>();
        List<Question> questions = Question.getAllQuestions();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.QUESTIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < questions.size() && i < csvIds.size(); i++) {
            questionFK.put(csvIds.get(i), questions.get(i));
        }
        return questionFK;
    }

    static HashMap<Long, Option> getOptionIdRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Option> optionFK = new HashMap<>();
        List<Option> options = Option.getAllOptions();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.OPTIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < options.size() && i < csvIds.size(); i++) {
            optionFK.put(csvIds.get(i), options.get(i));
        }
        return optionFK;
    }

    static HashMap<Long, Match> getMatchIdRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Match> matchesFK = new HashMap<>();
        List<Match> matches = Match.listAll();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.MATCHES)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < matches.size() && i < csvIds.size(); i++) {
            matchesFK.put(csvIds.get(i), matches.get(i));
        }
        return matchesFK;
    }

    static HashMap<Long, Organisation> getOrganisationIdRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Organisation> organisationFK = new HashMap<>();
        List<Organisation> organisations = Organisation.getAllOrganisations();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.ORGANISATIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < organisations.size() && i < csvIds.size(); i++) {
            organisationFK.put(csvIds.get(i), organisations.get(i));
        }
        return organisationFK;
    }

    static HashMap<Long, Drug> getDrugIdRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Drug> drugFK = new HashMap<>();
        List<Drug> drugs = Drug.getAllDrugs();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.DRUGS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < drugs.size() && i < csvIds.size(); i++) {
            drugFK.put(csvIds.get(i), drugs.get(i));
        }
        return drugFK;
    }

    static HashMap<Long, Treatment> getTreatmentIdRelationCsvDB(AssetManager assetManager)
            throws IOException {
        HashMap<Long, Treatment> treatmentFK = new HashMap<>();
        List<Treatment> treatments = Treatment.getAllTreatments();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(PopulateDB.TREATMENT_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < treatments.size() && i < csvIds.size(); i++) {
            treatmentFK.put(csvIds.get(i), treatments.get(i));
        }
        return treatmentFK;
    }

}
