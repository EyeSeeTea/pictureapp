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

package org.eyeseetea.malariacare.database.model;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

import java.util.List;

/**
 * Created by Jose on 25/05/2015.
 */
@Table(databaseName = AppDatabase.NAME)
public class QuestionThreshold extends BaseModel {
    private static final String TAG = ".QuestionThreshold";
    @Column
    @PrimaryKey(autoincrement = true)
    long id_question_threshold;

    @Column
    Long id_question;
    /**
     * Reference to its question (lazy)
     */
    Question question;

    @Column
    Long id_match;
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

    public long getId_question_threshold() {
        return id_question_threshold;
    }

    public void setId_question_threshold(long id_question_threshold) {
        this.id_question_threshold = id_question_threshold;
    }

    public Question getQuestion() {
        if (question == null) {
            if (id_question == null) return null;
            question = new Select()
                    .from(Question.class)
                    .where(Condition.column(Question$Table.ID_QUESTION)
                            .is(id_question)).querySingle();
        }
        return question;
    }

    public void setQuestion(Long id_question) {
        this.id_question = id_question;
        this.question = null;
    }

    public void setQuestion(Question question) {
        this.question = question;
        this.id_question = (question != null) ? question.getId_question() : null;
    }

    public Match getMatch() {
        if (match == null) {
            if (id_match == null) return null;
            match = new Select()
                    .from(Match.class)
                    .where(Condition.column(Match$Table.ID_MATCH)
                            .is(id_match)).querySingle();
        }
        return match;
    }

    public void setMatch(Long id_match) {
        this.id_match = id_match;
        this.match = null;
    }

    public void setMatch(Match match) {
        this.match = match;
        this.id_match = (match != null) ? match.getId_match() : null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionThreshold that = (QuestionThreshold) o;

        if (id_question_threshold != that.id_question_threshold) return false;
        if (minValue != that.minValue) return false;
        if (maxValue != that.maxValue) return false;
        if (!id_question.equals(that.id_question)) return false;
        return id_match.equals(that.id_match);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_question_threshold ^ (id_question_threshold >>> 32));
        result = 31 * result + id_question.hashCode();
        result = 31 * result + id_match.hashCode();
        result = 31 * result + minValue;
        result = 31 * result + maxValue;
        return result;
    }

    @Override
    public String toString() {
        return "QuestionThreshold{" +
                "id_question_threshold=" + id_question_threshold +
                ", id_question=" + id_question +
                ", id_match=" + id_match +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                '}';
    }
}
