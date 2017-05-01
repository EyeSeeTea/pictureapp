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
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
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
    Long id_question_fk;
    /**
     * Reference to the question for this value (loaded lazily)
     */
    Question question;

    @Column
    Long id_survey_fk;
    /**
     * Reference to the survey of this value (loaded lazily)
     */
    Survey survey;

    @Column
    Long id_option_fk;
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
        this.value = (option != null) ? option.getCode() : null;
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
                .where(Value_Table.id_survey_fk.eq(survey.getId_survey())).count();
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
                .on(Value_Table.id_question_fk.withTable(valueAlias)
                        .eq(Question_Table.id_question.withTable(questionAlias)))
                .where(Value_Table.id_survey_fk.withTable(valueAlias)
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
        return this.id_option_fk;
    }

    public Option getOption() {
        if (option == null) {
            if (id_option_fk == null) return null;
            option = new Select()
                    .from(Option.class)
                    .where(Option_Table.id_option
                            .is(id_option_fk)).querySingle();
        }
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
        this.id_option_fk = (option != null) ? option.getId_option() : null;
    }

    public void setOption(Long id_option) {
        this.id_option_fk = id_option;
        this.option = null;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Survey getSurvey() {
        if (survey == null) {
            if (id_survey_fk == null) return null;
            survey = new Select()
                    .from(Survey.class)
                    .where(Survey_Table.id_survey
                            .is(id_survey_fk)).querySingle();
        }
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
        this.id_survey_fk = (survey != null) ? survey.getId_survey() : null;
    }

    public void setSurvey(Long id_survey) {
        this.id_survey_fk = id_survey;
        this.survey = null;
    }
    /**
     * Looks for the value with the given question
     */
    public static Value findValueFromDatabase(Long idQuestion, Survey survey) {
        for (Value value : survey.getValuesFromDBWithoutSave()) {
            if (value.matchesQuestion(idQuestion)) {
                return value;
            }
        }
        //No matches -> null
        return null;
    }

    /**
     * Looks for the value with the given question
     */
    public static Value findValue(Long idQuestion, Survey survey) {
        for (Value value : survey.getValues()) {
            if (value.matchesQuestion(idQuestion)) {
                return value;
            }
        }
        //No matches -> null
        return null;
    }

    /**
     * Looks for the value with the given question + option
     */
    public static Value findValue(Long idQuestion, Long idOption, Survey survey) {
        for (Value value : survey.getValues()) {
            if (value.matchesQuestionOption(idQuestion, idOption)) {
                return value;
            }
        }
        //No matches -> null
        return null;
    }

    /**
     * The value is 'Positive' from a dropdown
     *
     * @return true|false
     */
    public boolean isAPositive() {
        return getOption() != null && getOption().getCode().equals("Positive");
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
        return getOption() != null && getOption().getCode().equals("Yes");
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
        return idQuestion == this.id_question_fk && idOption == this.id_option_fk;
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
        return idQuestion == this.id_question_fk;
    }

    public static void saveAll(List<Value> values, final IDataSourceCallback<Void> callback) {

        //Refresh survey for assign SurveyId
        for (Value value : values) {
            value.setSurvey(value.getSurvey());
        }

        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Value>() {
                            @Override
                            public void processModel(Value value) {
                                value.save();
                            }
                        }).addAll(values).build())
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        callback.onError(error);
                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        callback.onSuccess(null);
                    }
                }).build().execute();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Value value1 = (Value) o;

        if (id_value != value1.id_value) return false;
        if (value != null ? !value.equals(value1.value) : value1.value != null) return false;
        if (id_question_fk != null ? !id_question_fk.equals(value1.id_question_fk)
                : value1.id_question_fk != null) {
            return false;
        }
        if (id_survey_fk != null ? !id_survey_fk.equals(value1.id_survey_fk) : value1.id_survey_fk != null) {
            return false;
        }
        return !(id_option_fk != null ? !id_option_fk.equals(value1.id_option_fk)
                : value1.id_option_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_value ^ (id_value >>> 32));
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (id_question_fk != null ? id_question_fk.hashCode() : 0);
        result = 31 * result + (id_survey_fk != null ? id_survey_fk.hashCode() : 0);
        result = 31 * result + (id_option_fk != null ? id_option_fk.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Value{" +
                "id_value=" + id_value +
                ", value='" + value + '\'' +
                ", id_question_fk=" + id_question_fk +
                ", id_survey_fk=" + id_survey_fk +
                ", id_option_fk=" + id_option_fk +
                '}';
    }


}
