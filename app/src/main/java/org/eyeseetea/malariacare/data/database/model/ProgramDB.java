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

import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class, name ="Program")
public class ProgramDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_program;
    @Column
    String uid_program;
    @Column
    String name;
    @Column
    String stage_uid;

    /**
     * List of mProgramDBs for this mProgramDB
     */
    List<ProgramDB> mProgramDBs;

    /**
     * List of mOrgUnitDB authorized for this mProgramDB
     */
    List<OrgUnitDB> mOrgUnitDBs;

    /**
     * List of mTabDBs that belongs to this mProgramDB
     */
    List<TabDB> mTabDBs;

    public ProgramDB() {
    }

    public ProgramDB(String name) {
        this.name = name;
    }

    public ProgramDB(String uid, String name) {
        this.uid_program = uid;
        this.name = name;
    }

    public static List<ProgramDB> getAllPrograms() {
        return new Select().from(ProgramDB.class).queryList();
    }

    public static ProgramDB getFirstProgram() {
        return new Select().from(ProgramDB.class).querySingle();
    }

    public static int getMaxTotalQuestions() {
        int maxTotalQuestions = 0;
        ProgramDB p = ProgramDB.getFirstProgram();
        if (p != null) {
            QuestionDB qMax = SQLite.select(Method.max(QuestionDB_Table.total_questions),
                    QuestionDB_Table.total_questions)
                    .from(QuestionDB.class).as(AppDatabase.questionName)
                    .join(HeaderDB.class, Join.JoinType.INNER).as(AppDatabase.headerName)
                    .on(QuestionDB_Table.id_header_fk.withTable(AppDatabase.questionAlias)
                            .eq(HeaderDB_Table.id_header.withTable(AppDatabase.headerAlias)))

                    .join(TabDB.class, Join.JoinType.INNER).as(AppDatabase.tabName)
                    .on(HeaderDB_Table.id_tab_fk.withTable(AppDatabase.headerAlias)
                            .eq(TabDB_Table.id_tab.withTable(AppDatabase.tabAlias)))

                    .join(ProgramDB.class, Join.JoinType.INNER).as(AppDatabase.programName)
                    .on(TabDB_Table.id_program_fk.withTable(AppDatabase.tabAlias)
                            .eq(ProgramDB_Table.id_program.withTable(AppDatabase.programAlias)))

                    .where(ProgramDB_Table.uid_program.withTable(AppDatabase.programAlias).eq(
                            p.getUid()))
                    .querySingle();

            if (qMax != null && qMax.getTotalQuestions() != null) {
                maxTotalQuestions = qMax.getTotalQuestions();
            }
        }
        return maxTotalQuestions;
    }

    public Long getId_program() {
        return id_program;
    }

    public void setId_program(Long id_program) {
        this.id_program = id_program;
    }

    public String getUid() {
        return uid_program;
    }

    public void setUid(String uid) {
        this.uid_program = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStageUid() {
        return stage_uid;
    }

    public void setStageUid(String stage_uid) {
        this.stage_uid = stage_uid;
    }

    public List<ProgramDB> getProgramDBs() {
        if (mProgramDBs == null) {
            this.mProgramDBs = new Select().from(ProgramDB.class)
                    .where(ProgramDB_Table.id_program.eq(this.getId_program()))
                    .queryList();
        }
        return this.mProgramDBs;
    }

    public List<OrgUnitDB> getOrgUnitDBs() {
        if (mOrgUnitDBs == null) {
            List<OrgUnitProgramRelationDB> orgUnitProgramRelationDBs = new Select().from(
                    OrgUnitProgramRelationDB.class)
                    .where(OrgUnitProgramRelationDB_Table.id_program_fk.eq(
                            this.getId_program()))
                    .queryList();
            this.mOrgUnitDBs = new ArrayList<>();
            for (OrgUnitProgramRelationDB programRelation : orgUnitProgramRelationDBs) {
                mOrgUnitDBs.add(programRelation.getOrgUnitDB());
            }
        }
        return mOrgUnitDBs;
    }

    private Context getContext() {
        return PreferencesState.getInstance().getContext();
    }

    public void addOrgUnit(OrgUnitDB orgUnitDB) {
        //Null -> nothing
        if (orgUnitDB == null) {
            return;
        }

        //Save a new relationship
        OrgUnitProgramRelationDB orgUnitProgramRelationDB = new OrgUnitProgramRelationDB(orgUnitDB, this);
        orgUnitProgramRelationDB.save();

        //Clear cache to enable reloading
        mOrgUnitDBs = null;
    }

    public List<TabDB> getTabDBs() {
        if (mTabDBs == null) {
            mTabDBs = new Select().from(TabDB.class)
                    .where(TabDB_Table.id_program_fk.eq(this.getId_program()))
                    .orderBy(TabDB_Table.order_pos, true)
                    .queryList();
        }
        return mTabDBs;
    }

    public static ProgramDB findById(Long id_program) {
        return new Select()
                .from(ProgramDB.class)
                .where(ProgramDB_Table.id_program
                        .is(id_program)).querySingle();
    }
    public static ProgramDB findByUID(String UID) {
        return new Select()
                .from(ProgramDB.class)
                .where(ProgramDB_Table.uid_program
                        .is(UID)).querySingle();
    }

    public static ProgramDB findByName(String name) {
        return new Select()
                .from(ProgramDB.class)
                .where(ProgramDB_Table.name
                        .is(name)).querySingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProgramDB)) return false;

        ProgramDB programDB = (ProgramDB) o;

        if (id_program != programDB.id_program) return false;
        if (uid_program != null ? !uid_program.equals(programDB.uid_program) : programDB.uid_program != null) return false;
        return name.equals(programDB.name);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_program ^ (id_program >>> 32));
        result = 31 * result + (uid_program != null ? uid_program.hashCode() : 0);
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ProgramDB{" +
                "id=" + id_program +
                ", uid_program='" + uid_program + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static ProgramDB getProgram(String uid) {
        return new Select().from(ProgramDB.class)
                .where(ProgramDB_Table.uid_program.eq(uid)).querySingle();
    }

    public static ProgramDB getProgram(long id) {
        return new Select().from(ProgramDB.class)
                .where(ProgramDB_Table.id_program.is(id)).querySingle();
    }
}
