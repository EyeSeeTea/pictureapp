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
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionOptionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionOptionName;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class QuestionOption extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_question_option;

    @Column
    Long id_option;
    /**
     * Reference to its option (lazy)
     */
    Option option;

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

    public QuestionOption() {
    }

    public QuestionOption(Option option, Question question, Match match) {
        setQuestion(question);
        setOption(option);
        setMatch(match);
    }

    /**
     * Returns the QuestionOptions for the given question and option
     */
    public static List<QuestionOption> findByQuestionAndOption(Question questionWithOption,
            Option option) {
        if (questionWithOption == null || option == null) {
            return null;
        }

        return new Select().from(QuestionOption.class)
                .where(QuestionOption_Table.id_question
                        .is(questionWithOption.getId_question()))
                .and(QuestionOption_Table.id_option
                        .is(option.getId_option()))
                .queryList();
    }

    /**
     * Find questionOptions related with the given questionRelation by its match
     */
    public static List<QuestionOption> findByQuestionRelation(QuestionRelation questionRelation) {
        return new Select().from(QuestionOption.class).as(AppDatabase.questionOptionName)
                .join(Match.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOption_Table.id_match.withTable(AppDatabase.questionOptionAlias)
                        .eq(Match_Table.id_match.withTable(AppDatabase.matchAlias)))
                .where(Match_Table.id_question_relation.withTable(AppDatabase.matchAlias).eq(
                        questionRelation.getId_question_relation())).queryList();
    }

    /**
     * Method to get the matches in questionOption with a question id and a a option
     *
     * @param id_question the id of the question
     * @param id_option   the id of the option
     * @return The list of matches
     */
    public static List<Match> getMatchesWithQuestionOption(Long id_question, Long id_option) {
        return new Select().from(Match.class).as(matchName)
                .join(QuestionOption.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(Match_Table.id_match.withTable(matchAlias)
                        .eq(QuestionOption_Table.id_question.withTable(questionOptionAlias)))
                .where(QuestionOption_Table.id_question.withTable(questionOptionAlias).is(
                        id_question))
                .and(QuestionOption_Table.id_option.withTable(questionOptionAlias).is(
                        id_option)).queryList();
    }

    /**
     * Method to delete in cascade the questionOptions passed.
     *
     * @param questionsOptions The question option to delete.
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

    public Option getOption() {
        if (option == null) {
            if (id_option == null) return null;
            option = new Select()
                    .from(Option.class)
                    .where(Option_Table.id_option
                            .is(id_option)).querySingle();
        }
        return option;
    }

    public void setOption(Long id_option) {
        this.id_option = id_option;
        this.option = null;
    }

    public void setOption(Option option) {
        this.option = option;
        this.id_option = (option != null) ? option.getId_option() : null;
    }

    public Question getQuestion() {
        if (question == null) {
            if (id_question == null) return null;
            question = new Select()
                    .from(Question.class)
                    .where(Question_Table.id_question
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
                    .where(Match_Table.id_match
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

    /**
     * Returns the threshold associated with this questionoption
     */
    public QuestionThreshold getQuestionThreshold() {
        Match match = getMatch();

        //No match -> no threshold
        if (match == null) {
            return null;
        }

        return match.getQuestionThreshold();
    }

    public static List<QuestionOption> getQuestionOptionsWithMatchId(Long id_match) {
        return new Select()
                .from(QuestionOption.class)
                .where(Condition.column(QuestionOption$Table.ID_MATCH)
                        .is(id_match)).queryList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionOption that = (QuestionOption) o;

        if (id_question_option != that.id_question_option) return false;
        if (id_option != null ? !id_option.equals(that.id_option) : that.id_option != null) {
            return false;
        }
        if (id_question != null ? !id_question.equals(that.id_question)
                : that.id_question != null) {
            return false;
        }
        return !(id_match != null ? !id_match.equals(that.id_match) : that.id_match != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_question_option ^ (id_question_option >>> 32));
        result = 31 * result + (id_option != null ? id_option.hashCode() : 0);
        result = 31 * result + (id_question != null ? id_question.hashCode() : 0);
        result = 31 * result + (id_match != null ? id_match.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QuestionOption{" +
                "id_question_option=" + id_question_option +
                ", id_option=" + id_option +
                ", id_question=" + id_question +
                ", id_match=" + id_match +
                '}';
    }


}
