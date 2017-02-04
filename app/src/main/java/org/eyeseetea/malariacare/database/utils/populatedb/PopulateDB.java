/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.eyeseetea.malariacare.database.utils.populatedb;

import android.content.res.AssetManager;
import android.util.Log;

import com.opencsv.CSVReader;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Drug;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.OrgUnitLevel;
import org.eyeseetea.malariacare.database.model.Organisation;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Score;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Treatment;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PopulateDB {

    public static final String PROGRAMS_CSV = "Programs.csv";
    public static final String TABS_CSV = "Tabs.csv";
    public static final String HEADERS_CSV = "Headers.csv";
    public static final String ANSWERS_CSV = "Answers.csv";
    public static final String OPTION_ATTRIBUTES_CSV = "OptionAttributes.csv";
    public static final String OPTIONS_CSV = "Options.csv";
    public static final String QUESTIONS_CSV = "Questions.csv";
    public static final String QUESTION_OPTIONS_CSV = "QuestionOptions.csv";
    public static final String MATCHES = "Matches.csv";
    public static final String QUESTION_RELATIONS_CSV = "QuestionRelations.csv";
    public static final String QUESTION_THRESHOLDS_CSV = "QuestionThresholds.csv";
    public static final String DRUG_COMBINATIONS_CSV = "DrugCombinations.csv";
    public static final String DRUGS_CSV = "Drugs.csv";
    public static final String ORGANISATIONS_CSV = "Organisations.csv";
    public static final String TREATMENT_MATCHES_CSV = "TreatmentMatches.csv";
    public static final String TREATMENT_CSV = "Treatments.csv";

    public static final String ORG_UNIT_LEVEL_CSV = "OrgUnitLevel.csv";
    public static final String ORG_UNIT_CSV = "OrgUnit.csv";
    public static final char SEPARATOR = ';';
    public static final char QUOTECHAR = '\'';
    private static final List<String> tables2populate = Arrays.asList(
            PROGRAMS_CSV,
            TABS_CSV,
            HEADERS_CSV,
            ANSWERS_CSV,
            OPTION_ATTRIBUTES_CSV,
            OPTIONS_CSV,
            QUESTIONS_CSV,
            QUESTION_RELATIONS_CSV,
            MATCHES,
            QUESTION_OPTIONS_CSV,
            QUESTION_THRESHOLDS_CSV,
            DRUGS_CSV,
            ORGANISATIONS_CSV,
            TREATMENT_CSV,
            DRUG_COMBINATIONS_CSV,
            TREATMENT_MATCHES_CSV);
    private static final List<String> tables2updateQuestions = Arrays.asList(
            OPTION_ATTRIBUTES_CSV,
            OPTIONS_CSV,
            QUESTIONS_CSV,
            QUESTION_RELATIONS_CSV,
            MATCHES,
            QUESTION_OPTIONS_CSV);
    private static final List<String> tables2populateDummy = Arrays.asList(
            ORG_UNIT_LEVEL_CSV,
            ORG_UNIT_CSV);
    private static final String TAG = "PopulateDB";

    static Map<Integer, Program> programList = new LinkedHashMap<Integer, Program>();
    static Map<Integer, Tab> tabList = new LinkedHashMap<Integer, Tab>();
    static Map<Integer, Header> headerList = new LinkedHashMap<Integer, Header>();
    static Map<Integer, Question> questionList = new LinkedHashMap<Integer, Question>();
    static Map<Integer, OptionAttribute> optionAttributeList =
            new LinkedHashMap<Integer, OptionAttribute>();
    static Map<Integer, Option> optionList = new LinkedHashMap<Integer, Option>();
    static Map<Integer, Answer> answerList = new LinkedHashMap<Integer, Answer>();
    static Map<Integer, QuestionRelation> questionRelationList = new LinkedHashMap();
    static HashMap<Long, Match> matchList = new HashMap();

    static Map<Integer, OrgUnitLevel> orgUnitLevelList = new LinkedHashMap();
    static Map<Integer, OrgUnit> orgUnitList = new LinkedHashMap();
    static HashMap<Long, Drug> drugList = new HashMap<>();
    static HashMap<Long, Organisation> organisationList = new HashMap<>();
    static HashMap<Long, Treatment> treatmentList = new HashMap<>();

    public static void initDataIfRequired(AssetManager assetManager) throws IOException {
        if (!Tab.isEmpty()) {
            Log.i(TAG, "DB Already loaded, showing surveys...");
            return;
        }

        Log.i(TAG, "DB empty, loading data ...");
        try {
            PopulateDB.populateDB(assetManager);
            //Get maximum total of questions
            Session.setMaxTotalQuestions(Program.getMaxTotalQuestions());
        } catch (IOException e) {
            throw e;
        }
        Log.i(TAG, "DB empty, loading data ...DONE");
    }

    public static void populateDB(AssetManager assetManager) throws IOException {

        //Reset inner references
        cleanInnerLists();
        for (String table : tables2populate) {
            Log.i(TAG, "Loading csv: " + table);
            CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(table)),
                    SEPARATOR, QUOTECHAR);

            String[] line;
            while ((line = reader.readNext()) != null) {
                switch (table) {
                    case PROGRAMS_CSV:
                        Program program = new Program();
                        program.setUid(line[1]);
                        program.setName(line[2]);
                        program.save();
                        programList.put(Integer.valueOf(line[0]), program);
                        break;
                    case TABS_CSV:
                        Tab tab = new Tab();
                        tab.setName(line[1]);
                        tab.setOrder_pos(Integer.valueOf(line[2]));
                        tab.setProgram(programList.get(Integer.valueOf(line[3])));
                        tab.setType(Integer.valueOf(line[4]));
                        tab.save();
                        tabList.put(Integer.valueOf(line[0]), tab);
                        break;
                    case HEADERS_CSV:
                        Header header = new Header();
                        header.setShort_name(line[1]);
                        header.setName(line[2]);
                        header.setOrder_pos(Integer.valueOf(line[3]));
                        header.setTab(tabList.get(Integer.valueOf(line[4])));
                        header.save();
                        headerList.put(Integer.valueOf(line[0]), header);
                        break;
                    case ANSWERS_CSV:
                        Answer answer = new Answer();
                        answer.setName(line[1]);
                        answer.save();
                        answerList.put(Integer.valueOf(line[0]), answer);
                        break;
                    case OPTION_ATTRIBUTES_CSV:
                        OptionAttribute optionAttribute = new OptionAttribute();
                        optionAttribute.setBackground_colour(line[1]);
                        optionAttribute.setPath(line[2]);
                        if (line.length > 3 && !line[3].equals("")) {
                            optionAttribute.setHorizontal_alignment(Integer.valueOf(line[3]));
                        } else {
                            optionAttribute.setHorizontal_alignment(
                                    OptionAttribute.DEFAULT_HORIZONTAL_ALIGNMENT);
                        }
                        if (line.length > 4 && !line[4].equals("")) {
                            optionAttribute.setVertical_alignment(Integer.valueOf(line[4]));
                        } else {
                            optionAttribute.setHorizontal_alignment(
                                    OptionAttribute.DEFAULT_VERTICAL_ALIGNMENT);
                        }
                        if (line.length > 5 && !line[5].equals("")) {
                            optionAttribute.setText_size(Integer.valueOf(line[5]));
                        } else {
                            optionAttribute.setText_size(Integer.parseInt(
                                    PreferencesState.getInstance().getContext().getResources()
                                            .getString(
                                                    R.string.default_option_text_size)));
                        }
                        if (line.length > 6 && !line[6].equals("")) {
                            optionAttribute.setDefaultOption(Integer.valueOf(line[6]));
                        } else {
                            optionAttribute.setDefaultOption(0);
                        }
                        optionAttribute.save();
                        optionAttributeList.put(Integer.valueOf(line[0]), optionAttribute);
                        break;
                    case OPTIONS_CSV:
                        Option option = new Option();
                        option.setCode(line[1]);
                        option.setName(line[2]);
                        option.setFactor(Float.valueOf(line[3]));
                        option.setAnswer(answerList.get(Integer.valueOf(line[4])));
                        if (line[5] != null && !line[5].isEmpty()) {
                            option.setOptionAttribute(
                                    optionAttributeList.get(Integer.valueOf(line[5])));
                        }
                        option.save();
                        optionList.put(Integer.valueOf(line[0]), option);
                        break;
                    case QUESTIONS_CSV:
                        Question question = new Question();
                        question.setCode(line[1]);
                        question.setDe_name(line[2]);
                        question.setHelp_text(line[3]);
                        question.setForm_name(line[4]);
                        question.setUid(line[5]);
                        question.setOrder_pos(Integer.valueOf(line[6]));
                        question.setNumerator_w(Float.valueOf(line[7]));
                        question.setDenominator_w(Float.valueOf(line[8]));
                        question.setHeader(headerList.get(Integer.valueOf(line[9])));
                        if (!line[10].equals("")) {
                            question.setAnswer(answerList.get(Integer.valueOf(line[10])));
                        }
                        if (!line[11].equals("")) {
                            question.setQuestion(questionList.get(Integer.valueOf(line[11])));
                        }
                        question.setOutput(Integer.valueOf(line[12]));
                        question.setTotalQuestions(Integer.valueOf(line[13]));
                        question.setVisible(Integer.valueOf(line[14]));
                        if (line.length > 15 && !line[15].equals("")) {
                            question.setPath((line[15]));
                        }
                        if (line.length > 16 && !line[16].equals("")) {
                            question.setCompulsory(Integer.valueOf(line[16]));
                        } else {
                            question.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
                        }
                        question.save();
                        questionList.put(Integer.valueOf(line[0]), question);
                        break;
                    case QUESTION_RELATIONS_CSV:
                        QuestionRelation questionRelation = new QuestionRelation();
                        questionRelation.setOperation(Integer.valueOf(line[1]));
                        questionRelation.setQuestion(questionList.get(Integer.valueOf(line[2])));
                        questionRelation.save();
                        questionRelationList.put(Integer.valueOf(line[0]), questionRelation);
                        break;
                    case MATCHES:
                        Match match = new Match();
                        match.setQuestionRelation(
                                questionRelationList.get(Integer.valueOf(line[1])));
                        match.save();
                        matchList.put(Long.valueOf(line[0]), match);
                        break;
                    case QUESTION_OPTIONS_CSV:
                        QuestionOption questionOption = new QuestionOption();
                        questionOption.setQuestion(questionList.get(Integer.valueOf(line[1])));
                        questionOption.setOption(optionList.get(Integer.valueOf(line[2])));
                        if (!line[3].equals("")) {
                            questionOption.setMatch(matchList.get(Long.valueOf(line[3])));
                        }
                        questionOption.save();
                        break;
                    case QUESTION_THRESHOLDS_CSV:
                        QuestionThreshold questionThreshold = new QuestionThreshold();
                        questionThreshold.setMatch(matchList.get(Long.valueOf(line[1])));
                        questionThreshold.setQuestion(questionList.get(Integer.valueOf(line[2])));
                        if (!line[3].equals("")) {
                            questionThreshold.setMinValue(Integer.valueOf(line[3]));
                        }
                        if (!line[4].equals("")) {
                            questionThreshold.setMaxValue(Integer.valueOf(line[4]));
                        }
                        questionThreshold.save();
                        break;
                    case DRUGS_CSV:
                        Drug drug = PopulateRow.populateDrugs(line, null);
                        drug.insert();
                        drugList.put(Long.parseLong(line[0]), drug);
                        break;
                    case ORGANISATIONS_CSV:
                        Organisation organisation = PopulateRow.populateOrganisations(line, null);
                        organisation.insert();
                        organisationList.put(Long.parseLong(line[0]), organisation);
                        break;
                    case TREATMENT_CSV:
                        Treatment treatment = PopulateRow.populateTreatments(line, organisationList,
                                null);
                        treatment.insert();
                        treatmentList.put(Long.parseLong(line[0]), treatment);
                        break;
                    case DRUG_COMBINATIONS_CSV:
                        PopulateRow.populateDrugCombinations(line, drugList, treatmentList,
                                null).insert();
                        break;
                    case TREATMENT_MATCHES_CSV:
                        PopulateRow.populateTreatmentMatches(line, treatmentList, matchList,
                                null).insert();
                        break;
                }
            }
            reader.close();
        }
        //Free references since the maps are static
        cleanInnerLists();
    }

    public static void populateDummyData(AssetManager assetManager) throws IOException {
        //Reset inner references
        cleanDummyLists();

        for (String table : tables2populateDummy) {
            Log.i(TAG, "Loading csv: " + table);
            CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(table)),
                    SEPARATOR, QUOTECHAR);

            String[] line;
            while ((line = reader.readNext()) != null) {
                switch (table) {
                    case ORG_UNIT_LEVEL_CSV:
                        OrgUnitLevel orgUnitLevel = new OrgUnitLevel();
                        orgUnitLevel.setName(line[1]);
                        orgUnitLevel.save();
                        orgUnitLevelList.put(Integer.valueOf(line[0]), orgUnitLevel);
                        break;
                    case ORG_UNIT_CSV:
                        OrgUnit orgUnit = new OrgUnit();
                        orgUnit.setUid(line[1]);
                        orgUnit.setName(line[2]);
                        orgUnit.setOrgUnit(Long.valueOf(line[3]));
                        orgUnit.setOrgUnitLevel(orgUnitLevelList.get(Integer.valueOf(line[4])));
                        orgUnit.save();
                        orgUnitList.put(Integer.valueOf(line[0]), orgUnit);
                        break;
                }
            }
            reader.close();
        }
        //Free references since the maps are static
        cleanDummyLists();

    }

    private static void cleanInnerLists() {
        programList.clear();
        tabList.clear();
        headerList.clear();
        questionList.clear();
        optionAttributeList.clear();
        optionList.clear();
        answerList.clear();
        questionRelationList.clear();
        matchList.clear();
        treatmentList.clear();
        organisationList.clear();
        drugList.clear();
    }

    private static void cleanDummyLists() {
        orgUnitLevelList.clear();
        orgUnitList.clear();
    }

    /**
     * Deletes all data from the app database
     */
    public static void wipeDatabase() {
        Delete.tables(
                Value.class,
                Score.class,
                Survey.class,
                OrgUnit.class,
                OrgUnitLevel.class
        );
    }

    /**
     * Delete all surveys from database (and its related info)
     */
    public static void wipeSurveys() {
        Delete.tables(
                Value.class,
                Score.class,
                Survey.class
        );
    }

    /**
     * Deletes all data from the sdk database
     */
    public static void wipeSDKData() {
        Delete.tables(
                Event.class,
                DataValue.class,
                FailedItem.class
        );
        DateTimeManager.getInstance().delete();
    }

    public static void addTotalQuestions(AssetManager assetManager, List<Question> questions)
            throws IOException {
        //Reset inner references
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String[] line;
        while ((line = reader.readNext()) != null) {
            for (Question question : questions) {
                if (question.getUid().equals(line[5])) {
                    question.setTotalQuestions(Integer.valueOf(line[13]));
                    question.save();
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addImagePathQuestions(AssetManager assetManager) throws IOException {
        //Reset inner references,
        List<Question> questions = Question.getAllQuestions();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String[] line;
        while ((line = reader.readNext()) != null) {
            for (Question question : questions) {
                if (question.getUid().equals(line[5])) {
                    if (line.length > 15 && !line[15].equals("")) {
                        question.setPath(line[15]);
                        question.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addVisibleQuestions(AssetManager assetManager, List<Question> questions)
            throws IOException {
        //Reset inner references
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String[] line;
        while ((line = reader.readNext()) != null) {
            for (Question question : questions) {
                if (question.getUid().equals(line[5])) {
                    question.setVisible(Integer.valueOf(line[14]));
                    question.save();
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addOptionAttributes(AssetManager assetManager) throws IOException {
        List<Option> options = Option.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(OPTION_ATTRIBUTES_CSV)), SEPARATOR,
                QUOTECHAR);
        CSVReader readerOptions = new CSVReader(
                new InputStreamReader(assetManager.open(OPTIONS_CSV)), SEPARATOR, QUOTECHAR);
        //Remove bad optionAttributes.
        Delete.tables(OptionAttribute.class);
        String[] line;

        //save new optionattributes
        while ((line = reader.readNext()) != null) {
            OptionAttribute optionAttribute = new OptionAttribute();
            optionAttribute.setBackground_colour(line[1]);
            optionAttribute.setPath(line[2]);
            if (line.length > 3 && !line[3].equals("")) {
                optionAttribute.setHorizontal_alignment(Integer.valueOf(line[3]));
            } else {
                optionAttribute.setHorizontal_alignment(
                        OptionAttribute.DEFAULT_HORIZONTAL_ALIGNMENT);
            }
            if (line.length > 4 && !line[4].equals("")) {
                optionAttribute.setVertical_alignment(Integer.valueOf(line[4]));
            } else {
                optionAttribute.setVertical_alignment(OptionAttribute.DEFAULT_VERTICAL_ALIGNMENT);
            }
            if (line.length > 5 && !line[5].equals("")) {
                optionAttribute.setText_size(Integer.valueOf(line[5]));
            } else {
                optionAttribute.setText_size(Integer.parseInt(
                        PreferencesState.getInstance().getContext().getResources().getString(
                                R.string.default_option_text_size)));
            }
            if (line.length > 6 && !line[6].equals("")) {
                optionAttribute.setDefaultOption(Integer.valueOf(line[6]));
            } else {
                optionAttribute.setDefaultOption(0);
            }
            optionAttribute.save();
            optionAttributeList.put(Integer.valueOf(line[0]), optionAttribute);
        }

        line = null;

        //Save new optionattributes for each question
        while ((line = readerOptions.readNext()) != null) {
            for (Option option : options) {
                if (String.valueOf(option.getId_option()).equals(line[0])) {
                    if (!line[5].equals("")) {
                        option.setOptionAttribute(
                                optionAttributeList.get(Integer.valueOf(line[5])));
                        option.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addOptionTextSize(AssetManager assetManager) throws IOException {
        List<Option> options = Option.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(
                new InputStreamReader(assetManager.open(OPTION_ATTRIBUTES_CSV)), SEPARATOR,
                QUOTECHAR);
        CSVReader readerOptions = new CSVReader(
                new InputStreamReader(assetManager.open(OPTIONS_CSV)), SEPARATOR, QUOTECHAR);
        //Remove bad optionAttributes.
        Delete.tables(OptionAttribute.class);
        String[] line;

        //save new optionattributes
        while ((line = reader.readNext()) != null) {
            OptionAttribute optionAttribute = new OptionAttribute();
            optionAttribute.setBackground_colour(line[1]);
            optionAttribute.setPath(line[2]);
            if (line.length > 3 && !line[3].equals("")) {
                optionAttribute.setHorizontal_alignment(Integer.valueOf(line[3]));
            } else {
                optionAttribute.setHorizontal_alignment(
                        OptionAttribute.DEFAULT_HORIZONTAL_ALIGNMENT);
            }
            if (line.length > 4 && !line[4].equals("")) {
                optionAttribute.setVertical_alignment(Integer.valueOf(line[4]));
            } else {
                optionAttribute.setVertical_alignment(OptionAttribute.DEFAULT_VERTICAL_ALIGNMENT);
            }
            if (line.length > 5 && !line[5].equals("")) {
                optionAttribute.setText_size(Integer.valueOf(line[5]));
            } else {
                optionAttribute.setText_size(Integer.parseInt(
                        PreferencesState.getInstance().getContext().getResources().getString(
                                R.string.default_option_text_size)));
            }
            if (line.length > 6 && !line[6].equals("")) {
                optionAttribute.setDefaultOption(Integer.valueOf(line[6]));
            } else {
                optionAttribute.setDefaultOption(0);
            }
            optionAttribute.save();
            optionAttributeList.put(Integer.valueOf(line[0]), optionAttribute);
        }

        line = null;

        //Save new optionattributes for each question
        while ((line = readerOptions.readNext()) != null) {
            for (Option option : options) {
                if (String.valueOf(option.getId_option()).equals(line[0])) {
                    if (!line[5].equals("")) {
                        option.setOptionAttribute(
                                optionAttributeList.get(Integer.valueOf(line[5])));
                        option.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void updateOptionNames(AssetManager assetManager) throws IOException {
        List<Option> options = Option.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(OPTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String line[];
        //Save new option name for each option
        while ((line = reader.readNext()) != null) {
            for (Option option : options) {
                if (String.valueOf(option.getId_option()).equals(line[0])) {
                    option.setCode(line[1]);
                    option.setName(line[2]);
                    option.save();
                    break;
                }
            }
        }
        reader.close();
    }

    public static void updateQuestions(AssetManager assetManager) throws IOException {
        List<Question> questions = Question.getAllQuestions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String line[];
        //Save new option name for each option
        while ((line = reader.readNext()) != null) {
            for (Question question : questions) {
                if (String.valueOf(question.getId_question()).equals((line[0]))) {
                    question.setCode(line[1]);
                    question.setDe_name(line[2]);
                    question.setHelp_text(line[3]);
                    question.setForm_name(line[4]);
                    //Update necessary from migration22 in myanmar
                    question.setOutput(Integer.valueOf(line[12]));
                    //Update necessary from migration3
                    question.setTotalQuestions(Integer.valueOf(line[13]));
                    //Update necessary from migration4
                    question.setVisible(Integer.valueOf(line[14]));
                    //Update necessary from migration7
                    if (line.length > 15 && !line[15].equals("")) {
                        question.setPath(line[15]);
                    }
                    if (line.length > 16 && !line[16].equals("")) {
                        question.setCompulsory(Integer.valueOf(line[16]));
                    } else {
                        question.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
                    }
                    question.save();
                    break;
                }
            }
        }
        reader.close();
    }


    public static void addNotTestedRemminder(AssetManager assetManager) throws IOException {
        //Reset inner references
        cleanInnerLists();
        List<Option> actualOptions = Option.getAllOptions();
        List<Question> actualQuestions = Question.getAllQuestions();

        questionList = new LinkedHashMap<Integer, Question>();
        optionAttributeList = new LinkedHashMap<Integer, OptionAttribute>();
        optionList = new LinkedHashMap<Integer, Option>();
        questionRelationList = new LinkedHashMap();
        matchList = new LinkedHashMap();
        int updateQRFromPosition = 24;
        int updateMatchFromPosition = 25;
        int updateQOFromPosition = 51;
        int QRRow = 0;
        int MatchRow = 0;
        int QORow = 0;
        for (String table : tables2updateQuestions) {
            Log.i(TAG, "Loading csv: " + table);
            CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(table)),
                    SEPARATOR, QUOTECHAR);

            String[] line;
            while ((line = reader.readNext()) != null) {
                boolean isNew = true;
                switch (table) {
                    case OPTION_ATTRIBUTES_CSV:
                        OptionAttribute optionAttribute = new OptionAttribute();
                        optionAttribute.setBackground_colour(line[1]);
                        optionAttribute.setPath(line[2]);
                        if (line.length > 3 && !line[3].equals("")) {
                            optionAttribute.setHorizontal_alignment(Integer.valueOf(line[3]));
                        } else {
                            optionAttribute.setHorizontal_alignment(
                                    OptionAttribute.DEFAULT_HORIZONTAL_ALIGNMENT);
                        }
                        if (line.length > 4 && !line[4].equals("")) {
                            optionAttribute.setVertical_alignment(Integer.valueOf(line[4]));
                        } else {
                            optionAttribute.setHorizontal_alignment(
                                    OptionAttribute.DEFAULT_VERTICAL_ALIGNMENT);
                        }
                        if (line.length > 5 && !line[5].equals("")) {
                            optionAttribute.setText_size(Integer.valueOf(line[5]));
                        } else {
                            optionAttribute.setText_size(Integer.parseInt(
                                    PreferencesState.getInstance().getContext().getResources()
                                            .getString(
                                                    R.string.default_option_text_size)));
                        }
                        if (line.length > 6 && !line[6].equals("")) {
                            optionAttribute.setDefaultOption(Integer.valueOf(line[6]));
                        } else {
                            optionAttribute.setDefaultOption(0);
                        }
                        optionAttributeList.put(Integer.valueOf(line[0]), optionAttribute);
                        break;
                    case OPTIONS_CSV:
                        //Ignore if the option already exists.
                        for (Option option : actualOptions) {
                            if (String.valueOf(option.getId_option()).equals(line[0])) {
                                isNew = false;
                            }
                        }
                        Option option;
                        if (isNew) {
                            option = new Option();
                            option.setCode(line[1]);
                            option.setName(line[2]);
                            option.setFactor(Float.valueOf(line[3]));
                            option.setAnswer(Answer.findById(Long.valueOf(line[4])));
                            if (line[5] != null && !line[5].isEmpty()) {
                                OptionAttribute localOptionAttribute = OptionAttribute.findById(
                                        Long.valueOf(line[5]));
                                if (localOptionAttribute == null) {
                                    localOptionAttribute = optionAttributeList.get(
                                            Integer.valueOf(line[5]));
                                    localOptionAttribute.save();
                                }
                                option.setOptionAttribute(localOptionAttribute);
                            }
                            option.save();
                        } else {
                            option = Option.findById(Float.valueOf(line[0]));
                        }
                        optionList.put(Integer.valueOf(line[0]), option);
                        break;
                    case QUESTIONS_CSV:
                        //Ignore if the question already exists.
                        for (Question question : actualQuestions) {
                            if (String.valueOf(question.getId_question()).equals(line[0])) {
                                isNew = false;
                            }
                        }
                        Question question;
                        if (isNew) {
                            question = new Question();
                            question.setCode(line[1]);
                            question.setDe_name(line[2]);
                            question.setHelp_text(line[3]);
                            question.setForm_name(line[4]);
                            question.setUid(line[5]);
                            question.setOrder_pos(Integer.valueOf(line[6]));
                            question.setNumerator_w(Float.valueOf(line[7]));
                            question.setDenominator_w(Float.valueOf(line[8]));
                            question.setHeader(Header.findById(Long.valueOf(line[9])));
                            if (!line[10].equals("")) {
                                question.setAnswer(Answer.findById(Long.valueOf(line[10])));
                            }
                            if (!line[11].equals("")) {
                                question.setQuestion(questionList.get(Integer.valueOf(line[11])));
                            }
                            question.setOutput(Integer.valueOf(line[12]));
                            question.setTotalQuestions(Integer.valueOf(line[13]));
                            question.setVisible(Integer.valueOf(line[14]));
                            if (line.length > 15 && !line[15].equals("")) {
                                question.setPath((line[15]));
                            }
                            if (line.length > 16 && !line[16].equals("")) {
                                question.setCompulsory(Integer.valueOf(line[16]));
                            } else {
                                question.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
                            }
                            question.save();
                        } else {
                            question = Question.findByUID(line[5]);
                        }

                        questionList.put(Integer.valueOf(line[0]), question);
                        break;
                    case QUESTION_RELATIONS_CSV:
                        //Ignore if the option already exists.
                        QRRow++;
                        if (updateQRFromPosition > QRRow) {
                            break;
                        }
                        QuestionRelation questionRelation = new QuestionRelation();
                        questionRelation.setOperation(Integer.valueOf(line[1]));
                        questionRelation.setQuestion(questionList.get(Integer.valueOf(line[2])));
                        questionRelation.save();
                        questionRelationList.put(Integer.valueOf(line[0]), questionRelation);
                        break;
                    case MATCHES:
                        //Ignore if the match already exists.
                        MatchRow++;
                        if (updateMatchFromPosition > MatchRow) {
                            break;
                        }
                        Match match = new Match();
                        match.setQuestionRelation(
                                questionRelationList.get(Integer.valueOf(line[1])));
                        match.save();
                        matchList.put(Long.valueOf(line[0]), match);
                        break;
                    case QUESTION_OPTIONS_CSV:
                        //Ignore if the question option already exists.
                        QORow++;
                        if (updateQOFromPosition > QORow) {
                            break;
                        }
                        QuestionOption questionOption = new QuestionOption();
                        questionOption.setQuestion(questionList.get(Integer.valueOf(line[1])));
                        questionOption.setOption(optionList.get(Integer.valueOf(line[2])));
                        if (!line[3].equals("")) {
                            questionOption.setMatch(matchList.get(Integer.valueOf(line[3])));
                        }
                        questionOption.save();
                        break;
                }
            }
        }
    }


    /**
     * Migration used to add a new parent-child relation in lao
     */
    public static void createMissingRelationInLao() {
        //new match relation in csv
        //29;29
        //new QuestionOption in csv
        //45;5;13;29
        //new QuestionRelation in csv
        //29;1;6
        Long childId = 6l;
        Long parentId = 5l;
        Float optionId = 13f;
        QuestionRelation questionRelation = new QuestionRelation(Question.findByID(childId),
                QuestionRelation.PARENT_CHILD);
        questionRelation.save();
        Match match = new Match(questionRelation);
        match.save();
        QuestionOption questionOption
                = new QuestionOption(Option.findById(optionId), Question.findByID(parentId), match);
        questionOption.save();
    }
}
