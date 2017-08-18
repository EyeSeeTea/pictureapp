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


package org.eyeseetea.malariacare.data.database.utils.populatedb;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.CompositeScoreDB;
import org.eyeseetea.malariacare.data.database.model.DrugCombinationDB;
import org.eyeseetea.malariacare.data.database.model.DrugDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitLevelDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitProgramRelationDB;
import org.eyeseetea.malariacare.data.database.model.PartnerDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.QuestionThresholdDB;
import org.eyeseetea.malariacare.data.database.model.ScoreDB;
import org.eyeseetea.malariacare.data.database.model.StringKeyDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.SurveyScheduleDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.model.TabGroupDB;
import org.eyeseetea.malariacare.data.database.model.TranslationDB;
import org.eyeseetea.malariacare.data.database.model.TreatmentDB;
import org.eyeseetea.malariacare.data.database.model.TreatmentMatchDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.sdk.common.FileUtils;

import java.io.FileNotFoundException;
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
    public static final String PARTNER_CSV = "Partner.csv";
    public static final String TREATMENT_MATCHES_CSV = "TreatmentMatches.csv";
    public static final String TREATMENT_CSV = "Treatments.csv";
    public static final String TREATMENT_TABLE_CSV = "TreatmentTable.csv";
    public static final String STRING_KEY_CSV = "StringKeys.csv";
    public static final String TRANSLATION_CSV = "Translations.csv";

    public static final String ORG_UNIT_LEVEL_CSV = "OrgUnitLevel.csv";
    public static final String ORG_UNIT_CSV = "OrgUnit.csv";
    public static final char SEPARATOR = ';';
    public static final char QUOTECHAR = '\'';

    public static List<Class<? extends BaseModel>> allTables = Arrays.asList(
            CompositeScoreDB.class,
            OrgUnitProgramRelationDB.class,
            ScoreDB.class,
            SurveyScheduleDB.class,
            TabGroupDB.class,
            SurveyDB.class,
            ValueDB.class,
            UserDB.class,
            StringKeyDB.class,
            TranslationDB.class,
            ProgramDB.class,
            TabDB.class,
            HeaderDB.class,
            AnswerDB.class,
            OptionAttributeDB.class,
            OptionDB.class,
            QuestionDB.class,
            QuestionRelationDB.class,
            MatchDB.class,
            QuestionOptionDB.class,
            QuestionThresholdDB.class,
            DrugDB.class,
            PartnerDB.class,
            TreatmentDB.class,
            DrugCombinationDB.class,
            TreatmentMatchDB.class,
            OrgUnitLevelDB.class,
            OrgUnitDB.class
    );

    private static final List<String> tables2populate = Arrays.asList(
            STRING_KEY_CSV,
            TRANSLATION_CSV,
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
            PARTNER_CSV,
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

    static Map<Integer, ProgramDB> programList = new LinkedHashMap<Integer, ProgramDB>();
    static Map<Integer, TabDB> tabList = new LinkedHashMap<Integer, TabDB>();
    static Map<Integer, HeaderDB> headerList = new LinkedHashMap<Integer, HeaderDB>();
    static Map<Integer, QuestionDB> questionList = new LinkedHashMap<Integer, QuestionDB>();
    static Map<Integer, OptionAttributeDB> optionAttributeList =
            new LinkedHashMap<Integer, OptionAttributeDB>();
    static Map<Integer, OptionDB> optionList = new LinkedHashMap<Integer, OptionDB>();
    static Map<Integer, AnswerDB> answerList = new LinkedHashMap<Integer, AnswerDB>();
    static Map<Integer, QuestionRelationDB> questionRelationList = new LinkedHashMap();
    static HashMap<Long, MatchDB> matchList = new HashMap();

    static Map<Integer, OrgUnitLevelDB> orgUnitLevelList = new LinkedHashMap();
    static Map<Integer, OrgUnitDB> orgUnitList = new LinkedHashMap();
    static HashMap<Long, DrugDB> drugList = new HashMap<>();
    static HashMap<Long, PartnerDB> organisationList = new HashMap<>();
    static HashMap<Long, TreatmentDB> treatmentList = new HashMap<>();
    static HashMap<Long, StringKeyDB> stringKeyList = new HashMap<>();

    public static void initDataIfRequired(Context context) throws IOException {
        if (PopulateDB.hasMandatoryTables()) {
            Log.i(TAG, "Your DB is already populated");
            return;
        }
        new PopulateDBStrategy().init();

        Log.i(TAG, "DB empty, loading data ...");

        PopulateDB.populateDB(context);
        //Get maximum total of questions
        Session.setMaxTotalQuestions(ProgramDB.getMaxTotalQuestions());

        Log.i(TAG, "DB Loaded ...DONE");
    }

    public static boolean hasMandatoryTables() {
        for (Class table : PopulateDBStrategy.getAllMandatoryTables()) {
            if (SQLite.selectCountOf().from(table).count() == 0) {
                Log.d(TAG, "Mandatory table is empty" + table);
                return false;
            }
        }
        return true;
    }

    public static void populateDB(Context context) throws IOException {
        //Reset inner references
        cleanInnerLists();
        for (String table : tables2populate) {
            Log.i(TAG, "Loading csv: " + table);
            CSVReader reader = null;
            try {
                reader = new CSVReader(
                        new InputStreamReader(new PopulateDBStrategy().openFile(context, table)),
                        SEPARATOR, QUOTECHAR);
            } catch (FileNotFoundException e) {
                tableNotExistLog(e, table);
            } catch (IOException e) {
                tableNotExistLog(e, table);
            }
            if (reader == null) {
                continue;
            }
            String[] line;
            while ((line = reader.readNext()) != null && !line[0].isEmpty()) {
                switch (table) {
                    case PROGRAMS_CSV:
                        ProgramDB programDB = new ProgramDB();
                        programDB.setUid(line[1]);
                        programDB.setName(line[2]);
                        programDB.setStageUid(line[3]);
                        programDB.save();
                        programList.put(Integer.valueOf(line[0]), programDB);
                        break;
                    case TABS_CSV:
                        TabDB tabDB = new TabDB();
                        tabDB.setName(line[1]);
                        tabDB.setOrder_pos(Integer.valueOf(line[2]));
                        tabDB.setProgram(programList.get(Integer.valueOf(line[3])));
                        tabDB.setType(Integer.valueOf(line[4]));
                        tabDB.save();
                        tabList.put(Integer.valueOf(line[0]), tabDB);
                        break;
                    case HEADERS_CSV:
                        HeaderDB headerDB = new HeaderDB();
                        headerDB.setShort_name(line[1]);
                        headerDB.setName(line[2]);
                        headerDB.setOrder_pos(Integer.valueOf(line[3]));
                        headerDB.setTabDB(tabList.get(Integer.valueOf(line[4])));
                        headerDB.save();
                        headerList.put(Integer.valueOf(line[0]), headerDB);
                        break;
                    case ANSWERS_CSV:
                        AnswerDB answerDB = new AnswerDB();
                        answerDB.setName(line[1]);
                        answerDB.save();
                        answerList.put(Integer.valueOf(line[0]), answerDB);
                        break;
                    case OPTION_ATTRIBUTES_CSV:
                        OptionAttributeDB optionAttributeDB = new OptionAttributeDB();
                        optionAttributeDB.setBackground_colour(line[1]);
                        optionAttributeDB.setPath(line[2]);
                        if (line.length > 3 && !line[3].equals("")) {
                            optionAttributeDB.setHorizontal_alignment(Integer.valueOf(line[3]));
                        } else {
                            optionAttributeDB.setHorizontal_alignment(
                                    OptionAttributeDB.DEFAULT_HORIZONTAL_ALIGNMENT);
                        }
                        if (line.length > 4 && !line[4].equals("")) {
                            optionAttributeDB.setVertical_alignment(Integer.valueOf(line[4]));
                        } else {
                            optionAttributeDB.setHorizontal_alignment(
                                    OptionAttributeDB.DEFAULT_VERTICAL_ALIGNMENT);
                        }
                        if (line.length > 5 && !line[5].equals("")) {
                            optionAttributeDB.setText_size(Integer.valueOf(line[5]));
                        } else {
                            optionAttributeDB.setText_size(Integer.parseInt(
                                    PreferencesState.getInstance().getContext().getResources()
                                            .getString(
                                                    R.string.default_option_text_size)));
                        }
                        if (line.length > 6 && !line[6].equals("")) {
                            optionAttributeDB.setDefaultOption(Integer.valueOf(line[6]));
                        } else {
                            optionAttributeDB.setDefaultOption(0);
                        }
                        optionAttributeDB.save();
                        optionAttributeList.put(Integer.valueOf(line[0]), optionAttributeDB);
                        break;
                    case OPTIONS_CSV:
                        OptionDB optionDB = new OptionDB();
                        optionDB.setName(line[1]);
                        optionDB.setCode(line[2]);
                        optionDB.setFactor(Float.valueOf(line[3]));
                        optionDB.setAnswerDB(answerList.get(Integer.valueOf(line[4])));
                        if (line[5] != null && !line[5].isEmpty()) {
                            optionDB.setOptionAttributeDB(
                                    optionAttributeList.get(Integer.valueOf(line[5])));
                        }
                        optionDB.save();
                        optionList.put(Integer.valueOf(line[0]), optionDB);
                        break;
                    case QUESTIONS_CSV:
                        QuestionDB questionDB = new QuestionDB();
                        questionDB.setCode(line[1]);
                        questionDB.setDe_name(line[2]);
                        questionDB.setHelp_text(line[3]);
                        questionDB.setForm_name(line[4]);
                        questionDB.setUid(line[5]);
                        questionDB.setOrder_pos(Integer.valueOf(line[6]));
                        questionDB.setNumerator_w(Float.valueOf(line[7]));
                        questionDB.setDenominator_w(Float.valueOf(line[8]));
                        questionDB.setHeader(headerList.get(Integer.valueOf(line[9])));
                        if (!line[10].equals("")) {
                            questionDB.setAnswer(answerList.get(Integer.valueOf(line[10])));
                        }
                        if (!line[11].equals("")) {
                            questionDB.setQuestion(questionList.get(Integer.valueOf(line[11])));
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
                        questionDB.save();
                        questionList.put(Integer.valueOf(line[0]), questionDB);
                        break;
                    case QUESTION_RELATIONS_CSV:
                        QuestionRelationDB questionRelationDB = new QuestionRelationDB();
                        questionRelationDB.setOperation(Integer.valueOf(line[1]));
                        questionRelationDB.setQuestionDB(questionList.get(Integer.valueOf(line[2])));
                        questionRelationDB.save();
                        questionRelationList.put(Integer.valueOf(line[0]), questionRelationDB);
                        break;
                    case MATCHES:
                        MatchDB matchDB = new MatchDB();
                        matchDB.setQuestionRelationDB(
                                questionRelationList.get(Integer.valueOf(line[1])));
                        matchDB.save();
                        matchList.put(Long.valueOf(line[0]), matchDB);
                        break;
                    case QUESTION_OPTIONS_CSV:
                        QuestionOptionDB questionOptionDB = new QuestionOptionDB();
                        questionOptionDB.setQuestion(questionList.get(Integer.valueOf(line[1])));
                        if (!line[2].equals("")) {
                            questionOptionDB.setOption(optionList.get(Integer.valueOf(line[2])));
                        }
                        if (!line[3].equals("")) {
                            questionOptionDB.setMatch(matchList.get(Long.valueOf(line[3])));
                        }
                        questionOptionDB.save();
                        break;
                    case QUESTION_THRESHOLDS_CSV:
                        QuestionThresholdDB questionThresholdDB = new QuestionThresholdDB();
                        questionThresholdDB.setMatchDB(matchList.get(Long.valueOf(line[1])));
                        questionThresholdDB.setQuestionDB(questionList.get(Integer.valueOf(line[2])));
                        if (!line[3].equals("")) {
                            questionThresholdDB.setMinValue(Integer.valueOf(line[3]));
                        }
                        if (!line[4].equals("")) {
                            questionThresholdDB.setMaxValue(Integer.valueOf(line[4]));
                        }
                        questionThresholdDB.save();
                        break;
                    case DRUGS_CSV:
                        DrugDB drugDB = PopulateRow.populateDrugs(line, null);
                        drugDB.insert();
                        drugList.put(Long.parseLong(line[0]), drugDB);
                        break;
                    case PARTNER_CSV:
                        PartnerDB partnerDB = PopulateRow.populateOrganisations(line, null);
                        partnerDB.insert();
                        organisationList.put(Long.parseLong(line[0]), partnerDB);
                        break;
                    case TREATMENT_CSV:
                        TreatmentDB treatmentDB = PopulateRow.populateTreatments(line, organisationList,
                                stringKeyList, null);
                        treatmentDB.insert();
                        treatmentList.put(Long.parseLong(line[0]), treatmentDB);
                        break;
                    case DRUG_COMBINATIONS_CSV:
                        PopulateRow.populateDrugCombinations(line, drugList, treatmentList,
                                null).insert();
                        break;
                    case TREATMENT_MATCHES_CSV:
                        PopulateRow.populateTreatmentMatches(line, treatmentList, matchList,
                                null).insert();
                        break;
                    case STRING_KEY_CSV:
                        StringKeyDB stringKeyDB = PopulateRow.populateStringKey(line, null);
                        stringKeyDB.insert();
                        stringKeyList.put(Long.valueOf(line[0]), stringKeyDB);
                        break;
                    case TRANSLATION_CSV:
                        PopulateRow.populateTranslation(line, stringKeyList, null).insert();
                        break;
                }
            }
            reader.close();
        }
        //Free references since the maps are static
        cleanInnerLists();
    }

    private static void tableNotExistLog(Exception e, String table) {
        Log.i(TAG, "Table " + table + " is not populated");
        e.printStackTrace();
    }

    public static void populateDummyData(Context context) throws IOException {
        //Reset inner references
        cleanDummyLists();

        for (String table : tables2populateDummy) {
            Log.i(TAG, "Loading csv: " + table);
            CSVReader reader = new CSVReader(new InputStreamReader(context.openFileInput(table)),
                    SEPARATOR, QUOTECHAR);

            String[] line;
            while ((line = reader.readNext()) != null) {
                switch (table) {
                    case ORG_UNIT_LEVEL_CSV:
                        OrgUnitLevelDB orgUnitLevelDB = new OrgUnitLevelDB();
                        orgUnitLevelDB.setName(line[1]);
                        orgUnitLevelDB.save();
                        orgUnitLevelList.put(Integer.valueOf(line[0]), orgUnitLevelDB);
                        break;
                    case ORG_UNIT_CSV:
                        OrgUnitDB orgUnitDB = new OrgUnitDB();
                        orgUnitDB.setUid(line[1]);
                        orgUnitDB.setName(line[2]);
                        orgUnitDB.setOrgUnit(Long.valueOf(line[3]));
                        orgUnitDB.setOrgUnitLevelDB(orgUnitLevelList.get(Integer.valueOf(line[4])));
                        orgUnitDB.save();
                        orgUnitList.put(Integer.valueOf(line[0]), orgUnitDB);
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
    public static void wipeTables(Class<? extends BaseModel>[] classes) {
        Delete.tables(
                classes
        );
    }

    public static void wipeDataBase() {
        wipeTables((Class<? extends BaseModel>[]) allTables.toArray());
        deleteSQLiteMetadata();
    }


    /**
     * This method removes the sqlite_sequence table that contains the last autoincrement value for
     * each table
     */
    private static void deleteSQLiteMetadata() {
        String sqlCopy = "Delete from sqlite_sequence";
        DatabaseDefinition databaseDefinition =
                FlowManager.getDatabase(AppDatabase.class);
        databaseDefinition.getWritableDatabase().execSQL(sqlCopy);

    }
    /**
     * Delete all surveys from database (and its related info)
     */
    public static void wipeSurveys() {
        Delete.tables(
                ValueDB.class,
                ScoreDB.class,
                SurveyDB.class
        );
    }

    public static void addTotalQuestions(Context context, List<QuestionDB> questionDBs)
            throws IOException {
        //Reset inner references
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String[] line;
        while ((line = reader.readNext()) != null) {
            for (QuestionDB questionDB : questionDBs) {
                if (questionDB.getUid().equals(line[5])) {
                    questionDB.setTotalQuestions(Integer.valueOf(line[13]));
                    questionDB.save();
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addImagePathQuestions(Context context) throws IOException {
        //Reset inner references,
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestions();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String[] line;
        while ((line = reader.readNext()) != null) {
            for (QuestionDB questionDB : questionDBs) {
                if (questionDB.getUid().equals(line[5])) {
                    if (line.length > 15 && !line[15].equals("")) {
                        questionDB.setPath(line[15]);
                        questionDB.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addVisibleQuestions(Context context, List<QuestionDB> questionDBs)
            throws IOException {
        //Reset inner references
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String[] line;
        while ((line = reader.readNext()) != null) {
            for (QuestionDB questionDB : questionDBs) {
                if (questionDB.getUid().equals(line[5])) {
                    questionDB.setVisible(Integer.valueOf(line[14]));
                    questionDB.save();
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addOptionAttributes(Context context) throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.OPTION_ATTRIBUTES_CSV);
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.OPTIONS_CSV);
        List<OptionDB> optionDBs = OptionDB.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(OPTION_ATTRIBUTES_CSV)), SEPARATOR,
                QUOTECHAR);
        CSVReader readerOptions = new CSVReader(
                new InputStreamReader(context.openFileInput(OPTIONS_CSV)), SEPARATOR, QUOTECHAR);
        //Remove bad optionAttributes.
        Delete.tables(OptionAttributeDB.class);
        String[] line;

        //save new optionattributes
        while ((line = reader.readNext()) != null) {
            OptionAttributeDB optionAttributeDB = new OptionAttributeDB();
            optionAttributeDB.setBackground_colour(line[1]);
            optionAttributeDB.setPath(line[2]);
            if (line.length > 3 && !line[3].equals("")) {
                optionAttributeDB.setHorizontal_alignment(Integer.valueOf(line[3]));
            } else {
                optionAttributeDB.setHorizontal_alignment(
                        OptionAttributeDB.DEFAULT_HORIZONTAL_ALIGNMENT);
            }
            if (line.length > 4 && !line[4].equals("")) {
                optionAttributeDB.setVertical_alignment(Integer.valueOf(line[4]));
            } else {
                optionAttributeDB.setVertical_alignment(OptionAttributeDB.DEFAULT_VERTICAL_ALIGNMENT);
            }
            if (line.length > 5 && !line[5].equals("")) {
                optionAttributeDB.setText_size(Integer.valueOf(line[5]));
            } else {
                optionAttributeDB.setText_size(Integer.parseInt(
                        PreferencesState.getInstance().getContext().getResources().getString(
                                R.string.default_option_text_size)));
            }
            if (line.length > 6 && !line[6].equals("")) {
                optionAttributeDB.setDefaultOption(Integer.valueOf(line[6]));
            } else {
                optionAttributeDB.setDefaultOption(0);
            }
            optionAttributeDB.save();
            optionAttributeList.put(Integer.valueOf(line[0]), optionAttributeDB);
        }

        line = null;

        //Save new optionattributes for each question
        while ((line = readerOptions.readNext()) != null) {
            for (OptionDB optionDB : optionDBs) {
                if (String.valueOf(optionDB.getId_option()).equals(line[0])) {
                    if (!line[5].equals("")) {
                        optionDB.setOptionAttributeDB(
                                optionAttributeList.get(Integer.valueOf(line[5])));
                        optionDB.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addOptionTextSize(Context context) throws IOException {
        List<OptionDB> optionDBs = OptionDB.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(OPTION_ATTRIBUTES_CSV)), SEPARATOR,
                QUOTECHAR);
        CSVReader readerOptions = new CSVReader(
                new InputStreamReader(context.openFileInput(OPTIONS_CSV)), SEPARATOR, QUOTECHAR);
        //Remove bad optionAttributes.
        Delete.tables(OptionAttributeDB.class);
        String[] line;

        //save new optionattributes
        while ((line = reader.readNext()) != null) {
            OptionAttributeDB optionAttributeDB = new OptionAttributeDB();
            optionAttributeDB.setBackground_colour(line[1]);
            optionAttributeDB.setPath(line[2]);
            if (line.length > 3 && !line[3].equals("")) {
                optionAttributeDB.setHorizontal_alignment(Integer.valueOf(line[3]));
            } else {
                optionAttributeDB.setHorizontal_alignment(
                        OptionAttributeDB.DEFAULT_HORIZONTAL_ALIGNMENT);
            }
            if (line.length > 4 && !line[4].equals("")) {
                optionAttributeDB.setVertical_alignment(Integer.valueOf(line[4]));
            } else {
                optionAttributeDB.setVertical_alignment(OptionAttributeDB.DEFAULT_VERTICAL_ALIGNMENT);
            }
            if (line.length > 5 && !line[5].equals("")) {
                optionAttributeDB.setText_size(Integer.valueOf(line[5]));
            } else {
                optionAttributeDB.setText_size(Integer.parseInt(
                        PreferencesState.getInstance().getContext().getResources().getString(
                                R.string.default_option_text_size)));
            }
            if (line.length > 6 && !line[6].equals("")) {
                optionAttributeDB.setDefaultOption(Integer.valueOf(line[6]));
            } else {
                optionAttributeDB.setDefaultOption(0);
            }
            optionAttributeDB.save();
            optionAttributeList.put(Integer.valueOf(line[0]), optionAttributeDB);
        }

        line = null;

        //Save new optionattributes for each question
        while ((line = readerOptions.readNext()) != null) {
            for (OptionDB optionDB : optionDBs) {
                if (String.valueOf(optionDB.getId_option()).equals(line[0])) {
                    if (!line[5].equals("")) {
                        optionDB.setOptionAttributeDB(
                                optionAttributeList.get(Integer.valueOf(line[5])));
                        optionDB.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void updateOptionNames(Context context) throws IOException {
        List<OptionDB> optionDBs = OptionDB.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(new InputStreamReader(context.openFileInput(OPTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String line[];
        //Save new option name for each option
        while ((line = reader.readNext()) != null) {
            for (OptionDB optionDB : optionDBs) {
                if (String.valueOf(optionDB.getId_option()).equals(line[0])) {
                    optionDB.setName(line[1]);
                    optionDB.setCode(line[2]);
                    optionDB.save();
                    break;
                }
            }
        }
        reader.close();
    }

    public static void updateQuestions(Context context) throws IOException {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(QUESTIONS_CSV)),
                SEPARATOR, QUOTECHAR);

        String line[];
        //Save new option name for each option
        while ((line = reader.readNext()) != null) {
            for (QuestionDB questionDB : questionDBs) {
                if (String.valueOf(questionDB.getId_question()).equals((line[0]))) {
                    questionDB.setCode(line[1]);
                    questionDB.setDe_name(line[2]);
                    questionDB.setHelp_text(line[3]);
                    questionDB.setForm_name(line[4]);
                    //Update necessary from migration22 in myanmar
                    questionDB.setOutput(Integer.valueOf(line[12]));
                    //Update necessary from migration3
                    questionDB.setTotalQuestions(Integer.valueOf(line[13]));
                    //Update necessary from migration4
                    questionDB.setVisible(Integer.valueOf(line[14]));
                    //Update necessary from migration7
                    if (line.length > 15 && !line[15].equals("")) {
                        questionDB.setPath(line[15]);
                    }
                    if (line.length > 16 && !line[16].equals("")) {
                        questionDB.setCompulsory(Integer.valueOf(line[16]));
                    } else {
                        questionDB.setCompulsory(QuestionDB.QUESTION_NOT_COMPULSORY);
                    }
                    questionDB.save();
                    break;
                }
            }
        }
        reader.close();
    }


    public static void addNotTestedRemminder(Context context) throws IOException {
        //Reset inner references
        cleanInnerLists();
        List<OptionDB> actualOptionDBs = OptionDB.getAllOptions();
        List<QuestionDB> actualQuestionDBs = QuestionDB.getAllQuestions();

        questionList = new LinkedHashMap<Integer, QuestionDB>();
        optionAttributeList = new LinkedHashMap<Integer, OptionAttributeDB>();
        optionList = new LinkedHashMap<Integer, OptionDB>();
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
            CSVReader reader = new CSVReader(new InputStreamReader(context.openFileInput(table)),
                    SEPARATOR, QUOTECHAR);

            String[] line;
            while ((line = reader.readNext()) != null) {
                boolean isNew = true;
                switch (table) {
                    case OPTION_ATTRIBUTES_CSV:
                        OptionAttributeDB optionAttributeDB = new OptionAttributeDB();
                        optionAttributeDB.setBackground_colour(line[1]);
                        optionAttributeDB.setPath(line[2]);
                        if (line.length > 3 && !line[3].equals("")) {
                            optionAttributeDB.setHorizontal_alignment(Integer.valueOf(line[3]));
                        } else {
                            optionAttributeDB.setHorizontal_alignment(
                                    OptionAttributeDB.DEFAULT_HORIZONTAL_ALIGNMENT);
                        }
                        if (line.length > 4 && !line[4].equals("")) {
                            optionAttributeDB.setVertical_alignment(Integer.valueOf(line[4]));
                        } else {
                            optionAttributeDB.setHorizontal_alignment(
                                    OptionAttributeDB.DEFAULT_VERTICAL_ALIGNMENT);
                        }
                        if (line.length > 5 && !line[5].equals("")) {
                            optionAttributeDB.setText_size(Integer.valueOf(line[5]));
                        } else {
                            optionAttributeDB.setText_size(Integer.parseInt(
                                    PreferencesState.getInstance().getContext().getResources()
                                            .getString(
                                                    R.string.default_option_text_size)));
                        }
                        if (line.length > 6 && !line[6].equals("")) {
                            optionAttributeDB.setDefaultOption(Integer.valueOf(line[6]));
                        } else {
                            optionAttributeDB.setDefaultOption(0);
                        }
                        optionAttributeList.put(Integer.valueOf(line[0]), optionAttributeDB);
                        break;
                    case OPTIONS_CSV:
                        //Ignore if the optionDB already exists.
                        for (OptionDB optionDB : actualOptionDBs) {
                            if (String.valueOf(optionDB.getId_option()).equals(line[0])) {
                                isNew = false;
                            }
                        }
                        OptionDB optionDB;
                        if (isNew) {
                            optionDB = new OptionDB();
                            optionDB.setName(line[1]);
                            optionDB.setCode(line[2]);
                            optionDB.setFactor(Float.valueOf(line[3]));
                            optionDB.setAnswerDB(AnswerDB.findById(Long.valueOf(line[4])));
                            if (line[5] != null && !line[5].isEmpty()) {
                                OptionAttributeDB localOptionAttributeDB = OptionAttributeDB.findById(
                                        Long.valueOf(line[5]));
                                if (localOptionAttributeDB == null) {
                                    localOptionAttributeDB = optionAttributeList.get(
                                            Integer.valueOf(line[5]));
                                    localOptionAttributeDB.save();
                                }
                                optionDB.setOptionAttributeDB(localOptionAttributeDB);
                            }
                            optionDB.save();
                        } else {
                            optionDB = OptionDB.findById(Long.valueOf(line[0]));
                        }
                        optionList.put(Integer.valueOf(line[0]), optionDB);
                        break;
                    case QUESTIONS_CSV:
                        //Ignore if the questionDB already exists.
                        for (QuestionDB questionDB : actualQuestionDBs) {
                            if (String.valueOf(questionDB.getId_question()).equals(line[0])) {
                                isNew = false;
                            }
                        }
                        QuestionDB questionDB;
                        if (isNew) {
                            questionDB = new QuestionDB();
                            questionDB.setCode(line[1]);
                            questionDB.setDe_name(line[2]);
                            questionDB.setHelp_text(line[3]);
                            questionDB.setForm_name(line[4]);
                            questionDB.setUid(line[5]);
                            questionDB.setOrder_pos(Integer.valueOf(line[6]));
                            questionDB.setNumerator_w(Float.valueOf(line[7]));
                            questionDB.setDenominator_w(Float.valueOf(line[8]));
                            questionDB.setHeader(HeaderDB.findById(Long.valueOf(line[9])));
                            if (!line[10].equals("")) {
                                questionDB.setAnswer(AnswerDB.findById(Long.valueOf(line[10])));
                            }
                            if (!line[11].equals("")) {
                                questionDB.setQuestion(questionList.get(Integer.valueOf(line[11])));
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
                            questionDB.save();
                        } else {
                            questionDB = QuestionDB.findByUID(line[5]);
                        }

                        questionList.put(Integer.valueOf(line[0]), questionDB);
                        break;
                    case QUESTION_RELATIONS_CSV:
                        //Ignore if the optionDB already exists.
                        QRRow++;
                        if (updateQRFromPosition > QRRow) {
                            break;
                        }
                        QuestionRelationDB questionRelationDB = new QuestionRelationDB();
                        questionRelationDB.setOperation(Integer.valueOf(line[1]));
                        questionRelationDB.setQuestionDB(questionList.get(Integer.valueOf(line[2])));
                        questionRelationDB.save();
                        questionRelationList.put(Integer.valueOf(line[0]), questionRelationDB);
                        break;
                    case MATCHES:
                        //Ignore if the matchDB already exists.
                        MatchRow++;
                        if (updateMatchFromPosition > MatchRow) {
                            break;
                        }
                        MatchDB matchDB = new MatchDB();
                        matchDB.setQuestionRelationDB(
                                questionRelationList.get(Integer.valueOf(line[1])));
                        matchDB.save();
                        matchList.put(Long.valueOf(line[0]), matchDB);
                        break;
                    case QUESTION_OPTIONS_CSV:
                        //Ignore if the questionDB optionDB already exists.
                        QORow++;
                        if (updateQOFromPosition > QORow) {
                            break;
                        }
                        QuestionOptionDB questionOptionDB = new QuestionOptionDB();
                        questionOptionDB.setQuestion(questionList.get(Integer.valueOf(line[1])));
                        questionOptionDB.setOption(optionList.get(Integer.valueOf(line[2])));
                        if (!line[3].equals("")) {
                            questionOptionDB.setMatch(matchList.get(Integer.valueOf(line[3])));
                        }
                        questionOptionDB.save();
                        break;
                }
            }
        }
    }


    /**
     * Migration used to add a new parent-child relation in lao
     */
    public static void createMissingRelationInLao() {
        //new matchDB relation in csv
        //29;29
        //new QuestionOptionDB in csv
        //45;5;13;29
        //new QuestionRelation in csv
        //29;1;6
        Long childId = 6l;
        Long parentId = 5l;
        Long optionId = 13l;
        QuestionRelationDB questionRelationDB = new QuestionRelationDB(QuestionDB.findByID(childId),
                QuestionRelationDB.PARENT_CHILD);
        questionRelationDB.save();
        MatchDB matchDB = new MatchDB(questionRelationDB);
        matchDB.save();
        QuestionOptionDB questionOptionDB
                = new QuestionOptionDB(OptionDB.findById(optionId), QuestionDB.findByID(parentId),
                matchDB);
        questionOptionDB.save();
    }

    public static void initDBQuery() {
        TabDB.getAllTabs();
    }

    public static void wipeOrgUnitsAndEvents() {
        wipeTables((Class<? extends BaseModel>[]) Arrays.asList(
                OrgUnitDB.class,
                SurveyDB.class,
                ValueDB.class,
                ScoreDB.class,
                SurveyScheduleDB.class,
                UserDB.class).toArray());
    }

    public static void wipeMedia(String dir) {
        FileUtils.removeDir(PreferencesState.getInstance().getContext().getFilesDir().getAbsolutePath()+"/"+dir);
        wipeTables((Class<? extends BaseModel>[]) Arrays.asList(
                MediaDB.class).toArray());
    }
}
