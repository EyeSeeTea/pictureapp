/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
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

import java.util.List;

@Table(database = AppDatabase.class, name="Answer")
public class AnswerDB extends BaseModel {

    /**
     * Default mock mAnswerDB.output value
     */
    public static final Integer DEFAULT_ANSWER_OUTPUT = -1;

    /**
     * Required for creating the dynamic stock mQuestionDB in SCMM
     */
    public final static Long DYNAMIC_STOCK_ANSWER_ID = 204l;

    @Column
    @PrimaryKey(autoincrement = true)
    long id_answer;
    @Column
    String name;

    /**
     * List of mOptionDBs that belongs to this mAnswerDB type
     */
    List<OptionDB> mOptionDBs;

    /**
     * List of mOptionDBs that have this mAnswerDB type
     */
    List<QuestionDB> mQuestionDBs;

    public AnswerDB() {
    }

    public AnswerDB(String name) {
        this.name = name;
    }

    public static List<AnswerDB> getAllAnswers() {
        return new Select().from(AnswerDB.class).queryList();
    }

    public static void deleteAll() {
        for (AnswerDB answerDB : getAllAnswers()) {
            answerDB.delete();
        }
    }

    public static AnswerDB findById(Long id) {
        return new Select()
                .from(AnswerDB.class)
                .where(AnswerDB_Table.id_answer.eq(id)).querySingle();
    }

    public Long getId_answer() {
        return id_answer;
    }

    public void setId_answer(Long id_answer) {
        this.id_answer = id_answer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OptionDB> getOptionDBs() {
        if (mOptionDBs == null) {
            mOptionDBs = new Select()
                    .from(OptionDB.class)
                    .where(OptionDB_Table.id_answer_fk
                            .eq(this.getId_answer())).queryList();
        }
        return mOptionDBs;
    }

    public void setOptionDBs(List<OptionDB> optionDBs) {
        this.mOptionDBs = optionDBs;
    }

    public List<QuestionDB> getQuestionDBs() {
        if (mQuestionDBs == null) {
            mQuestionDBs = new Select()
                    .from(QuestionDB.class)
                    .where(QuestionDB_Table.id_answer_fk
                            .eq(this.getId_answer())).queryList();
        }
        return mQuestionDBs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnswerDB answerDB = (AnswerDB) o;

        if (id_answer != answerDB.id_answer) return false;
        return !(name != null ? !name.equals(answerDB.name) : answerDB.name != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_answer ^ (id_answer >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AnswerDB{" +
                "id_answer=" + id_answer +
                ", name='" + name + '\'' +
                '}';
    }


}
