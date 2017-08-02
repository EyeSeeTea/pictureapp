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

package org.eyeseetea.malariacare.layout.score;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.model.CompositeScoreDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for storing and dealing with survey scores.
 */
public class ScoreRegister {

    /**
     * Tag for logging
     */
    private static final String TAG = ".ScoreRegister";

    /**
     * Map of scores for each compositescore
     */
    private static final Map<CompositeScoreDB, CompositeNumDenRecord> compositeScoreMap =
            new HashMap<CompositeScoreDB, CompositeNumDenRecord>();

    /**
     * Map of scores for each tab
     */
    private static final Map<Tab, TabNumDenRecord> tabScoreMap =
            new HashMap<Tab, TabNumDenRecord>();

    public static void initScoresForQuestions(List<Question> questions, Survey survey) {
        for (Question question : questions) {
            if (!question.isHiddenBySurvey(survey.getId_survey())) {
                question.initScore(survey);
            } else {
                addRecord(question, 0F, calcDenum(question));
            }
        }
    }

    public static void addRecord(Question question, Float num, Float den) {
        if (question.getCompositeScoreDB() != null) {
            compositeScoreMap.get(question.getCompositeScoreDB()).addRecord(question, num, den);
        }
        tabScoreMap.get(question.getHeaderDB().getTab()).addRecord(question, num, den);
    }

    public static void deleteRecord(Question question) {
        if (question.getCompositeScoreDB() != null) {
            compositeScoreMap.get(question.getCompositeScoreDB()).deleteRecord(question);
        }
        tabScoreMap.get(question.getHeaderDB().getTab()).deleteRecord(question);
    }

    private static List<Float> getRecursiveScore(CompositeScoreDB cScore, List<Float> result) {

        if (!cScore.hasChildren()) {

            //FIXME this try catch just covers a error in data compositeScore: '4.2'
            try {
                return compositeScoreMap.get(cScore).calculateNumDenTotal(result);
            } catch (NullPointerException ex) {
                return Arrays.asList(new Float(0f), new Float(0f));
            }
        } else {
            for (CompositeScoreDB cScoreChildren : cScore.getCompositeScoreDBChildren()) {
                result = getRecursiveScore(cScoreChildren, result);
            }
            return result;
        }
    }

    public static List<Float> getNumDenum(Question question) {
        return tabScoreMap.get(question.getHeaderDB().getTab()).getNumDenRecord().get(question);
    }

    public static Float getCompositeScore(CompositeScoreDB cScore) {

        List<Float> result = compositeScoreMap.get(cScore).calculateNumDenTotal(
                new ArrayList<Float>(Arrays.asList(0F, 0F)));

        result = getRecursiveScore(cScore, result);

        return ScoreUtils.calculateScoreFromNumDen(result);
    }


    public static List<Float> calculateGeneralScore(Tab tab) {
        return tabScoreMap.get(tab).calculateTotal();
    }

    /**
     * Resets compositescores and initializes a new set of them
     */
    public static void registerCompositeScores(List<CompositeScoreDB> compositeScoreDBs) {
        compositeScoreMap.clear();
        for (CompositeScoreDB compositeScoreDB : compositeScoreDBs) {
            Log.i(TAG, "Register composite score: " + compositeScoreDB.getHierarchical_code());
            compositeScoreMap.put(compositeScoreDB, new CompositeNumDenRecord());
        }
    }

    /**
     * Resets generalScores and initializes a new set ot them
     */
    public static void registerTabScores(List<Tab> tabs) {
        tabScoreMap.clear();
        for (Tab tab : tabs) {
            Log.i(TAG, "Register tab score: " + tab.getName());
            tabScoreMap.put(tab, new TabNumDenRecord());
        }
    }

    /**
     * Clears every score in session
     */
    public static void clear() {
        compositeScoreMap.clear();
        tabScoreMap.clear();
    }

    /**
     * Calculates the numerator of the given question in the current survey
     */
    public static float calcNum(Question question) {
        Survey survey = SurveyFragmentStrategy.getSessionSurveyByQuestion(question);
        return calcNum(question, survey);
    }

    /**
     * Calculates the numerator of the given question & survey
     */
    public static float calcNum(Question question, Survey survey) {
        if (survey == null || question == null) {
            return 0;
        }

        OptionDB optionDB = question.getOptionBySurvey(survey);
        if (optionDB == null) {
            return 0;
        }
        return question.getNumerator_w() * optionDB.getFactor();
    }

    /**
     * Calculates the numerator of the given question in the current survey
     */
    public static float calcDenum(Question question) {
        Survey survey = SurveyFragmentStrategy.getSessionSurveyByQuestion(question);
        return calcDenum(question, survey);
    }

    /**
     * Calculates the denominator of the given question & survey
     */
    public static float calcDenum(Question question, Survey survey) {
        float result = 0;

        if (!question.isScored()) {
            return 0;
        }

        OptionDB optionDB = question.getOptionBySurvey(survey);
        if (optionDB == null) {
            return calcDenum(0, question);
        }
        return calcDenum(optionDB.getFactor(), question);
    }

    private static float calcDenum(float factor, Question question) {
        float num = question.getNumerator_w();
        float denum = question.getDenominator_w();

        if (num == denum) {
            return denum;
        }
        if (num == 0 && denum != 0) {
            return factor * denum;
        }
        return 0;
    }

    /**
     * Cleans, prepares, calculates and returns all the scores info for the given survey
     */
    public static List<CompositeScoreDB> loadCompositeScores(Survey survey) {
        //Cleans score
        ScoreRegister.clear();

        //Register scores for tabs
        List<Tab> tabs = survey.getProgram().getTabs();
        ScoreRegister.registerTabScores(tabs);

        //Register scores for composites
        List<CompositeScoreDB> compositeScoreDBList = CompositeScoreDB.listByProgram(
                survey.getProgram());
        ScoreRegister.registerCompositeScores(compositeScoreDBList);

        //Initialize scores x question
        ScoreRegister.initScoresForQuestions(Question.listByProgram(survey.getProgram()), survey);

        return compositeScoreDBList;
    }

}
