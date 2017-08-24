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

@Table(database = AppDatabase.class, name = "QuestionThreshold")
public class QuestionThresholdDB extends BaseModel {
    private static final String TAG = ".QuestionThresholdDB";
    @Column
    @PrimaryKey(autoincrement = true)
    long id_question_threshold;

    @Column
    Long id_question_fk;
    /**
     * Reference to its mQuestionDB (lazy)
     */
    QuestionDB mQuestionDB;

    @Column
    Long id_match_fk;
    /**
     * Reference to its mMatchDB (lazy)
     */
    MatchDB mMatchDB;

    @Column
    Integer minValue;

    @Column
    Integer maxValue;

    public QuestionThresholdDB() {
    }

    public QuestionThresholdDB(QuestionDB questionDB, MatchDB matchDB, Integer minValue,
            Integer maxValue) {
        setQuestionDB(questionDB);
        setMatchDB(matchDB);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public static QuestionThresholdDB findByQuestionAndOption(QuestionDB questionDBWithOption,
            OptionDB optionDB) {
        List<QuestionOptionDB> questionOptionDBList = QuestionOptionDB.findByQuestionAndOption(
                questionDBWithOption, optionDB);
        //No questionOption no threshold
        if (questionOptionDBList == null || questionOptionDBList.isEmpty()) {
            return null;
        }

        //Look for threshold under questionOption
        for (QuestionOptionDB questionOptionDB : questionOptionDBList) {
            MatchDB matchDB = questionOptionDB.getMatchDB();
            QuestionThresholdDB questionThresholdDB = matchDB.getQuestionThreshold();
            //Found
            if (questionThresholdDB != null) {
                return questionThresholdDB;
            }
        }
        return null;
    }

    public static List<QuestionThresholdDB> getAllQuestionThresholds() {
        return new Select().from(QuestionThresholdDB.class).queryList();
    }

    public static void deleteAll() {
        deleteQuestionThresholds(getAllQuestionThresholds());
    }

    /**
     * Method to get the mMatchDBs with a mQuestionDB id and a value between or equal min max
     * @param id_question The id of the mQuestionDB.
     * @param value The value to check.
     * @return The list of mMatchDBs.
     */
    public static List<MatchDB> getMatchesWithQuestionValue(Long id_question,
            int value) {
        return new Select().from(MatchDB.class).as(matchName)
                .join(QuestionThresholdDB.class, Join.JoinType.LEFT_OUTER).as(questionThresholdName)
                .on(MatchDB_Table.id_match.withTable(matchAlias)
                        .eq(QuestionThresholdDB_Table.id_match_fk.withTable(
                                questionThresholdAlias)))
                .where(QuestionThresholdDB_Table.id_question_fk.withTable(questionThresholdAlias)
                        .is(id_question))
                .and(QuestionThresholdDB_Table.minValue.withTable(questionThresholdAlias)
                        .lessThanOrEq(value))
                .and(QuestionThresholdDB_Table.maxValue.withTable(questionThresholdAlias)
                        .greaterThanOrEq(value))
                .queryList();
    }

    /**
     * Method to delete the questionThresholdDBs passed
     */
    public static void deleteQuestionThresholds(List<QuestionThresholdDB> questionThresholdDBs) {
        for (QuestionThresholdDB questionThresholdDB : questionThresholdDBs) {
            questionThresholdDB.delete();
        }
    }

    public long getId_question_threshold() {
        return id_question_threshold;
    }

    public void setId_question_threshold(long id_question_threshold) {
        this.id_question_threshold = id_question_threshold;
    }

    public QuestionDB getQuestionDB() {
        if (mQuestionDB == null) {
            if (id_question_fk == null) return null;
            mQuestionDB = new Select()
                    .from(QuestionDB.class)
                    .where(QuestionDB_Table.id_question
                            .is(id_question_fk)).querySingle();
        }
        return mQuestionDB;
    }

    public void setQuestionDB(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
        this.id_question_fk = (questionDB != null) ? questionDB.getId_question() : null;
    }

    public void setQuestion(Long id_question) {
        this.id_question_fk = id_question;
        this.mQuestionDB = null;
    }

    public MatchDB getMatchDB() {
        if (mMatchDB == null) {
            if (id_match_fk == null) return null;
            mMatchDB = new Select()
                    .from(MatchDB.class)
                    .where(MatchDB_Table.id_match
                            .is(id_match_fk)).querySingle();
        }
        return mMatchDB;
    }

    public void setMatchDB(MatchDB matchDB) {
        this.mMatchDB = matchDB;
        this.id_match_fk = (matchDB != null) ? matchDB.getId_match() : null;
    }

    public void setMatch(Long id_match) {
        this.id_match_fk = id_match;
        this.mMatchDB = null;
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


    public static List<QuestionThresholdDB> getQuestionThresholdsWithMatch(Long matchId) {
        return new Select()
                .from(QuestionThresholdDB.class)
                .where(QuestionThresholdDB_Table.id_match_fk.is(matchId))
                .queryList();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionThresholdDB that = (QuestionThresholdDB) o;

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
        return "QuestionThresholdDB{" +
                "id_question_threshold=" + id_question_threshold +
                ", id_question=" + id_question_fk +
                ", id_match=" + id_match_fk +
                ", minValue=" + minValue +
                ", maxValue=" + maxValue +
                '}';
    }
}
