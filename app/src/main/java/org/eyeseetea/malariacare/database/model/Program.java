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

package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class)
public class Program extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_program;
    @Column
    String uid;
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
        this.uid = uid;
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
        Question qMax = SQLite.select(Method.max(Question_Table.total_questions),
                Question_Table.total_questions)
                .from(Question.class).as(AppDatabase.questionName)
                .join(Header.class, Join.JoinType.INNER).as(AppDatabase.headerName)
                .on(Question_Table.id_header.withTable(AppDatabase.questionAlias)
                        .eq(Header_Table.id_header.withTable(AppDatabase.headerAlias)))

                .join(Tab.class, Join.JoinType.INNER).as(AppDatabase.tabName)
                .on(Header_Table.id_tab.withTable(AppDatabase.tabAlias)
                        .eq(Tab_Table.id_tab.withTable(AppDatabase.tabAlias)))

                .join(Program.class, Join.JoinType.INNER).as(AppDatabase.programName)
                .on(Tab_Table.id_program.withTable(AppDatabase.tabAlias)
                        .eq(Program_Table.id_program.withTable(AppDatabase.programAlias)))

                .where(Program_Table.uid.withTable(AppDatabase.programAlias).eq(
                        p.getUid()))
                .querySingle();

        if (qMax != null && qMax.getTotalQuestions() != null) {
            maxTotalQuestions = qMax.getTotalQuestions();
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
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
                    .where(OrgUnitProgramRelation_Table.id_program.eq(
                            this.getId_program()))
                    .queryList();
            this.orgUnits = new ArrayList<>();
            for (OrgUnitProgramRelation programRelation : orgUnitProgramRelations) {
                orgUnits.add(programRelation.getOrgUnit());
            }
        }
        return orgUnits;
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
                    .where(Tab_Table.id_program.eq(this.getId_program()))
                    .orderBy(OrderBy.fromProperty(Tab_Table.order_pos).descending())
                    .queryList();
        }
        return tabs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Program)) return false;

        Program program = (Program) o;

        if (id_program != program.id_program) return false;
        if (uid != null ? !uid.equals(program.uid) : program.uid != null) return false;
        return name.equals(program.name);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_program ^ (id_program >>> 32));
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Program{" +
                "id=" + id_program +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
