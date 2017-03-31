package org.eyeseetea.malariacare.data.database.utils.populatedb;


import android.content.Context;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Drug;
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
import org.eyeseetea.malariacare.data.database.model.Treatment;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by manuel on 11/01/17.
 */

public class RelationsIdCsvDB {
    static HashMap<Long, Header> getHeaderFKRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Header> headerFK = new HashMap<>();
        List<Header> headers = Header.getAllHeaders();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.HEADERS_CSV)),
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

    public static HashMap<Long, Answer> getAnswerFKRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Answer> answerFK = new HashMap<>();
        List<Answer> answers = Answer.getAllAnswers();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.ANSWERS_CSV)),
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

    static HashMap<Long, Tab> getTabsIdRelationsCsvDB(Context context)
            throws IOException {
        HashMap<Long, Tab> tabFK = new HashMap<>();
        List<Tab> tabs = Tab.getAllTabs();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.TABS_CSV)),
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

    static HashMap<Long, Program> getProgramIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Program> programFK = new HashMap<>();
        List<Program> programs = Program.getAllPrograms();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.PROGRAMS_CSV)),
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
            Context context)
            throws IOException {
        HashMap<Long, QuestionRelation> questionRelationsFK = new HashMap<>();
        List<QuestionRelation> questionRelations = QuestionRelation.listAll();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_RELATIONS_CSV)),
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

    static HashMap<Long, Question> getQuestionIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Question> questionFK = new HashMap<>();
        List<Question> questions = Question.getAllQuestions();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTIONS_CSV)),
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

    static HashMap<Long, Option> getOptionIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Option> optionFK = new HashMap<>();
        List<Option> options = Option.getAllOptions();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.OPTIONS_CSV)),
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

    static HashMap<Long, Match> getMatchIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Match> matchesFK = new HashMap<>();
        List<Match> matches = Match.listAll();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.MATCHES)),
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

    static HashMap<Long, Long> getMatchIdRelationDBCsv(Context context)
            throws IOException {
        HashMap<Long, Long> matchesFK = new HashMap<>();
        List<Match> matches = Match.listAll();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.MATCHES)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < matches.size() && i < csvIds.size(); i++) {
            matchesFK.put(matches.get(i).getId_match(), csvIds.get(i));
        }
        return matchesFK;
    }

    static HashMap<Long, Long> getQuestionOptionIdRelationDBCsv(Context context)
            throws IOException {
        HashMap<Long, Long> questionOptionFK = new HashMap<>();
        List<QuestionOption> questionOptions = QuestionOption.listAll();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_OPTIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < questionOptions.size() && i < csvIds.size(); i++) {
            questionOptionFK.put(questionOptions.get(i).getId_question_option(), csvIds.get(i));
        }
        return questionOptionFK;
    }

    static HashMap<Long, Long> getQuestionThresholdIdRelationDBCsv(Context context)
            throws IOException {
        HashMap<Long, Long> questionThresholdFK = new HashMap<>();
        List<QuestionThreshold> matches = QuestionThreshold.getAllQuestionThresholds();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.QUESTION_THRESHOLDS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < matches.size() && i < csvIds.size(); i++) {
            questionThresholdFK.put(matches.get(i).getId_question_threshold(), csvIds.get(i));
        }
        return questionThresholdFK;
    }

    static HashMap<Long, Partner> getOrganisationIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Partner> organisationFK = new HashMap<>();
        List<Partner> partners = Partner.getAllOrganisations();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.PARTNER_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < partners.size() && i < csvIds.size(); i++) {
            organisationFK.put(csvIds.get(i), partners.get(i));
        }
        return organisationFK;
    }

    static HashMap<Long, StringKey> getStringKeyIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, StringKey> stringKeyFK = new HashMap<>();
        List<StringKey> stringKeys = StringKey.getAllStringKeys();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.STRING_KEY_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < stringKeys.size() && i < csvIds.size(); i++) {
            stringKeyFK.put(csvIds.get(i), stringKeys.get(i));
        }
        return stringKeyFK;
    }

    static HashMap<Long, Object> getIdRelationCsvDB(Context context, String CsvName,
            List<Object> allDBObjects)
            throws IOException {
        HashMap<Long, Object> objectFK = new HashMap<>();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(CsvName)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < allDBObjects.size() && i < csvIds.size(); i++) {
            objectFK.put(csvIds.get(i), allDBObjects.get(i));
        }
        return objectFK;
    }


    static HashMap<Long, Drug> getDrugIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Drug> drugFK = new HashMap<>();
        List<Drug> drugs = Drug.getAllDrugs();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.DRUGS_CSV)),
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

    static HashMap<Long, Treatment> getTreatmentIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, Treatment> treatmentFK = new HashMap<>();
        List<Treatment> treatments = Treatment.getAllTreatments();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.TREATMENT_CSV)),
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

    public static HashMap<Long, OptionAttribute> getOptionAttributeIdRelationCsvDB(Context context)
            throws IOException {
        HashMap<Long, OptionAttribute> optionAttributeFK = new HashMap<>();
        List<OptionAttribute> optionAttributes = OptionAttribute.getAllOptionAttributes();
        List<Long> csvIds = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.OPTION_ATTRIBUTES_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] idToAdd;
        while ((idToAdd = reader.readNext()) != null) {
            csvIds.add(Long.parseLong(idToAdd[0]));
        }
        for (int i = 0; i < optionAttributes.size() && i < csvIds.size(); i++) {
            optionAttributeFK.put(csvIds.get(i), optionAttributes.get(i));
        }
        return optionAttributeFK;
    }

}
