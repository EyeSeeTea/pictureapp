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

import static org.eyeseetea.malariacare.data.database.AppDatabase.matchName;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class)
public class QuestionOption extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_question_option;

    @Column
    Long id_option_fk;
    /**
     * Reference to its mOptionDB (lazy)
     */
    OptionDB mOptionDB;

    @Column
    Long id_question_fk;
    /**
     * Reference to its question (lazy)
     */
    Question question;

    @Column
    Long id_match_fk;
    /**
     * Reference to its mMatchDB (lazy)
     */
    MatchDB mMatchDB;

    public QuestionOption() {
    }

    public QuestionOption(OptionDB optionDB, Question question, MatchDB matchDB) {
        setQuestion(question);
        setOption(optionDB);
        setMatch(matchDB);
    }

    /**
     * Returns the QuestionOptions for the given question and mOptionDB
     */
    public static List<QuestionOption> findByQuestionAndOption(Question questionWithOption,
            OptionDB optionDB) {
        if (questionWithOption == null || optionDB == null) {
            return null;
        }

        return new Select().from(QuestionOption.class)
                .where(QuestionOption_Table.id_question_fk
                        .is(questionWithOption.getId_question()))
                .and(QuestionOption_Table.id_option_fk
                        .is(optionDB.getId_option()))
                .queryList();
    }

    /**
     * Find questionOptions related with the given questionRelation by its mMatchDB
     */
    public static List<QuestionOption> findByQuestionRelation(QuestionRelation questionRelation) {
        return new Select().from(QuestionOption.class).as(AppDatabase.questionOptionName)
                .join(MatchDB.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOption_Table.id_match_fk.withTable(AppDatabase.questionOptionAlias)
                        .eq(MatchDB_Table.id_match.withTable(AppDatabase.matchAlias)))
                .where(MatchDB_Table.id_question_relation_fk.withTable(AppDatabase.matchAlias).eq(
                        questionRelation.getId_question_relation())).queryList();
    }

    /**
     * Method to get the mMatchDBs in questionOption with a question id and a a mOptionDB
     *
     * @param id_question the id of the question
     * @param id_option   the id of the mOptionDB
     * @return The list of mMatchDBs
     */
    public static List<MatchDB> getMatchesWithQuestionOption(Long id_question, Long id_option) {
        List<QuestionOption> questionOptions = new Select()
                .from(QuestionOption.class)
                .where(QuestionOption_Table.id_question_fk.is(id_question))
                .and(QuestionOption_Table.id_option_fk.is(id_option))
                .queryList();
        List<MatchDB> matchDBs = new ArrayList<>();
        for (QuestionOption questionOption : questionOptions) {
            matchDBs.add(new Select()
                    .from(MatchDB.class)
                    .where(MatchDB_Table.id_match.is(questionOption.getMatchDB().getId_match()))
                    .querySingle());
        }
        return matchDBs;
//FIXME doing in two query because there is a bug in DBFlow
    }

    /**
     * Method to delete in cascade the questionOptions passed.
     *
     * @param questionsOptions The question mOptionDB to delete.
     */
    public static void deleteQuestionOptions(List<QuestionOption> questionsOptions) {
        for (QuestionOption questionOption : questionsOptions) {
            questionOption.delete();
        }
    }

    public static List<QuestionOption> listAll() {
        return new Select().from(QuestionOption.class).queryList();
    }

    public long getId_question_option() {
        return id_question_option;
    }

    public void setId_question_option(long id_question_option) {
        this.id_question_option = id_question_option;
    }

    public OptionDB getOptionDB() {
        if (mOptionDB == null) {
            if (id_option_fk == null) return null;
            mOptionDB = new Select()
                    .from(OptionDB.class)
                    .where(OptionDB_Table.id_option
                            .is(id_option_fk)).querySingle();
        }
        return mOptionDB;
    }

    public void setOptionDB(Long id_option) {
        this.id_option_fk = id_option;
        this.mOptionDB = null;
    }

    public void setOption(OptionDB optionDB) {
        this.mOptionDB = optionDB;
        this.id_option_fk = (optionDB != null) ? optionDB.getId_option() : null;
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

    public void setQuestion(Long id_question) {
        this.id_question_fk = id_question;
        this.question = null;
    }

    public void setQuestion(Question question) {
        this.question = question;
        this.id_question_fk = (question != null) ? question.getId_question() : null;
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

    public void setMatchDB(Long id_match) {
        this.id_match_fk = id_match;
        this.mMatchDB = null;
    }

    public void setMatch(MatchDB matchDB) {
        this.mMatchDB = matchDB;
        this.id_match_fk = (matchDB != null) ? matchDB.getId_match() : null;
    }

    /**
     * Returns the threshold associated with this questionoption
     */
    public QuestionThreshold getQuestionThreshold() {
        MatchDB matchDB = getMatchDB();

        //No mMatchDB -> no threshold
        if (matchDB == null) {
            return null;
        }

        return matchDB.getQuestionThreshold();
    }

    public static List<QuestionOption> getQuestionOptionsWithMatchId(Long id_match) {
        return new Select()
                .from(QuestionOption.class)
                .where(QuestionOption_Table.id_match_fk.is(id_match))
                .queryList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionOption that = (QuestionOption) o;

        if (id_question_option != that.id_question_option) return false;
        if (id_option_fk != null ? !id_option_fk.equals(that.id_option_fk) : that.id_option_fk != null) {
            return false;
        }
        if (id_question_fk != null ? !id_question_fk.equals(that.id_question_fk)
                : that.id_question_fk != null) {
            return false;
        }
        return !(id_match_fk != null ? !id_match_fk.equals(that.id_match_fk) : that.id_match_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_question_option ^ (id_question_option >>> 32));
        result = 31 * result + (id_option_fk != null ? id_option_fk.hashCode() : 0);
        result = 31 * result + (id_question_fk != null ? id_question_fk.hashCode() : 0);
        result = 31 * result + (id_match_fk != null ? id_match_fk.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QuestionOption{" +
                "id_question_option=" + id_question_option +
                ", id_option_fk=" + id_option_fk +
                ", id_question_fk=" + id_question_fk +
                ", id_match_fk=" + id_match_fk +
                '}';
    }


}
