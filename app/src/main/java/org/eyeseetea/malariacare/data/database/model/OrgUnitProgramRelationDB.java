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

/**
 * Created by ivan.arrizabalaga on 14/02/15.
 */
@Table(database = AppDatabase.class, name="OrgUnitProgramRelation")
public class OrgUnitProgramRelationDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_orgunit_program_relation;

    @Column
    Long id_org_unit_fk;

    /**
     * Reference to lazy mOrgUnitDB
     */
    OrgUnitDB mOrgUnitDB;

    @Column
    Long id_program_fk;

    /**
     * Reference to lazy mProgramDB
     */
    ProgramDB mProgramDB;

    public OrgUnitProgramRelationDB() {
    }

    public OrgUnitProgramRelationDB(OrgUnitDB orgUnitDB, ProgramDB programDB) {
        setOrgUnitDB(orgUnitDB);
        setProgramDB(programDB);
    }

    public OrgUnitDB getOrgUnitDB() {
        if (mOrgUnitDB == null) {
            if (id_org_unit_fk == null) return null;
            mOrgUnitDB = new Select()
                    .from(OrgUnitDB.class)
                    .where(OrgUnitDB_Table.id_org_unit
                            .is(id_org_unit_fk)).querySingle();
        }
        return mOrgUnitDB;
    }

    public void setOrgUnitDB(OrgUnitDB orgUnitDB) {
        this.mOrgUnitDB = orgUnitDB;
        this.id_org_unit_fk = (orgUnitDB != null) ? orgUnitDB.getId_org_unit() : null;
    }

    public void setOrgUnit(Long id_org_unit) {
        this.id_org_unit_fk = id_org_unit;
        this.mOrgUnitDB = null;
    }

    public ProgramDB getProgramDB() {
        if (mProgramDB == null) {
            if (id_program_fk == null) return null;
            mProgramDB = new Select()
                    .from(ProgramDB.class)
                    .where(ProgramDB_Table.id_program
                            .is(id_program_fk)).querySingle();
        }
        return mProgramDB;
    }

    public void setProgramDB(ProgramDB programDB) {
        this.mProgramDB = programDB;
        this.id_program_fk = (programDB != null) ? programDB.getId_program() : null;
    }

    public void setProgram(Long id_program) {
        this.id_program_fk = id_program;
        this.mProgramDB = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgUnitProgramRelationDB that = (OrgUnitProgramRelationDB) o;

        if (id_orgunit_program_relation != that.id_orgunit_program_relation) return false;
        if (id_org_unit_fk != null ? !id_org_unit_fk.equals(that.id_org_unit_fk)
                : that.id_org_unit_fk != null) {
            return false;
        }
        return !(id_program_fk != null ? !id_program_fk.equals(that.id_program_fk)
                : that.id_program_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_orgunit_program_relation ^ (id_orgunit_program_relation >>> 32));
        result = 31 * result + (id_org_unit_fk != null ? id_org_unit_fk.hashCode() : 0);
        result = 31 * result + (id_program_fk != null ? id_program_fk.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrgUnitProgramRelationDB{" +
                "id_orgunit_program_relation=" + id_orgunit_program_relation +
                ", id_org_unit_fk=" + id_org_unit_fk +
                ", id_program_fk=" + id_program_fk +
                '}';
    }
}

