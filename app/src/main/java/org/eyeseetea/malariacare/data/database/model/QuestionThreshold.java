/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.database.model;

import static org.eyeseetea.malariacare.data.database.AppDatabase.matchAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.matchName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionThresholdAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionThresholdName;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class QuestionThreshold extends BaseModel {
    private static final String TAG = ".QuestionThreshold";
    @Column
    @PrimaryKey(autoincrement = true)
    long id_question_threshold;

    @Column
    Long id_question_fk;
    /**
     * Reference to its question (lazy)
     */
    Question question;

    @Column
    Long id_match_fk;
    /**
     * Reference to its match (lazy)
     */
    Match match;

    @Column
    Integer minValue;

    @Column
    Integer maxValue;

    public QuestionThreshold() {
    }

    public QuestionThreshold(Question question, Match match, Integer minValue, Integer maxValue) {
        setQuestion(question);
        setMatch(match);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public static QuestionThreshold findByQuestionAndOption(Question questionWithOption,
            Option option) {
        List<QuestionOption> questionOptionList = QuestionOption.findByQuestionAndOption(
                questionWithOption, option);
        //No questionOption no threshold
        if (questionOptionList == null || questionOptionList.isEmpty()) {
            return null;
        }

        //Look for threshold under questionOption
        for (QuestionOption questionOption : questionOptionList) {
            Match match = questionOption.getMatch();
            QuestionThreshold questionThreshold = match.getQuestionThreshold();
            //Found
            if (questionThreshold != null) {
                return questionThreshold;
            }
        }
        return null;
    }

    public static List<QuestionThreshold> getAllQuestionThresholds() {
        return new Select().from(QuestionThreshold.class).queryList();
    }

    /**
     * Method to get the matches with a question id and a value between or equal min max
     * @param id_question The id of the question.
     * @param value The value to check.
     * @return The list of matches.
     */
    public static List<Match> getMatchesWithQuestionValue(Long id_question,
            int value) {
        return new Select().from(Match.class).as(matchName)
                .join(QuestionThreshold.class, Join.JoinType.LEFT_OUTER).as(questionThresholdName)
                .on(Match_Table.id_match.withTable(matchAlias)
                        .eq(QuestionThreshold_Table.id_match_fk.withTable(questionThresholdAlias)))
                .where(QuestionThreshold_Table.id_question_fk.withTable(questionThresholdAlias)
                        .is(id_question))
                .and(QuestionThreshold_Table.minValue.withTable(questionThresholdAlias)
                        .lessThanOrEq(value))
                .and(QuestionThreshold_Table.maxValue.withTable(questionThresholdAlias)
                        .greaterThanOrEq(value))
                .queryList();
    }

    /**
     * Method to delete the questionThresholds passed
     */
    public static void deleteQuestionThresholds(List<QuestionThreshold> questionThresholds) {
        for (QuestionThreshold questionThreshold : questionThresholds) {
            questionThreshold.delete();
        }
    }

    public long getId_question_threshold() {
        return id_question_threshold;
    }

    public void setId_question_threshold(long id_question_threshold) {
        this.id_question_threshold = id_question_threshold;
    }

    public Question getQuestion() {
        if (question == null) {
            if (id_question_fk == null) return null;
            question = new Select()
                    .from(Question.class)
                    .where(Question_Table.id_question
                            .is(id_question_fk)).querySingle();
        }
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        this.id_question_fk = (question != null) ? question.getId_question() : null;
    }

    public void setQuestion(Long id_question) {
        this.id_question_fk = id_question;
        this.question = null;
    }

    public Match getMatch() {
        if (match == null) {
            if (id_match_fk == null) return null;
            match = new Select()
                    .from(Match.class)
                    .where(Match_Table.id_match
                            .is(id_match_fk)).querySingle();
        }
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
        this.id_match_fk = (match != null) ? match.getId_match() : null;
    }

    public void setMatch(Long id_match) {
        this.id_match_fk = id_match;
        this.match = null;
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Checks if the given string contains a number inside this threshold
     */
    public boolean isInThreshold(String value) {
        //Get number
        try {
            int intValue = Integer.valueOf(value);
            return isInThreshold(intValue);
        } catch (NumberFormatException ex) {
            Log.e(TAG, ex.getMessage());
            return false;
        }
    }

    /**
     * Checks if the given number is inside this threshold
     */
    public boolean isInThreshold(int value) {
        boolean okLowerBound = minValue == null || value >= minValue;
        boolean okUpperBound = maxValue == null || value <= maxValue;

        return okLowerBound && okUpperBound;
    }


    public static List<QuestionThreshold> getQuestionThresholdsWithMatch(Long matchId) {
        return new Select()
                .from(QuestionThreshold.class)
                .where(QuestionThreshold_Table.id_match_fk.is(matchId))
                .queryList();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionThreshold that = (QuestionThreshold) o;

        if (id_question_threshold != that.id_question_threshold) return false;
        if (minValue != that.minValue) return false;
        if (maxValue != that.maxValue) return false;
        if (!id_question_fk.equals(that.id_question_fk)) return false;
        return id_match_fk.equals(that.id_match_fk);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_question_threshold ^ (id_question_threshold >>> 32));
        result = 31 * result + id_question_fk.hashCode();
        result = 31 * result + id_match_fk.hashCode();
        result = 31 * result + minValue;
        result = 31 * result + maxValue;
        return result;
    }

    @Override
    public String toString() {
        return "QuestionThreshold{" +
                "id_question_threshold=" + id_question_threshold +
                ", id_question=" + id_question_fk +
                ", id_match=" + id_match_fk +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                '}';
    }
}
