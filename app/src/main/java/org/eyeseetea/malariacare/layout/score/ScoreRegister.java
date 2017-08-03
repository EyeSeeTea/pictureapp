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
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
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
    private static final Map<TabDB, TabNumDenRecord> tabScoreMap =
            new HashMap<TabDB, TabNumDenRecord>();

    public static void initScoresForQuestions(List<QuestionDB> questionDBs, SurveyDB surveyDB) {
        for (QuestionDB questionDB : questionDBs) {
            if (!questionDB.isHiddenBySurvey(surveyDB.getId_survey())) {
                questionDB.initScore(surveyDB);
            } else {
                addRecord(questionDB, 0F, calcDenum(questionDB));
            }
        }
    }

    public static void addRecord(QuestionDB questionDB, Float num, Float den) {
        if (questionDB.getCompositeScoreDB() != null) {
            compositeScoreMap.get(questionDB.getCompositeScoreDB()).addRecord(questionDB, num, den);
        }
        tabScoreMap.get(questionDB.getHeaderDB().getTabDB()).addRecord(questionDB, num, den);
    }

    public static void deleteRecord(QuestionDB questionDB) {
        if (questionDB.getCompositeScoreDB() != null) {
            compositeScoreMap.get(questionDB.getCompositeScoreDB()).deleteRecord(questionDB);
        }
        tabScoreMap.get(questionDB.getHeaderDB().getTabDB()).deleteRecord(questionDB);
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

    public static List<Float> getNumDenum(QuestionDB questionDB) {
        return tabScoreMap.get(questionDB.getHeaderDB().getTabDB()).getNumDenRecord().get(questionDB);
    }

    public static Float getCompositeScore(CompositeScoreDB cScore) {

        List<Float> result = compositeScoreMap.get(cScore).calculateNumDenTotal(
                new ArrayList<Float>(Arrays.asList(0F, 0F)));

        result = getRecursiveScore(cScore, result);

        return ScoreUtils.calculateScoreFromNumDen(result);
    }


    public static List<Float> calculateGeneralScore(TabDB tabDB) {
        return tabScoreMap.get(tabDB).calculateTotal();
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
    public static void registerTabScores(List<TabDB> tabDBs) {
        tabScoreMap.clear();
        for (TabDB tabDB : tabDBs) {
            Log.i(TAG, "Register tabDB score: " + tabDB.getName());
            tabScoreMap.put(tabDB, new TabNumDenRecord());
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
     * Calculates the numerator of the given questionDB in the current survey
     */
    public static float calcNum(QuestionDB questionDB) {
        SurveyDB surveyDB = SurveyFragmentStrategy.getSessionSurveyByQuestion(questionDB);
        return calcNum(questionDB, surveyDB);
    }

    /**
     * Calculates the numerator of the given questionDB & survey
     */
    public static float calcNum(QuestionDB questionDB, SurveyDB surveyDB) {
        if (surveyDB == null || questionDB == null) {
            return 0;
        }

        OptionDB optionDB = questionDB.getOptionBySurvey(surveyDB);
        if (optionDB == null) {
            return 0;
        }
        return questionDB.getNumerator_w() * optionDB.getFactor();
    }

    /**
     * Calculates the numerator of the given questionDB in the current survey
     */
    public static float calcDenum(QuestionDB questionDB) {
        SurveyDB surveyDB = SurveyFragmentStrategy.getSessionSurveyByQuestion(questionDB);
        return calcDenum(questionDB, surveyDB);
    }

    /**
     * Calculates the denominator of the given questionDB & survey
     */
    public static float calcDenum(QuestionDB questionDB, SurveyDB surveyDB) {
        float result = 0;

        if (!questionDB.isScored()) {
            return 0;
        }

        OptionDB optionDB = questionDB.getOptionBySurvey(surveyDB);
        if (optionDB == null) {
            return calcDenum(0, questionDB);
        }
        return calcDenum(optionDB.getFactor(), questionDB);
    }

    private static float calcDenum(float factor, QuestionDB questionDB) {
        float num = questionDB.getNumerator_w();
        float denum = questionDB.getDenominator_w();

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
    public static List<CompositeScoreDB> loadCompositeScores(SurveyDB surveyDB) {
        //Cleans score
        ScoreRegister.clear();

        //Register scores for tabs
        List<TabDB> tabDBs = surveyDB.getProgramDB().getTabDBs();
        ScoreRegister.registerTabScores(tabDBs);

        //Register scores for composites
        List<CompositeScoreDB> compositeScoreDBList = CompositeScoreDB.listByProgram(
                surveyDB.getProgramDB());
        ScoreRegister.registerCompositeScores(compositeScoreDBList);

        //Initialize scores x question
        ScoreRegister.initScoresForQuestions(QuestionDB.listByProgram(surveyDB.getProgramDB()),
                surveyDB);

        return compositeScoreDBList;
    }

}
