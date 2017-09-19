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

import static org.eyeseetea.malariacare.data.database.AppDatabase.treatmentAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.treatmentMatchAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.treatmentMatchName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.treatmentName;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class, name = "Match")
public class MatchDB extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_match;

    @Column
    Long id_question_relation_fk;
    /**
     * Reference to the associated mQuestionRelationDB (loaded lazily)
     */
    QuestionRelationDB mQuestionRelationDB;

    /**
     * List of mQuestionOptionDBs associated to this mMatchDB
     */
    List<QuestionOptionDB> mQuestionOptionDBs;

    public MatchDB() {
    }

    public MatchDB(QuestionRelationDB questionRelationDB) {
        setQuestionRelationDB(questionRelationDB);
    }

    public static MatchDB findById(long id) {
        return new Select()
                .from(MatchDB.class)
                .where(MatchDB_Table.id_match.is(id))
                .querySingle();
    }

    public static List<MatchDB> listAll() {
        return new Select().from(MatchDB.class).queryList();
    }

    public static void deleteAll() {
        deleteMatches(listAll());
    }

    public List<QuestionOptionDB> getQuestionOptionDBs() {
        if (mQuestionOptionDBs == null) {
            this.mQuestionOptionDBs = new Select().from(QuestionOptionDB.class)
                    .where(QuestionOptionDB_Table.id_match_fk.eq(this.getId_match()))
                    .queryList();
        }
        return this.mQuestionOptionDBs;
    }

    /**
     * Get all questionThresholds mMatchDBs with the mMatchDB passed.
     */
    private static List<QuestionThresholdDB> getQuestionThreshold(MatchDB matchDB) {
        return new Select().from(QuestionThresholdDB.class)
                .where(QuestionThresholdDB_Table.id_match_fk.eq(
                        matchDB.getId_match())).queryList();
    }

    /**
     * Get all mQuestionOptionDBs mMatchDBs with the mMatchDB passed.
     */
    private static List<QuestionOptionDB> getQuestionOptions(MatchDB matchDB) {
        return new Select().from(QuestionOptionDB.class)
                .where(QuestionOptionDB_Table.id_match_fk.eq(
                        matchDB.getId_match())).queryList();
    }

    /**
     * Get all mTreatmentDB mMatchDBs with the mMatchDB passed.
     */
    private static List<TreatmentMatchDB> getTreatmentMatches(MatchDB matchDB) {
        return new Select().from(TreatmentMatchDB.class)
                .where(TreatmentMatchDB_Table.id_match_fk.eq(
                        matchDB.getId_match())).queryList();
    }

    public TreatmentDB getTreatment() {
        return new Select().from(TreatmentDB.class).as(treatmentName)
                .join(TreatmentMatchDB.class, Join.JoinType.LEFT_OUTER).as(treatmentMatchName)
                .on(TreatmentDB_Table.id_treatment.withTable(treatmentAlias)
                        .eq(TreatmentMatchDB_Table.id_treatment_fk.withTable(treatmentMatchAlias)))
                .where(TreatmentMatchDB_Table.id_match_fk.withTable(treatmentMatchAlias)
                        .eq(id_match))
                .querySingle();
    }

    /**
     * Method to delete the mMatchDBs in cascade
     */
    public static void deleteMatches(List<MatchDB> matchDBs) {
        for (MatchDB matchDB : matchDBs) {
            TreatmentMatchDB.deleteTreatmentMatches(getTreatmentMatches(matchDB));
            QuestionOptionDB.deleteQuestionOptions(getQuestionOptions(matchDB));
            QuestionThresholdDB.deleteQuestionThresholds(getQuestionThreshold(matchDB));
            matchDB.delete();
        }
    }

    public long getId_match() {
        return id_match;
    }

    public void setId_match(long id_match) {
        this.id_match = id_match;
    }

    public QuestionRelationDB getQuestionRelationDB() {
        if (mQuestionRelationDB == null) {
            if (id_question_relation_fk == null) return null;
            mQuestionRelationDB = new Select()
                    .from(QuestionRelationDB.class)
                    .where(QuestionRelationDB_Table.id_question_relation
                            .is(id_question_relation_fk)).querySingle();
        }
        return mQuestionRelationDB;
    }

    public void setQuestionRelationDB(QuestionRelationDB questionRelationDB) {
        this.mQuestionRelationDB = questionRelationDB;
        this.id_question_relation_fk =
                (questionRelationDB != null) ? questionRelationDB.getId_question_relation() : null;
    }

    public void setQuestionRelation(Long id_question_relation) {
        this.id_question_relation_fk = id_question_relation;
        this.mQuestionRelationDB = null;
    }

    /**
     * Returns the threshold associated with this questionoption
     */
    public QuestionThresholdDB getQuestionThreshold() {
        //Find threshold with this mMatchDB
        return new Select().from(QuestionThresholdDB.class)
                .where(QuestionThresholdDB_Table.id_match_fk
                        .is(id_match)).querySingle();
    }

    /**
     * Returns the mQuestionDB from QuestionRelationDB for this mMatchDB with the given operationType
     */
    public QuestionDB getQuestionFromRelationWithType(int operationType) {
        QuestionRelationDB questionRelationDB = this.getQuestionRelationDB();
        if (questionRelationDB == null || questionRelationDB.getOperation() != operationType) {
            return null;
        }

        return questionRelationDB.getQuestionDB();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatchDB matchDB = (MatchDB) o;

        if (id_match != matchDB.id_match) return false;
        return !(id_question_relation_fk != null ? !id_question_relation_fk.equals(
                matchDB.id_question_relation_fk) : matchDB.id_question_relation_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_match ^ (id_match >>> 32));
        result = 31 * result + (id_question_relation_fk != null ? id_question_relation_fk.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Match{" +
                "id_match=" + id_match +
                ", id_question_relation_fk=" + id_question_relation_fk +
                '}';
    }
}
