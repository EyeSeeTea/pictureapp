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

@Table(database = AppDatabase.class)
public class Option extends BaseModel {

    //FIXME A 'Yes' answer shows children questions, this should be configurable by some
    // additional attribute in Option
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
     * Reference to parent answer (loaded lazily)
     */
    Answer answer;

    @Column
    long id_option_attribute_fk;

    /**
     * Reference to extended option attributes (loaded lazily)
     */
    OptionAttribute optionAttribute;

    /**
     * List of values that has choosen this option
     */
    List<Value> values;

    public Option() {
    }

    public Option(String code, Float factor, Answer answer) {
        this.code = code;
        this.factor = factor;
        this.setAnswer(answer);
    }

    public Option(String name, String code, Float factor, Answer answer) {
        this.name = name;
        this.factor = factor;
        this.code = code;
        this.setAnswer(answer);
    }


    public Option(String code) {
        this.code = code;
    }

    public static List<Option> getAllOptions() {
        return new Select().from(Option.class).queryList();
    }

    public static Option findById(Long id) {
        return new Select()
                .from(Option.class)
                .where(Option_Table.id_option.eq(id)).querySingle();
    }

    public static Option findByName(String name) {
        return new Select()
                .from(Option.class)
                .where(Option_Table.name.eq(name)).querySingle();
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

    public Answer getAnswer() {
        if (answer == null) {
            if (id_answer_fk == null) return null;
            answer = new Select()
                    .from(Answer.class)
                    .where(Answer_Table.id_answer
                            .is(id_answer_fk)).querySingle();
        }
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
        this.id_answer_fk = (answer != null) ? answer.getId_answer() : null;
    }

    public OptionAttribute getOptionAttribute() {
        if (optionAttribute == null) {
            optionAttribute = new Select().from(
                    OptionAttribute.class)
                    .where(OptionAttribute_Table.id_option_attribute.eq(
                            id_option_attribute_fk)).querySingle();
        }
        return optionAttribute;
    }

    public void setOptionAttribute(
            OptionAttribute optionAttribute) {
        this.optionAttribute = optionAttribute;
        this.id_option_attribute_fk =
                (optionAttribute != null) ? optionAttribute.getId_option_attribute() : null;
    }

    /**
     * Getter for extended option attribute 'path'
     */
    public String getPath() {
        OptionAttribute optionAttribute = this.getOptionAttribute();
        if (optionAttribute == null) {
            return null;
        }

        return optionAttribute.getPath();
    }

    /**
     * Getter for extended option attribute 'path' translation in paths.xml
     */
    public String getInternationalizedPath() {
        OptionAttribute optionAttribute = this.getOptionAttribute();
        if (optionAttribute == null) {
            return null;
        }

        return optionAttribute.getInternationalizedPath();
    }

    /**
     * Getter for extended option attribute 'backgroundColor'
     */
    public String getBackground_colour() {
        OptionAttribute optionAttribute = this.getOptionAttribute();
        if (optionAttribute == null) {
            return null;
        }

        return optionAttribute.getBackground_colour();
    }

    /**
     * Looks for the value with the given question  is the provided option
     */
    public static boolean findOption(Long idQuestion, Long idOption, Survey survey) {
        Value value = Value.findValue(idQuestion, survey);
        if (value == null) {
            return false;
        }

        Long valueIdOption = value.getId_option();
        return idOption.equals(valueIdOption);
    }

    /**
     * Gets the Question of this Option in session
     */
    public Question getQuestionBySession() {
        return getQuestionBySurvey(Session.getMalariaSurvey());
    }

    /**
     * Gets the Question of this Option in the given Survey
     */
    public Question getQuestionBySurvey(
            Survey survey) {
        if (survey == null) {
            return null;
        }
        List<Value> returnValues = new Select().from(Value.class)
                //// FIXME: 29/12/16  indexed
                //.indexedBy(Constants.VALUE_IDX)
                .where(Value_Table.id_option_fk.eq(this.getId_option()))
                .and(Value_Table.id_survey_fk.eq(survey.getId_survey())).queryList();

        return (returnValues.size() == 0) ? null : returnValues.get(0).getQuestion();
    }


    /**
     * Checks if this option actives the children questions
     *
     * @return true: Children questions should be shown, false: otherwise.
     */
    public boolean isActiveChildren() {
        return CHECKBOX_YES_OPTION.equals(name);
    }

    /**
     * Checks if this option name is equals to a given string.
     *
     * @return true|false
     */
    public boolean is(String given) {
        return given.equals(name);
    }

    public List<Value> getValues() {
        if (values == null) {
            values = new Select().from(Value.class)
                    .where(Value_Table.id_option_fk.eq(this.getId_option())).queryList();
        }
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (id_option != option.id_option) return false;
        if (id_option_attribute_fk != option.id_option_attribute_fk) return false;
        if (code != null ? !code.equals(option.code) : option.code != null) return false;
        if (name != null ? !name.equals(option.name) : option.name != null) return false;
        if (factor != null ? !factor.equals(option.factor) : option.factor != null) return false;
        return !(id_answer_fk != null ? !id_answer_fk.equals(option.id_answer_fk)
                : option.id_answer_fk != null);

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
        return "Option{" +
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
