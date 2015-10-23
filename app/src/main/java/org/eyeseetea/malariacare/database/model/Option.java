/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Survelliance App.
 *
 *  QIS Survelliance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Survelliance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Survelliance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

import java.util.List;

@Table(databaseName = AppDatabase.NAME)
public class Option extends BaseModel {

    //FIXME A 'Yes' answer shows children questions, this should be configurable by some additional attribute in Option
    public static final String CHECKBOX_YES_OPTION="Yes";

    @Column
    @PrimaryKey(autoincrement = true)
    long id_option;

    @Column
    String code;

    @Column
    String name;

    @Column
    Float factor;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_answer",
            columnType = Long.class,
            foreignColumnName = "id_answer")},
            saveForeignKeyModel = false)
    Answer answer;

    @Column
    String path;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_option_attribute",
            columnType = Long.class,
            foreignColumnName = "id_option_attribute")},
            saveForeignKeyModel = false)
    OptionAttribute optionAttribute;

    @Column
    String background_colour;

    public Option() {
    }

    public Option(String name, Float factor, Answer answer, String code, OptionAttribute optionAttribute, String background_colour) {
        this.name = name;
        this.factor = factor;
        this.answer = answer;
        this.code = code;
        this.optionAttribute = optionAttribute;
        this.background_colour = background_colour;
    }

    public Option(String name) {
        this.name = name;
    }

    public long getId_option() {
        return id_option;
    }

    public void setId_option(long id_option) {
        this.id_option = id_option;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getFactor() {
        return factor;
    }

    public void setFactor(Float factor) {
        this.factor = factor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public OptionAttribute getOptionAttribute() {
        return optionAttribute;
    }

    public void setOptionAttribute(OptionAttribute optionAttribute) {
        this.optionAttribute = optionAttribute;
    }

    public String getBackground_colour() {
        return background_colour;
    }

    public void setBackground_colour(String background_colour) {
        this.background_colour = background_colour;
    }

    /**
     * Checks if this option actives the children questions
     * @return true: Children questions should be shown, false: otherwise.
     */
    public boolean isActiveChildren(){
        return CHECKBOX_YES_OPTION.equals(name);
    }

    /**
     * Checks if this option name is equals to a given string.
     *
     * @return true|false
     */
    public boolean is(String given){
        return given.equals(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (answer != null ? !answer.equals(option.answer) : option.answer != null) return false;
        if (factor != null ? !factor.equals(option.factor) : option.factor != null) return false;
        if (code != null ? !code.equals(option.code) : option.code != null) return false;
        if (name != null ? !name.equals(option.name) : option.name != null) return false;
        if (path != null ? !path.equals(option.path) : option.path != null) return false;
        if (optionAttribute != null ? !optionAttribute.equals(option.optionAttribute) : option.optionAttribute != null) return false;
        if (background_colour != null ? !background_colour.equals(option.background_colour) : option.background_colour != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (factor != null ? factor.hashCode() : 0);
        result = 31 * result + (answer != null ? answer.hashCode() : 0);
        result = 31 * result + (optionAttribute != null ? optionAttribute.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (background_colour != null ? background_colour.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Option{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", factor=" + factor +
                ", answer=" + answer +
                ", path=" + path +
                ", optionAttribute=" + optionAttribute +
                ", background_colour=" + background_colour +
                '}';
    }
}
