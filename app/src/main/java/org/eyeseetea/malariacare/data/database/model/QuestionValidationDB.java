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
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

@Table(database = AppDatabase.class, name="QuestionValidation")
public class QuestionValidationDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_question_validation;
    @Column
    String regexp;
    @Column
    String message;

    public QuestionValidationDB() {
    }

    public QuestionValidationDB(String regexp, String message) {
        this.regexp = regexp;
        this.message = message;
    }

    public long getId_question_validation() {
        return id_question_validation;
    }

    public void setId_question_validation(long id_question_validation) {
        this.id_question_validation = id_question_validation;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionValidationDB that = (QuestionValidationDB) o;

        if (id_question_validation != that.id_question_validation) return false;
        if (regexp != null ? !regexp.equals(that.regexp) : that.regexp != null) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id_question_validation ^ (id_question_validation >>> 32));
        result = 31 * result + (regexp != null ? regexp.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "QuestionValidationDB{" +
                "id_question_validation=" + id_question_validation +
                ", regexp='" + regexp + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
