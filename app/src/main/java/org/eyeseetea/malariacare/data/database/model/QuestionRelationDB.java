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

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

/**
 * Created by Jose on 25/05/2015.
 */
@Table(database = AppDatabase.class, name = "QuestionRelation")

public class QuestionRelationDB extends BaseModel {

    /**
     * Constant that reflects a mMatchDB relationship
     */
    public static final int MATCH = 0;
    /**
     * Constant that reflects a parent child relationship
     */
    public static final int PARENT_CHILD = 1;
    /**
     * Constant that reflects a counter relationship
     */
    public static final int COUNTER = 2;
    /**
     * Constant that reflects a warning (validation) relationship
     */
    public static final int WARNING = 3;
    /**
     * Constant that reflects a reminder (something that shows up only if a current value is XX for
     * some related mQuestionDB) relationship
     */
    public static final int REMINDER = 4;

    public static final int TREATMENT_MATCH = 5;

    /**
     * This mMatchDB propagate the value from questionrelation mQuestionDB to the matched mQuestionDB
     */
    public static final int MATCH_PROPAGATE = 6;

    private static final String TAG = ".QuestionRelationDB";
    @Column
    @PrimaryKey(autoincrement = true)
    long id_question_relation;
    @Column
    Long id_question_fk;
    /**
     * Reference to associated mQuestionDB (loaded lazily)
     */
    QuestionDB mQuestionDB;

    @Column
    int operation;

    /**
     * List of mMatchDBs associated to this mQuestionRelationDB
     */
    List<MatchDB> mMatchDBs;

    public QuestionRelationDB() {
    }

    public QuestionRelationDB(QuestionDB questionDB, int operation) {
        this.operation = operation;
        this.setQuestionDB(questionDB);
    }

    public static QuestionRelationDB findById(Long id) {
        return new Select()
                .from(QuestionRelationDB.class)
                .where(QuestionRelationDB_Table.id_question_relation.is(id))
                .querySingle();
    }

    public static List<QuestionRelationDB> listAllParentChildRelations() {
        return new Select().from(QuestionRelationDB.class)
                .where(QuestionRelationDB_Table.operation.eq(
                        QuestionRelationDB.PARENT_CHILD)).queryList();
    }

    public static List<QuestionRelationDB> listAll() {
        return new Select().from(QuestionRelationDB.class).queryList();
    }

    /**
     * Method to delete in cascade the mQuestionDBs passed.
     */
    public static void deleteQuestionRelations(List<QuestionRelationDB> questionRelationDBs) {
        for (QuestionRelationDB questionRelationDB : questionRelationDBs) {
            MatchDB.deleteMatches(questionRelationDB.getAllMatches());
            questionRelationDB.delete();
        }
    }

    /**
     * Method to get the mMatchDBs with the id of this mQuestionDB relation
     */
    public List<MatchDB> getAllMatches() {
        return new Select().from(MatchDB.class)
                .where(MatchDB_Table.id_question_relation_fk.eq(
                        getId_question_relation())).queryList();
    }

    public long getId_question_relation() {
        return id_question_relation;
    }

    public void setId_question_relation(long id_question_relation) {
        this.id_question_relation = id_question_relation;
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

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public void createMatchFromQuestions(List<QuestionDB> children) {
        if (children.size() != 2) {
            Log.e(TAG, "createMatchFromQuestions(): children must be 2. Match not created");
            return;
        }
        MatchDB matchDB;
        for (OptionDB optionDBA : children.get(0).getAnswerDB().getOptionDBs()) {
            for (OptionDB optionDBB : children.get(1).getAnswerDB().getOptionDBs()) {
                if (optionDBA.getFactor().equals(optionDBB.getFactor())) {
                    //Save all optiona factor optionb factor with the same mMatchDB
                    matchDB = new MatchDB(this);
                    matchDB.save();
                    new QuestionOptionDB(optionDBA, children.get(0), matchDB).save();
                    new QuestionOptionDB(optionDBB, children.get(1), matchDB).save();
                }
            }
        }
    }

    public List<MatchDB> getMatchDBs() {
        if (mMatchDBs == null) {
            this.mMatchDBs = new Select().from(MatchDB.class)
                    //// FIXME: 29/12/16
                    //.indexedBy(Constants.MATCH_QUESTION_RELATION_IDX)
                    .where(MatchDB_Table.id_question_relation_fk.eq(
                            this.getId_question_relation()))
                    .queryList();
        }
        return mMatchDBs;
    }

    /**
     * Returns if this operation is a MatchDB relationship
     */
    public boolean isAMatch() {
        return this.operation == MATCH;
    }

    /**
     * Returns if this operation is a ParentChild relationship
     */
    public boolean isAParentChild() {
        return this.operation == PARENT_CHILD;
    }

    /**
     * Returns if this operation is a Counter relationship
     */
    public boolean isACounter() {
        return this.operation == COUNTER;
    }

    /**
     * Returns if this operation is a Warning relationship
     */
    public boolean isAWarning() {
        return this.operation == WARNING;
    }

    /**
     * Returns if this operation is a Reminder relationship
     */
    public boolean isAReminder() {
        return this.operation == REMINDER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuestionRelationDB that = (QuestionRelationDB) o;

        if (id_question_relation != that.id_question_relation) return false;
        if (operation != that.operation) return false;
        return !(id_question_fk != null ? !id_question_fk.equals(that.id_question_fk)
                : that.id_question_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_question_relation ^ (id_question_relation >>> 32));
        result = 31 * result + (id_question_fk != null ? id_question_fk.hashCode() : 0);
        result = 31 * result + operation;
        return result;
    }

    @Override
    public String toString() {
        return "QuestionRelationDB{" +
                "id_question_relation=" + id_question_relation +
                ", id_question=" + id_question_fk +
                ", operation=" + operation +
                '}';
    }


}
