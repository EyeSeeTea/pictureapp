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

@Table(database = AppDatabase.class)
public class Program extends BaseModel {

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
     * List of programs for this program
     */
    List<Program> programs;

    /**
     * List of orgUnit authorized for this program
     */
    List<OrgUnit> orgUnits;

    /**
     * List of tabs that belongs to this program
     */
    List<Tab> tabs;

    public Program() {
    }

    public Program(String name) {
        this.name = name;
    }

    public Program(String uid, String name) {
        this.uid_program = uid;
        this.name = name;
    }

    public static List<Program> getAllPrograms() {
        return new Select().from(Program.class).queryList();
    }

    public static Program getFirstProgram() {
        return new Select().from(Program.class).querySingle();
    }

    public static int getMaxTotalQuestions() {
        int maxTotalQuestions = 0;
        Program p = Program.getFirstProgram();
        if (p != null) {
            Question qMax = SQLite.select(Method.max(Question_Table.total_questions),
                    Question_Table.total_questions)
                    .from(Question.class).as(AppDatabase.questionName)
                    .join(Header.class, Join.JoinType.INNER).as(AppDatabase.headerName)
                    .on(Question_Table.id_header_fk.withTable(AppDatabase.questionAlias)
                            .eq(Header_Table.id_header.withTable(AppDatabase.headerAlias)))

                    .join(Tab.class, Join.JoinType.INNER).as(AppDatabase.tabName)
                    .on(Header_Table.id_tab_fk.withTable(AppDatabase.headerAlias)
                            .eq(Tab_Table.id_tab.withTable(AppDatabase.tabAlias)))

                    .join(Program.class, Join.JoinType.INNER).as(AppDatabase.programName)
                    .on(Tab_Table.id_program_fk.withTable(AppDatabase.tabAlias)
                            .eq(Program_Table.id_program.withTable(AppDatabase.programAlias)))

                    .where(Program_Table.uid_program.withTable(AppDatabase.programAlias).eq(
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

    public List<Program> getPrograms() {
        if (programs == null) {
            this.programs = new Select().from(Program.class)
                    .where(Program_Table.id_program.eq(this.getId_program()))
                    .queryList();
        }
        return this.programs;
    }

    public List<OrgUnit> getOrgUnits() {
        if (orgUnits == null) {
            List<OrgUnitProgramRelation> orgUnitProgramRelations = new Select().from(
                    OrgUnitProgramRelation.class)
                    .where(OrgUnitProgramRelation_Table.id_program_fk.eq(
                            this.getId_program()))
                    .queryList();
            this.orgUnits = new ArrayList<>();
            for (OrgUnitProgramRelation programRelation : orgUnitProgramRelations) {
                orgUnits.add(programRelation.getOrgUnit());
            }
        }
        return orgUnits;
    }

    private Context getContext() {
        return PreferencesState.getInstance().getContext();
    }

    public void addOrgUnit(OrgUnit orgUnit) {
        //Null -> nothing
        if (orgUnit == null) {
            return;
        }

        //Save a new relationship
        OrgUnitProgramRelation orgUnitProgramRelation = new OrgUnitProgramRelation(orgUnit, this);
        orgUnitProgramRelation.save();

        //Clear cache to enable reloading
        orgUnits = null;
    }

    public List<Tab> getTabs() {
        if (tabs == null) {
            tabs = new Select().from(Tab.class)
                    .where(Tab_Table.id_program_fk.eq(this.getId_program()))
                    .orderBy(Tab_Table.order_pos, true)
                    .queryList();
        }
        return tabs;
    }

    public static Program findById(Long id_program) {
        return new Select()
                .from(Program.class)
                .where(Program_Table.id_program
                        .is(id_program)).querySingle();
    }
    public static Program findByUID(String UID) {
        return new Select()
                .from(Program.class)
                .where(Program_Table.uid_program
                        .is(UID)).querySingle();
    }

    public static Program findByName(String name) {
        return new Select()
                .from(Program.class)
                .where(Program_Table.name
                        .is(name)).querySingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Program)) return false;

        Program program = (Program) o;

        if (id_program != program.id_program) return false;
        if (uid_program != null ? !uid_program.equals(program.uid_program) : program.uid_program != null) return false;
        return name.equals(program.name);

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
        return "Program{" +
                "id=" + id_program +
                ", uid_program='" + uid_program + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public static Program getProgram(String uid) {
        return new Select().from(Program.class)
                .where(Program_Table.uid_program.eq(uid)).querySingle();
    }

    public static Program getProgram(long id) {
        return new Select().from(Program.class)
                .where(Program_Table.id_program.is(id)).querySingle();
    }
}
