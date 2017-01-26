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

import static org.eyeseetea.malariacare.data.database.AppDatabase.questionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.valueAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.valueName;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class)
public class Value extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_value;
    @Column
    String value;

    @Column
    Long id_question;
    /**
     * Reference to the question for this value (loaded lazily)
     */
    Question question;

    @Column
    Long id_survey;
    /**
     * Reference to the survey of this value (loaded lazily)
     */
    Survey survey;

    @Column
    Long id_option;
    /**
     * Reference to the option of this value (loaded lazily)
     */
    Option option;

    public Value() {
    }

    public Value(String value, Question question, Survey survey) {
        this.option = null;
        this.value = value;
        this.setQuestion(question);
        this.setSurvey(survey);
    }

    public Value(Option option, Question question, Survey survey) {
        this.value = (option != null) ? option.getName() : null;
        this.setOption(option);
        this.setQuestion(question);
        this.setSurvey(survey);
    }

    public static int countBySurvey(Survey survey) {
        if (survey == null || survey.getId_survey() == null) {
            return 0;
        }
        return (int) SQLite.selectCountOf()
                .from(Value.class)
                .where(Value_Table.id_survey.eq(survey.getId_survey())).count();
    }

    /**
     * List ordered values of the survey
     */
    public static List<Value> listAllBySurvey(Survey survey) {
        if (survey == null || survey.getId_survey() == null) {
            return new ArrayList<>();
        }

        return new Select().from(Value.class).as(valueName)
                .join(Question.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(Value_Table.id_question.withTable(valueAlias)
                        .eq(Question_Table.id_question.withTable(questionAlias)))
                .where(Value_Table.id_survey.withTable(valueAlias)
                        .eq(survey.getId_survey()))
                .orderBy(OrderBy.fromProperty(Question_Table.order_pos).ascending()).queryList();
    }

    public Long getId_value() {
        return id_value;
    }

    public void setId_value(Long id_value) {
        this.id_value = id_value;
    }

    public Long getId_option() {
        return this.id_option;
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

    public void setOption(Option option) {
        this.option = option;
        this.id_option = (option != null) ? option.getId_option() : null;
    }

    public void setOption(Long id_option) {
        this.id_option = id_option;
        this.option = null;
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

    public void setQuestion(Question question) {
        this.question = question;
        this.id_question = (question != null) ? question.getId_question() : null;
    }

    public void setQuestion(Long id_question) {
        this.id_question = id_question;
        this.question = null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Survey getSurvey() {
        if (survey == null) {
            if (id_survey == null) return null;
            survey = new Select()
                    .from(Survey.class)
                    .where(Survey_Table.id_survey
                            .is(id_survey)).querySingle();
        }
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
        this.id_survey = (survey != null) ? survey.getId_survey() : null;
    }

    public void setSurvey(Long id_survey) {
        this.id_survey = id_survey;
        this.survey = null;
    }

    /**
     * The value is 'Positive' from a dropdown
     *
     * @return true|false
     */
    public boolean isAPositive() {
        return getOption() != null && getOption().getName().equals("Positive");
    }

    /**
     * Checks if the current value contains an answer
     *
     * @return true|false
     */
    public boolean isAnAnswer() {
        return (getValue() != null && !getValue().equals("")) || getOption() != null;
    }

    /**
     * Checks if the current value belongs to a 'required' question
     */
    public boolean belongsToAParentQuestion() {
        return !getQuestion().hasParent();
    }

    /**
     * The value is 'Yes' from a dropdown
     *
     * @return true|false
     */
    public boolean isAYes() {
        return getOption() != null && getOption().getName().equals("Yes");
    }

    /**
     * Checks if this value matches the given question and option
     */
    public boolean matchesQuestionOption(Long idQuestion, Long idOption) {

        //No question or option -> no match
        if (idQuestion == null || idOption == null) {
            return false;
        }

        //Check if both matches
        return idQuestion == this.id_question && idOption == this.id_option;
    }

    /**
     * Checks if this value matches the given question
     */
    public boolean matchesQuestion(Long idQuestion) {

        //No question or option -> no match
        if (idQuestion == null) {
            return false;
        }

        //Check if both matches
        return idQuestion == this.id_question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value value1 = (Value) o;

        if (id_value != value1.id_value) return false;
        if (value != null ? !value.equals(value1.value) : value1.value != null) return false;
        if (id_question != null ? !id_question.equals(value1.id_question)
                : value1.id_question != null) {
            return false;
        }
        if (id_survey != null ? !id_survey.equals(value1.id_survey) : value1.id_survey != null) {
            return false;
        }
        return !(id_option != null ? !id_option.equals(value1.id_option)
                : value1.id_option != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_value ^ (id_value >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (id_question != null ? id_question.hashCode() : 0);
        result = 31 * result + (id_survey != null ? id_survey.hashCode() : 0);
        result = 31 * result + (id_option != null ? id_option.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Value{" +
                "id_value=" + id_value +
                ", value='" + value + '\'' +
                ", id_question=" + id_question +
                ", id_survey=" + id_survey +
                ", id_option=" + id_option +
                '}';
    }
}
