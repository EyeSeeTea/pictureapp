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

import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = AppDatabase.NAME)
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
                    .where(Condition.column(Program$Table.ID_PROGRAM).eq(this.getId_program()))
                    .queryList();
        }
        return this.programs;
    }

    public List<OrgUnit> getOrgUnits() {
        if (orgUnits == null) {
            List<OrgUnitProgramRelation> orgUnitProgramRelations = new Select().from(
                    OrgUnitProgramRelation.class)
                    .where(Condition.column(OrgUnitProgramRelation$Table.ID_PROGRAM).eq(
                            this.getId_program()))
                    .queryList();
            this.orgUnits = new ArrayList<>();
            for (OrgUnitProgramRelation programRelation : orgUnitProgramRelations) {
                orgUnits.add(programRelation.getOrgUnit());
            }
        }
        return orgUnits;
    }

    public static List<Program> getAllPrograms() {
        return new Select().all().from(Program.class).queryList();
    }

    public static Program getFirstProgram() {
        return new Select().from(Program.class).querySingle();
    }

    public static int getMaxTotalQuestions() {

        int maxTotalQuestions = 0;
        Program p = Program.getFirstProgram();

        Question qMax = new Select(Question$Table.TOTAL_QUESTIONS).method("MAX",
                Question$Table.TOTAL_QUESTIONS)
                .from(Question.class).as("q")
                .join(Header.class, Join.JoinType.INNER).as("h")
                .on(Condition.column(ColumnAlias.columnWithTable("q", Question$Table.ID_HEADER))
                        .eq(ColumnAlias.columnWithTable("h", Header$Table.ID_HEADER)))
                .join(Tab.class, Join.JoinType.INNER).as("t")
                .on(Condition.column(ColumnAlias.columnWithTable("h", Header$Table.ID_TAB))
                        .eq(ColumnAlias.columnWithTable("t", Tab$Table.ID_TAB)))
                .join(Program.class, Join.JoinType.INNER).as("p")
                .on(Condition.column(ColumnAlias.columnWithTable("t", Tab$Table.ID_PROGRAM))
                        .eq(ColumnAlias.columnWithTable("p", Program$Table.ID_PROGRAM)))
                .where(Condition.column(ColumnAlias.columnWithTable("p", Program$Table.UID)).eq(
                        p.getUid()))
                .querySingle();

        if (qMax != null && qMax.getTotalQuestions() != null) {
            maxTotalQuestions = qMax.getTotalQuestions();
        }

        return maxTotalQuestions;
    }

    public List<Tab> getTabs() {
        if (tabs == null) {
            tabs = new Select().from(Tab.class)
                    .where(Condition.column(Tab$Table.ID_PROGRAM).eq(this.getId_program()))
                    .orderBy(Tab$Table.ORDER_POS).queryList();
        }
        return tabs;
    }

    private Context getContext(){
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

    public boolean isStockProgram() {
        return uid.equals(getContext().getString(R.string.stockProgramUID));
    }

    public static Program findById(Long id_program) {
        return new Select()
                .from(Program.class)
                .where(Condition.column(Program$Table.ID_PROGRAM)
                        .is(id_program)).querySingle();
    }

    public static Program findByUID(String UID) {
        return new Select()
                .from(Program.class)
                .where(Condition.column(Program$Table.UID)
                        .is(UID)).querySingle();
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
