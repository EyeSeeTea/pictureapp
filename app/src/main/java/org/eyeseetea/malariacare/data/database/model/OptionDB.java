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

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

@Table(database = AppDatabase.class, name = "Option")
public class OptionDB extends BaseModel {

    //FIXME A 'Yes' mAnswerDB shows children mQuestionDBs, this should be configurable by some
    // additional attribute in OptionDB
    public static final String CHECKBOX_YES_OPTION = "Yes";

    public static final int DOESNT_MATCH_POSITION = 0;
    public static final int MATCH_POSITION = 1;

    @Column
    @PrimaryKey(autoincrement = true)
    long id_option;
    //Fixme the code is used as name and the name is used as code
    @Column
    String code;
    @Column
    String name;
    @Column
    Float factor;
    @Column
    Long id_answer_fk;

    /**
     * Reference to parent mAnswerDB (loaded lazily)
     */
    AnswerDB mAnswerDB;

    @Column
    long id_option_attribute_fk;

    /**
     * Reference to extended mOptionDB attributes (loaded lazily)
     */
    OptionAttributeDB mOptionAttributeDB;

    /**
     * List of values that has choosen this mOptionDB
     */
    List<ValueDB> mValueDBs;

    public OptionDB() {
    }

    public OptionDB(String code, Float factor, AnswerDB answerDB) {
        this.code = code;
        this.factor = factor;
        this.setAnswerDB(answerDB);
    }

    public OptionDB(String name, String code, Float factor, AnswerDB answerDB) {
        this.name = name;
        this.factor = factor;
        this.code = code;
        this.setAnswerDB(answerDB);
    }


    public OptionDB(String code) {
        this.code = code;
    }

    public static List<OptionDB> getAllOptions() {
        return new Select().from(OptionDB.class).queryList();
    }

    public static OptionDB findById(Long id) {
        return new Select()
                .from(OptionDB.class)
                .where(OptionDB_Table.id_option.eq(id)).querySingle();
    }

    public static OptionDB findByName(String name) {
        return new Select()
                .from(OptionDB.class)
                .where(OptionDB_Table.name.eq(name)).querySingle();
    }

    public static OptionDB findByCode(String code) {
        return new Select()
                .from(OptionDB.class)
                .where(OptionDB_Table.code.eq(code)).querySingle();
    }


    public Long getId_option() {
        return id_option;
    }

