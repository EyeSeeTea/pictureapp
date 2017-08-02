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
     * Reference to the associated questionRelation (loaded lazily)
     */
    QuestionRelation questionRelation;

    /**
     * List of questionOptions associated to this mMatchDB
     */
    List<QuestionOption> questionOptions;

    public MatchDB() {
    }

    public MatchDB(QuestionRelation questionRelation) {
        setQuestionRelation(questionRelation);
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

    public List<QuestionOption> getQuestionOptions() {
        if (questionOptions == null) {
            this.questionOptions = new Select().from(QuestionOption.class)
                    .where(QuestionOption_Table.id_match_fk.eq(this.getId_match()))
                    .queryList();
        }
        return this.questionOptions;
    }

    /**
     * Get all questionThresholds mMatchDBs with the mMatchDB passed.
     */
    private static List<QuestionThreshold> getQuestionThreshold(MatchDB matchDB) {
        return new Select().from(QuestionThreshold.class)
                .where(QuestionThreshold_Table.id_match_fk.eq(
                        matchDB.getId_match())).queryList();
    }

    /**
     * Get all questionOptions mMatchDBs with the mMatchDB passed.
     */
    private static List<QuestionOption> getQuestionOptions(MatchDB matchDB) {
        return new Select().from(QuestionOption.class)
                .where(QuestionOption_Table.id_match_fk.eq(
                        matchDB.getId_match())).queryList();
    }

    /**
     * Get all treatment mMatchDBs with the mMatchDB passed.
     */
    private static List<TreatmentMatch> getTreatmentMatches(MatchDB matchDB) {
        return new Select().from(TreatmentMatch.class)
                .where(TreatmentMatch_Table.id_match_fk.eq(
                        matchDB.getId_match())).queryList();
    }

    public Treatment getTreatment() {
        return new Select().from(Treatment.class).as(treatmentName)
                .join(TreatmentMatch.class, Join.JoinType.LEFT_OUTER).as(treatmentMatchName)
                .on(Treatment_Table.id_treatment.withTable(treatmentAlias)
                        .eq(TreatmentMatch_Table.id_treatment_fk.withTable(treatmentMatchAlias)))
                .where(TreatmentMatch_Table.id_match_fk.withTable(treatmentMatchAlias)
                        .eq(id_match))
                .querySingle();
    }

    /**
     * Method to delete the mMatchDBs in cascade
     */
    public static void deleteMatches(List<MatchDB> matchDBs) {
        for (MatchDB matchDB : matchDBs) {
            TreatmentMatch.deleteTreatmentMatches(getTreatmentMatches(matchDB));
            QuestionOption.deleteQuestionOptions(getQuestionOptions(matchDB));
            QuestionThreshold.deleteQuestionThresholds(getQuestionThreshold(matchDB));
            matchDB.delete();
        }
    }

    public long getId_match() {
        return id_match;
    }

    public void setId_match(long id_match) {
        this.id_match = id_match;
    }

    public QuestionRelation getQuestionRelation() {
        if (questionRelation == null) {
            if (id_question_relation_fk == null) return null;
            questionRelation = new Select()
                    .from(QuestionRelation.class)
                    .where(QuestionRelation_Table.id_question_relation
                            .is(id_question_relation_fk)).querySingle();
        }
        return questionRelation;
    }

    public void setQuestionRelation(QuestionRelation questionRelation) {
        this.questionRelation = questionRelation;
        this.id_question_relation_fk =
                (questionRelation != null) ? questionRelation.getId_question_relation() : null;
    }

    public void setQuestionRelation(Long id_question_relation) {
        this.id_question_relation_fk = id_question_relation;
        this.questionRelation = null;
    }

    /**
     * Returns the threshold associated with this questionoption
     */
    public QuestionThreshold getQuestionThreshold() {
        //Find threshold with this mMatchDB
        return new Select().from(QuestionThreshold.class)
                .where(QuestionThreshold_Table.id_match_fk
                        .is(id_match)).querySingle();
    }

    /**
     * Returns the question from QuestionRelation for this mMatchDB with the given operationType
     */
    public Question getQuestionFromRelationWithType(int operationType) {
        QuestionRelation questionRelation = this.getQuestionRelation();
        if (questionRelation == null || questionRelation.getOperation() != operationType) {
            return null;
        }

        return questionRelation.getQuestion();
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
