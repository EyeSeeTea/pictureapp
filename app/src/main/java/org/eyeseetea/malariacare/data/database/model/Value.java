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
     * Reference to the mQuestionDB for this value (loaded lazily)
     */
    QuestionDB mQuestionDB;

    @Column
    Long id_survey_fk;
    /**
     * Reference to the survey of this value (loaded lazily)
     */
    SurveyDB mSurveyDB;

    @Column
    Long id_option_fk;
    /**
     * Reference to the mOptionDB of this value (loaded lazily)
     */
    OptionDB mOptionDB;

    public Value() {
    }

    public Value(String value, QuestionDB questionDB, SurveyDB surveyDB) {
        this.mOptionDB = null;
        this.value = value;
        this.setQuestionDB(questionDB);
        this.setSurveyDB(surveyDB);
    }

    public Value(OptionDB optionDB, QuestionDB questionDB, SurveyDB surveyDB) {
        this.value = (optionDB != null) ? optionDB.getCode() : null;
        this.setOptionDB(optionDB);
        this.setQuestionDB(questionDB);
        this.setSurveyDB(surveyDB);
    }

    public static int countBySurvey(SurveyDB surveyDB) {
        if (surveyDB == null || surveyDB.getId_survey() == null) {
            return 0;
        }
        return (int) SQLite.selectCountOf()
                .from(Value.class)
                .where(Value_Table.id_survey_fk.eq(surveyDB.getId_survey())).count();
    }

    /**
     * List ordered values of the survey
     */
    public static List<Value> listAllBySurvey(SurveyDB surveyDB) {
        if (surveyDB == null || surveyDB.getId_survey() == null) {
            return new ArrayList<>();
        }

        return new Select().from(Value.class).as(valueName)
                .join(QuestionDB.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(Value_Table.id_question_fk.withTable(valueAlias)
                        .eq(QuestionDB_Table.id_question.withTable(questionAlias)))
                .where(Value_Table.id_survey_fk.withTable(valueAlias)
                        .eq(surveyDB.getId_survey()))
                .orderBy(OrderBy.fromProperty(QuestionDB_Table.order_pos).ascending()).queryList();
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

    public void setOptionDB(OptionDB optionDB) {
        this.mOptionDB = optionDB;
        this.id_option_fk = (optionDB != null) ? optionDB.getId_option() : null;
    }

    public void setOption(Long id_option) {
        this.id_option_fk = id_option;
        this.mOptionDB = null;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SurveyDB getSurveyDB() {
        if (mSurveyDB == null) {
            if (id_survey_fk == null) return null;
            mSurveyDB = new Select()
                    .from(SurveyDB.class)
                    .where(SurveyDB_Table.id_survey
                            .is(id_survey_fk)).querySingle();
        }
        return mSurveyDB;
    }

    public void setSurveyDB(SurveyDB surveyDB) {
        this.mSurveyDB = surveyDB;
        this.id_survey_fk = (surveyDB != null) ? surveyDB.getId_survey() : null;
    }

    public void setSurvey(Long id_survey) {
        this.id_survey_fk = id_survey;
        this.mSurveyDB = null;
    }
    /**
     * Looks for the value with the given mQuestionDB
     */
    public static Value findValueFromDatabase(Long idQuestion, SurveyDB surveyDB) {
        for (Value value : surveyDB.getValuesFromDBWithoutSave()) {
            if (value.matchesQuestion(idQuestion)) {
                return value;
            }
        }
        //No mMatchDBs -> null
        return null;
    }

    /**
     * Looks for the value with the given mQuestionDB
     */
    public static Value findValue(Long idQuestion, SurveyDB surveyDB) {
        for (Value value : surveyDB.getValues()) {
            if (value.matchesQuestion(idQuestion)) {
                return value;
            }
        }
        //No mMatchDBs -> null
        return null;
    }

    /**
     * Looks for the value with the given mQuestionDB + mOptionDB
     */
    public static Value findValue(Long idQuestion, Long idOption, SurveyDB surveyDB) {
        for (Value value : surveyDB.getValues()) {
            if (value.matchesQuestionOption(idQuestion, idOption)) {
                return value;
            }
        }
        //No mMatchDBs -> null
        return null;
    }

    /**
     * The value is 'Positive' from a dropdown
     *
     * @return true|false
     */
    public boolean isAPositive() {
        return getOptionDB() != null && getOptionDB().getCode().equals("Positive");
    }

    /**
     * Checks if the current value contains an mAnswerDB
     *
     * @return true|false
     */
    public boolean isAnAnswer() {
        return (getValue() != null && !getValue().equals("")) || getOptionDB() != null;
    }

    /**
     * Checks if the current value belongs to a 'required' mQuestionDB
     */
    public boolean belongsToAParentQuestion() {
        return !getQuestionDB().hasParent();
    }

    /**
     * The value is 'Yes' from a dropdown
     *
     * @return true|false
     */
    public boolean isAYes() {
        return getOptionDB() != null && getOptionDB().getCode().equals("Yes");
    }

    /**
     * Checks if this value mMatchDBs the given mQuestionDB and mOptionDB
     */
    public boolean matchesQuestionOption(Long idQuestion, Long idOption) {

        //No mQuestionDB or mOptionDB -> no mMatchDB
        if (idQuestion == null || idOption == null) {
            return false;
        }

        //Check if both mMatchDBs
        return idQuestion == this.id_question_fk && idOption == this.id_option_fk;
    }

    /**
     * Checks if this value mMatchDBs the given mQuestionDB
     */
    public boolean matchesQuestion(Long idQuestion) {

        //No mQuestionDB or mOptionDB -> no mMatchDB
        if (idQuestion == null) {
            return false;
        }

        //Check if both mMatchDBs
        return idQuestion == this.id_question_fk;
    }

    public static void saveAll(List<Value> values, final IDataSourceCallback<Void> callback) {

        //Refresh survey for assign SurveyId
        for (Value value : values) {
            value.setSurveyDB(value.getSurveyDB());
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
