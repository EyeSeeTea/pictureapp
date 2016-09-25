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


package org.eyeseetea.malariacare.database.utils;

import android.content.res.AssetManager;
import android.util.Log;

import com.opencsv.CSVReader;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.OrgUnitLevel;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Score;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.TabGroup;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.model.Value;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.preferences.DateTimeManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PopulateDB {

    public static final String PROGRAMS_CSV = "Programs.csv";
    public static final String TAB_GROUPS_CSV = "TabGroups.csv";
    public static final String TABS_CSV = "Tabs.csv";
    public static final String HEADERS_CSV = "Headers.csv";
    public static final String ANSWERS_CSV = "Answers.csv";
    public static final String OPTION_ATTRIBUTES_CSV = "OptionAttributes.csv";
    public static final String OPTIONS_CSV = "Options.csv";
    public static final String QUESTIONS_CSV = "Questions.csv";
    public static final String QUESTION_OPTIONS_CSV="QuestionOptions.csv";
    public static final String  MATCHES = "Matches.csv";
    public static final String QUESTION_RELATIONS_CSV="QuestionRelations.csv";
    public static final String QUESTION_THRESHOLDS_CSV="QuestionThresholds.csv";

    private static final List<String> tables2populate = Arrays.asList(
            PROGRAMS_CSV,
            TAB_GROUPS_CSV,
            TABS_CSV,
            HEADERS_CSV,
            ANSWERS_CSV,
            OPTION_ATTRIBUTES_CSV,
            OPTIONS_CSV,
            QUESTIONS_CSV,
            QUESTION_RELATIONS_CSV,
            MATCHES,
            QUESTION_OPTIONS_CSV,
            QUESTION_THRESHOLDS_CSV);

    public static final char SEPARATOR = ';';
    public static final char QUOTECHAR = '\'';
    private static final String TAG = "PopulateDB";

    static Map<Integer, Program> programList = new LinkedHashMap<Integer, Program>();
    static Map<Integer, TabGroup> tabGroups = new LinkedHashMap<Integer, TabGroup>();
    static Map<Integer, Tab> tabList = new LinkedHashMap<Integer, Tab>();
    static Map<Integer, Header> headerList = new LinkedHashMap<Integer, Header>();
    static Map<Integer, Question> questionList = new LinkedHashMap<Integer, Question>();
    static Map<Integer, OptionAttribute> optionAttributeList = new LinkedHashMap<Integer, OptionAttribute>();
    static Map<Integer, Option> optionList = new LinkedHashMap<Integer, Option>();
    static Map<Integer, Answer> answerList = new LinkedHashMap<Integer, Answer>();
    static Map<Integer, QuestionRelation> questionRelationList = new LinkedHashMap();
    static Map<Integer, Match> matchList = new LinkedHashMap();

    public static void populateDB(AssetManager assetManager) throws IOException {

        //Reset inner references
        cleanInnerLists();
        for (String table : tables2populate) {
            Log.i(TAG,"Loading csv: "+table);
            CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(table)), SEPARATOR, QUOTECHAR);

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
                    case TAB_GROUPS_CSV:
                        TabGroup tabGroup = new TabGroup();
                        tabGroup.setName(line[1]);
                        tabGroup.setProgram(programList.get(Integer.valueOf(line[2])));
                        tabGroup.save();
                        tabGroups.put(Integer.valueOf(line[0]), tabGroup);
                        break;
                    case TABS_CSV:
                        Tab tab = new Tab();
                        tab.setName(line[1]);
                        tab.setOrder_pos(Integer.valueOf(line[2]));
                        tab.setTabGroup(tabGroups.get(Integer.valueOf(line[3])));
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
                        if(line.length>3 && !line[3].equals(""))
                            optionAttribute.setHorizontal_alignment(Integer.valueOf(line[3]));
                        else
                            optionAttribute.setHorizontal_alignment(OptionAttribute.DEFAULT_HORIZONTAL_ALIGNMENT);
                        if(line.length>4 && !line[4].equals(""))
                            optionAttribute.setVertical_alignment(Integer.valueOf(line[4]));
                        else
                            optionAttribute.setHorizontal_alignment(OptionAttribute.DEFAULT_VERTICAL_ALIGNMENT);
                        if(line.length>5 && !line[5].equals(""))
                            optionAttribute.setText_size(Integer.valueOf(line[5]));
                        else
                            optionAttribute.setText_size(Integer.parseInt(PreferencesState.getInstance().getContext().getResources().getString(R.string.default_option_text_size)));
                        optionAttribute.save();
                        optionAttributeList.put(Integer.valueOf(line[0]), optionAttribute);
                        break;
                    case OPTIONS_CSV:
                        Option option = new Option();
                        option.setCode(line[1]);
                        option.setName(line[2]);
                        option.setFactor(Float.valueOf(line[3]));
                        option.setAnswer(answerList.get(Integer.valueOf(line[4])));
                        if (line[5]!=null && !line[5].isEmpty()) {
                            option.setOptionAttribute(optionAttributeList.get(Integer.valueOf(line[5])));
                        }
                        option.save();
                        optionList.put(Integer.valueOf(line[0]), option);
                        break;
                    case QUESTIONS_CSV:
                        Question question = new Question();
                        question.setCode(line[1]);
                        question.setDe_name(line[2]);
                        question.setShort_name(line[3]);
                        question.setForm_name(line[4]);
                        question.setUid(line[5]);
                        question.setOrder_pos(Integer.valueOf(line[6]));
                        question.setNumerator_w(Float.valueOf(line[7]));
                        question.setDenominator_w(Float.valueOf(line[8]));
                        question.setHeader(headerList.get(Integer.valueOf(line[9])));
                        if (!line[10].equals(""))
                            question.setAnswer(answerList.get(Integer.valueOf(line[10])));
                        if (!line[11].equals(""))
                            question.setQuestion(questionList.get(Integer.valueOf(line[11])));
                        question.setOutput(Integer.valueOf(line[12]));
                        question.setTotalQuestions(Integer.valueOf(line[13]));
                        question.setVisible(Integer.valueOf(line[14]));
                        if(line.length>15 && !line[15].equals(""))
                            question.setPath((line[15]));
                        question.save();
                        questionList.put(Integer.valueOf(line[0]), question);
                        break;
                    case QUESTION_RELATIONS_CSV:
                        QuestionRelation questionRelation = new QuestionRelation();
                        questionRelation.setOperation(Integer.valueOf(line[1]));
                        questionRelation.setQuestion(questionList.get(Integer.valueOf(line[2])));
                        questionRelation.save();
                        questionRelationList.put(Integer.valueOf(line[0]),questionRelation);
                        break;
                    case MATCHES:
                        Match match = new Match();
                        match.setQuestionRelation(questionRelationList.get(Integer.valueOf(line[1])));
                        match.save();
                        matchList.put(Integer.valueOf(line[0]),match);
                        break;
                    case QUESTION_OPTIONS_CSV:
                        QuestionOption questionOption = new QuestionOption();
                        questionOption.setQuestion(questionList.get(Integer.valueOf(line[1])));
                        questionOption.setOption(optionList.get(Integer.valueOf(line[2])));
                        if(!line[3].equals(""))
                            questionOption.setMatch(matchList.get(Integer.valueOf(line[3])));
                        questionOption.save();
                        break;
                    case QUESTION_THRESHOLDS_CSV:
                        QuestionThreshold questionThreshold = new QuestionThreshold();
                        questionThreshold.setMatch(matchList.get(Integer.valueOf(line[1])));
                        questionThreshold.setQuestion(questionList.get(Integer.valueOf(line[2])));
                        if (!line[3].equals("")){
                            questionThreshold.setMinValue(Integer.valueOf(line[3]));
                        }
                        if (!line[4].equals("")){
                            questionThreshold.setMaxValue(Integer.valueOf(line[4]));
                        }
                        questionThreshold.save();
                }
            }
            reader.close();
        }
        //Free references since the maps are static
        cleanInnerLists();
    }

    /**
     * Used for testing purposes
     */
    public static void populateDummyData(){
    }

    private static void cleanInnerLists(){
        programList.clear();
        tabGroups.clear();
        tabList.clear();
        headerList.clear();
        questionList.clear();
        optionAttributeList.clear();
        optionList.clear();
        answerList.clear();
        questionRelationList.clear();
        matchList.clear();
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
                OrgUnitLevel.class,
                User.class
        );
    }

    /**
     * Delete all surveys from database (and its related info)
     */
    public static void wipeSurveys(){
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

    public static void addTotalQuestions(AssetManager assetManager, List<Question> questions) throws IOException {
        //Reset inner references
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(QUESTIONS_CSV)), SEPARATOR, QUOTECHAR);

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
        List<Question> questions=Question.getAllQuestions();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(QUESTIONS_CSV)), SEPARATOR, QUOTECHAR);

        String[] line;
        while ((line = reader.readNext()) != null) {
            for (Question question : questions) {
                if (question.getUid().equals(line[5])) {
                    if(line.length>15 && !line[15].equals("")) {
                        question.setPath(line[15]);
                        question.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addVisibleQuestions(AssetManager assetManager, List<Question> questions) throws IOException {
        //Reset inner references
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(QUESTIONS_CSV)), SEPARATOR, QUOTECHAR);

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

    public static void addOptionAttributes(AssetManager assetManager) throws IOException  {
        List<Option> options = Option.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(OPTION_ATTRIBUTES_CSV)), SEPARATOR, QUOTECHAR);
        CSVReader readerOptions = new CSVReader(new InputStreamReader(assetManager.open(OPTIONS_CSV)), SEPARATOR, QUOTECHAR);
        //Remove bad optionAttributes.
        Delete.tables(OptionAttribute.class);
        String[] line;

        //save new optionattributes
        while ((line = reader.readNext()) != null) {
            OptionAttribute optionAttribute = new OptionAttribute();
            optionAttribute.setBackground_colour(line[1]);
            optionAttribute.setPath(line[2]);
            if(line.length>3 && !line[3].equals(""))
                optionAttribute.setHorizontal_alignment(Integer.valueOf(line[3]));
            else
                optionAttribute.setHorizontal_alignment(OptionAttribute.DEFAULT_HORIZONTAL_ALIGNMENT);
            if(line.length>4 && !line[4].equals(""))
                optionAttribute.setVertical_alignment(Integer.valueOf(line[4]));
            else
                optionAttribute.setHorizontal_alignment(OptionAttribute.DEFAULT_VERTICAL_ALIGNMENT);
            if(line.length>5 && !line[5].equals(""))
                optionAttribute.setText_size(Integer.valueOf(line[5]));
            else
                optionAttribute.setText_size(Integer.parseInt(PreferencesState.getInstance().getContext().getResources().getString(R.string.default_option_text_size)));
            optionAttribute.save();
            optionAttributeList.put(Integer.valueOf(line[0]), optionAttribute);
        }

        line=null;

        //Save new optionattributes for each question
        while ((line = readerOptions.readNext()) != null) {
            for(Option option:options) {
                if(String.valueOf(option.getId_option()).equals(line[0])){
                    if (!line[5].equals("")) {
                        option.setOptionAttribute(optionAttributeList.get(Integer.valueOf(line[5])));
                        option.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void addOptionTextSize(AssetManager assetManager) throws IOException  {
        List<Option> options = Option.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(OPTION_ATTRIBUTES_CSV)), SEPARATOR, QUOTECHAR);
        CSVReader readerOptions = new CSVReader(new InputStreamReader(assetManager.open(OPTIONS_CSV)), SEPARATOR, QUOTECHAR);
        //Remove bad optionAttributes.
        Delete.tables(OptionAttribute.class);
        String[] line;

        //save new optionattributes
        while ((line = reader.readNext()) != null) {
            OptionAttribute optionAttribute = new OptionAttribute();
            optionAttribute.setBackground_colour(line[1]);
            optionAttribute.setPath(line[2]);
            if(line.length>3 && !line[3].equals(""))
                optionAttribute.setHorizontal_alignment(Integer.valueOf(line[3]));
            else
                optionAttribute.setHorizontal_alignment(OptionAttribute.DEFAULT_HORIZONTAL_ALIGNMENT);
            if(line.length>4 && !line[4].equals(""))
                optionAttribute.setVertical_alignment(Integer.valueOf(line[4]));
            else
                optionAttribute.setHorizontal_alignment(OptionAttribute.DEFAULT_VERTICAL_ALIGNMENT);
            if(line.length>5 && !line[5].equals(""))
                optionAttribute.setText_size(Integer.valueOf(line[5]));
            else
                optionAttribute.setText_size(Integer.parseInt(PreferencesState.getInstance().getContext().getResources().getString(R.string.default_option_text_size)));
            optionAttribute.save();
            optionAttributeList.put(Integer.valueOf(line[0]), optionAttribute);
        }

        line=null;

        //Save new optionattributes for each question
        while ((line = readerOptions.readNext()) != null) {
            for(Option option:options) {
                if(String.valueOf(option.getId_option()).equals(line[0])){
                    if (!line[5].equals("")) {
                        option.setOptionAttribute(optionAttributeList.get(Integer.valueOf(line[5])));
                        option.save();
                    }
                    break;
                }
            }
        }
        reader.close();
    }

    public static void updateOptionNames(AssetManager assetManager) throws IOException  {
        List<Option> options = Option.getAllOptions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(OPTIONS_CSV)), SEPARATOR, QUOTECHAR);

        String line[];
        //Save new option name for each option
        while ((line = reader.readNext()) != null) {
            for(Option option:options) {
                if(String.valueOf(option.getId_option()).equals(line[0])){
                    option.setCode(line[1]);
                    option.setName(line[2]);
                    option.save();
                    break;
                }
            }
        }
        reader.close();
    }

    public static void updateQuestionNameAndForms(AssetManager assetManager) throws IOException {
        List<Question> questions = Question.getAllQuestions();
        //Reset inner references
        cleanInnerLists();
        CSVReader reader = new CSVReader(new InputStreamReader(assetManager.open(QUESTIONS_CSV)), SEPARATOR, QUOTECHAR);

        String line[];
        //Save new option name for each option
        while ((line = reader.readNext()) != null) {
            for(Question question:questions) {
                if(question.getUid().equals(line[5])){
                    question.setCode(line[1]);
                    question.setDe_name(line[2]);
                    question.setShort_name(line[3]);
                    question.setForm_name(line[4]);
                    question.save();
                    break;
                }
            }
        }
        reader.close();
    }
}