    public void setId_option(Long id_option) {
        this.id_option = id_option;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternationalizedName() {
        return Utils.getInternationalizedString(name);
    }

    public String getInternationalizedCode() {
        return Utils.getInternationalizedString(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Float getFactor() {
        return factor;
    }

    public void setFactor(Float factor) {
        this.factor = factor;
    }

    public AnswerDB getAnswerDB() {
        if (mAnswerDB == null) {
            if (id_answer_fk == null) return null;
            mAnswerDB = new Select()
                    .from(AnswerDB.class)
                    .where(AnswerDB_Table.id_answer
                            .is(id_answer_fk)).querySingle();
        }
        return mAnswerDB;
    }

    public void setAnswerDB(AnswerDB answerDB) {
        this.mAnswerDB = answerDB;
        this.id_answer_fk = (answerDB != null) ? answerDB.getId_answer() : null;
    }

    public OptionAttributeDB getOptionAttributeDB() {
        if (mOptionAttributeDB == null) {
            mOptionAttributeDB = new Select().from(
                    OptionAttributeDB.class)
                    .where(OptionAttributeDB_Table.id_option_attribute.eq(
                            id_option_attribute_fk)).querySingle();
        }
        return mOptionAttributeDB;
    }

    public void setOptionAttributeDB(
            OptionAttributeDB optionAttributeDB) {
        this.mOptionAttributeDB = optionAttributeDB;
        this.id_option_attribute_fk =
                (optionAttributeDB != null) ? optionAttributeDB.getId_option_attribute() : null;
    }

    /**
     * Getter for extended mOptionDB attribute 'path'
     */
    public String getPath() {
        OptionAttributeDB optionAttributeDB = this.getOptionAttributeDB();
        if (optionAttributeDB == null) {
            return null;
        }

        return optionAttributeDB.getPath();
    }

    /**
     * Getter for extended mOptionDB attribute 'path' translation in paths.xml
     */
    public String getInternationalizedPath() {
        OptionAttributeDB optionAttributeDB = this.getOptionAttributeDB();
        if (optionAttributeDB == null) {
            return null;
        }

        return optionAttributeDB.getInternationalizedPath();
    }

    /**
     * Getter for extended mOptionDB attribute 'backgroundColor'
     */
    public String getBackground_colour() {
        OptionAttributeDB optionAttributeDB = this.getOptionAttributeDB();
        if (optionAttributeDB == null) {
            return null;
        }

        return optionAttributeDB.getBackground_colour();
    }

    /**
     * Looks for the value with the given mQuestionDB  is the provided mOptionDB
     */
    public static boolean findOption(Long idQuestion, Long idOption, SurveyDB surveyDB) {
        ValueDB valueDB = ValueDB.findValue(idQuestion, surveyDB);
        if (valueDB == null) {
            return false;
        }

        Long valueIdOption = valueDB.getId_option();
        return idOption.equals(valueIdOption);
    }

    /**
     * Gets the QuestionDB of this OptionDB in session
     */
    public QuestionDB getQuestionBySession() {
        return getQuestionBySurvey(Session.getMalariaSurveyDB());
    }

    /**
     * Gets the QuestionDB of this OptionDB in the given Survey
     */
    public QuestionDB getQuestionBySurvey(
            SurveyDB surveyDB) {
        if (surveyDB == null) {
            return null;
        }
        List<ValueDB> returnValueDBs = new Select().from(ValueDB.class)
                //// FIXME: 29/12/16  indexed
                //.indexedBy(Constants.VALUE_IDX)
                .where(ValueDB_Table.id_option_fk.eq(this.getId_option()))
                .and(ValueDB_Table.id_survey_fk.eq(surveyDB.getId_survey())).queryList();

        return (returnValueDBs.size() == 0) ? null : returnValueDBs.get(0).getQuestionDB();
    }


    /**
     * Checks if this mOptionDB actives the children mQuestionDBs
     *
     * @return true: Children mQuestionDBs should be shown, false: otherwise.
     */
    public boolean isActiveChildren() {
        return CHECKBOX_YES_OPTION.equals(name);
    }

    /**
     * Checks if this mOptionDB name is equals to a given string.
     *
     * @return true|false
     */
    public boolean is(String given) {
        return given.equals(name);
    }

    public List<ValueDB> getValueDBs() {
        if (mValueDBs == null) {
            mValueDBs = new Select().from(ValueDB.class)
                    .where(ValueDB_Table.id_option_fk.eq(this.getId_option())).queryList();
        }
        return mValueDBs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionDB optionDB = (OptionDB) o;

        if (id_option != optionDB.id_option) return false;
        if (id_option_attribute_fk != optionDB.id_option_attribute_fk) return false;
        if (code != null ? !code.equals(optionDB.code) : optionDB.code != null) return false;
        if (name != null ? !name.equals(optionDB.name) : optionDB.name != null) return false;
        if (factor != null ? !factor.equals(optionDB.factor) : optionDB.factor != null) {
            return false;
        }
        return !(id_answer_fk != null ? !id_answer_fk.equals(optionDB.id_answer_fk)
                : optionDB.id_answer_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_option ^ (id_option >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (factor != null ? factor.hashCode() : 0);
        result = 31 * result + (id_answer_fk != null ? id_answer_fk.hashCode() : 0);
        result = 31 * result + (int) (id_option_attribute_fk ^ (id_option_attribute_fk >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "OptionDB{" +
                "id_option=" + id_option +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", factor=" + factor +
                ", id_answer_fk=" + id_answer_fk +
                ", id_option_attribute_fk=" + id_option_attribute_fk +
                '}';
    }

    @Override
    public void delete() {
        super.delete();
    }
}
