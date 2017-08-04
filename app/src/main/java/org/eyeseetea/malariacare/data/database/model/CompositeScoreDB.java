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


import static org.eyeseetea.malariacare.data.database.AppDatabase.compositeScoreAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.compositeScoreName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.compositeScoreTwoAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.compositeScoreTwoName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.headerAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.headerName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.tabAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.tabName;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(database = AppDatabase.class, name = "CompositeScore")
public class CompositeScoreDB extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_composite_score;
    @Column
    String hierarchical_code;
    @Column
    String label;
    @Column
    String uid_composite_score;
    @Column
    Integer order_pos;
    @Column
    Long id_composite_score_parent;

    /**
     * Reference to parent mCompositeScoreDB (loaded lazily)
     */
    CompositeScoreDB mCompositeScoreDB;

    /**
     * List of compositeScores that belongs to this one
     */
    List<CompositeScoreDB> mCompositeScoreDBChildren;

    /**
     * List of mQuestionDBs associated to this mCompositeScoreDB
     */
    List<QuestionDB> mQuestionDBs;

    public CompositeScoreDB() {
    }

    public CompositeScoreDB(String hierarchical_code, String label,
            CompositeScoreDB compositeScoreDB,
            Integer order_pos) {
        this.hierarchical_code = hierarchical_code;
        this.label = label;
        this.order_pos = order_pos;
        this.setCompositeScoreDB(compositeScoreDB);
    }

    public CompositeScoreDB(String hierarchical_code, String label, String uid,
            CompositeScoreDB compositeScoreDB, Integer order_pos) {
        this.hierarchical_code = hierarchical_code;
        this.label = label;
        this.uid_composite_score = uid;
        this.order_pos = order_pos;
        this.setCompositeScoreDB(compositeScoreDB);
    }

    /**
     * Select all composite score that belongs to a mProgramDB
     *
     * @param programDB ProgramDB whose composite scores are searched.
     */
    public static List<CompositeScoreDB> listByProgram(ProgramDB programDB) {
        if (programDB == null || programDB.getId_program() == null) {
            return new ArrayList<>();
        }

        //FIXME: Apparently there is a bug in DBFlow joins that affects here. QuestionDB has a
        // column 'uid', and so do CompositeScoreDB, so results are having Questions one, and
        // should keep CompositeScoreDB one. To solve it, we've introduced a last join with
        // CompositeScoreDB again and a HashSet to remove resulting duplicates
        //Take scores associated to mQuestionDBs of the mProgramDB ('leaves')

        List<CompositeScoreDB> compositeScoresByProgramDB = new Select().distinct().from(
                CompositeScoreDB.class).as(compositeScoreName)
                .join(QuestionDB.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(CompositeScoreDB_Table.id_composite_score.withTable(compositeScoreAlias)
                        .eq(QuestionDB_Table.id_composite_score_fk.withTable(questionAlias)))
                .join(HeaderDB.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(QuestionDB_Table.id_header_fk.withTable(questionAlias)
                        .eq(HeaderDB_Table.id_header.withTable(headerAlias)))
                .join(TabDB.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(HeaderDB_Table.id_tab_fk.withTable(headerAlias)
                        .eq(TabDB_Table.id_tab.withTable(tabAlias)))
                .join(ProgramDB.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(TabDB_Table.id_program_fk.withTable(tabAlias)
                        .eq(ProgramDB_Table.id_program.withTable(programAlias)))
                .join(CompositeScoreDB.class, Join.JoinType.LEFT_OUTER).as(compositeScoreTwoName)
                .on(CompositeScoreDB_Table.id_composite_score.withTable(compositeScoreAlias)
                        .eq(CompositeScoreDB_Table.id_composite_score.withTable(
                                compositeScoreTwoAlias)))
                .where(ProgramDB_Table.id_program.withTable(programAlias)
                        .eq(programDB.getId_program()))
                .orderBy(CompositeScoreDB_Table.order_pos, true)
                .queryList();

        // remove duplicates
        Set<CompositeScoreDB> uniqueCompositeScoresByProgramDB = new HashSet<>();
        uniqueCompositeScoresByProgramDB.addAll(compositeScoresByProgramDB);
        compositeScoresByProgramDB.clear();
        compositeScoresByProgramDB.addAll(uniqueCompositeScoresByProgramDB);

        //Find parent scores from 'leaves'
        Set<CompositeScoreDB> parentCompositeScoreDBs = new HashSet<>();
        for (CompositeScoreDB compositeScoreDB : compositeScoresByProgramDB) {
            parentCompositeScoreDBs.addAll(listParentCompositeScores(compositeScoreDB));
        }
        compositeScoresByProgramDB.addAll(parentCompositeScoreDBs);


        Collections.sort(compositeScoresByProgramDB, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {

                CompositeScoreDB cs1 = (CompositeScoreDB) o1;
                CompositeScoreDB cs2 = (CompositeScoreDB) o2;

                return new Integer(cs1.getOrder_pos().compareTo(new Integer(cs2.getOrder_pos())));
            }
        });


        //return all scores
        return compositeScoresByProgramDB;
    }

    public static List<CompositeScoreDB> listParentCompositeScores(
            CompositeScoreDB compositeScoreDB) {
        List<CompositeScoreDB> parentScores = new ArrayList<>();
        if (compositeScoreDB == null || !compositeScoreDB.hasParent()) {
            return parentScores;
        }
        CompositeScoreDB currentScore = compositeScoreDB;
        while (currentScore != null && currentScore.hasParent()) {
            currentScore = currentScore.getComposite_score();
            parentScores.add(currentScore);
        }
        return parentScores;
    }

    public Long getId_composite_score() {
        return id_composite_score;
    }

    public void setId_composite_score(Long id_composite_score) {
        this.id_composite_score = id_composite_score;
    }

    public String getHierarchical_code() {
        return hierarchical_code;
    }

    public void setHierarchical_code(String hierarchical_code) {
        this.hierarchical_code = hierarchical_code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CompositeScoreDB getComposite_score() {
        if (mCompositeScoreDB == null) {
            if (id_composite_score_parent == null) return null;
            mCompositeScoreDB = new Select()
                    .from(CompositeScoreDB.class)
                    .where(CompositeScoreDB_Table.id_composite_score
                            .is(id_composite_score_parent)).querySingle();
        }
        return mCompositeScoreDB;
    }

    public void setCompositeScoreDB(CompositeScoreDB compositeScoreDB) {
        this.mCompositeScoreDB = compositeScoreDB;
        this.id_composite_score_parent =
                (compositeScoreDB != null) ? compositeScoreDB.getId_composite_score() : null;
    }

    public void setCompositeScore(Long id_parent) {
        this.id_composite_score_parent = id_parent;
        this.mCompositeScoreDB = null;
    }

    public String getUid() {
        return uid_composite_score;
    }

    public void setUid(String uid) {
        this.uid_composite_score = uid;
    }

    public Integer getOrder_pos() {
        return order_pos;
    }

    public void setOrder_pos(Integer order_pos) {
        this.order_pos = order_pos;
    }

    public boolean hasParent() {
        return getComposite_score() != null;
    }

    public List<CompositeScoreDB> getCompositeScoreDBChildren() {
        if (this.mCompositeScoreDBChildren == null) {
            this.mCompositeScoreDBChildren = new Select()
                    .from(CompositeScoreDB.class)
                    .where(CompositeScoreDB_Table.id_composite_score_parent.eq(
                            this.getId_composite_score()))
                    .orderBy(OrderBy.fromProperty(CompositeScoreDB_Table.order_pos))
                    .queryList();
        }
        return this.mCompositeScoreDBChildren;
    }

    public List<QuestionDB> getQuestionDBs() {
        if (mQuestionDBs == null) {
            mQuestionDBs = new Select()
                    .from(QuestionDB.class)
                    .where(QuestionDB_Table.id_composite_score_fk.eq(this.getId_composite_score()))
                    .orderBy(QuestionDB_Table.order_pos, true)
                    .queryList();
        }
        return mQuestionDBs;
    }

    public boolean hasChildren() {
        return !getCompositeScoreDBChildren().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeScoreDB that = (CompositeScoreDB) o;

        if (id_composite_score != that.id_composite_score) return false;
        if (hierarchical_code != null ? !hierarchical_code.equals(that.hierarchical_code)
                : that.hierarchical_code != null) {
            return false;
        }
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (uid_composite_score != null ? !uid_composite_score.equals(that.uid_composite_score) : that.uid_composite_score != null) return false;
        if (order_pos != null ? !order_pos.equals(that.order_pos) : that.order_pos != null) {
            return false;
        }
        return !(id_composite_score_parent != null ? !id_composite_score_parent.equals(that.id_composite_score_parent) : that.id_composite_score_parent != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_composite_score ^ (id_composite_score >>> 32));
        result = 31 * result + (hierarchical_code != null ? hierarchical_code.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (uid_composite_score != null ? uid_composite_score.hashCode() : 0);
        result = 31 * result + (order_pos != null ? order_pos.hashCode() : 0);
        result = 31 * result + (id_composite_score_parent != null ? id_composite_score_parent.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompositeScoreDB{" +
                "id_composite_score=" + id_composite_score +
                ", hierarchical_code='" + hierarchical_code + '\'' +
                ", label='" + label + '\'' +
                ", uid_composite_score='" + uid_composite_score + '\'' +
                ", order_pos=" + order_pos +
                ", id_composite_score_parent=" + id_composite_score_parent +
                '}';
    }
}
