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
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;

import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class, name = "OrgUnit")
public class OrgUnitDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_org_unit;
    @Column
    String uid_org_unit;
    @Column
    String name;
    @Column
    Long id_org_unit_parent;
    @Column
    Boolean is_banned;

    /**
     * Reference to parent mOrgUnitDB (loaded lazily)
     */
    OrgUnitDB mOrgUnitDB;

    @Column
    Long id_org_unit_level;

    /**
     * Reference to the level of this mOrgUnitDB (loaded lazily)
     */
    OrgUnitLevelDB mOrgUnitLevelDB;

    /**
     * List of surveys that belong to this orgunit
     */
    List<SurveyDB> mSurveyDBs;

    /**
     * List of mOrgUnitDBs that belong to this one
     */
    List<OrgUnitDB> children;

    /**
     * List of mProgramDB authorized for this orgunit
     */
    List<ProgramDB> mProgramDBs;

    public OrgUnitDB() {
    }

    public OrgUnitDB(String name) {
        this.name = name;
    }


    public OrgUnitDB(String uid, String name, OrgUnitDB orgUnitDB, OrgUnitLevelDB orgUnitLevelDB) {
        this.uid_org_unit = uid;
        this.name = name;
        this.setOrgUnitDB(orgUnitDB);
        this.setOrgUnitLevelDB(orgUnitLevelDB);
    }

    /**
     * Returns all the orgunits from the db
     */
    public static List<OrgUnitDB> getAllOrgUnit() {
        return new Select().from(OrgUnitDB.class)
                .orderBy(OrderBy.fromProperty(OrgUnitDB_Table.name)).queryList();
    }

    /**
     * Returns the list of org units from the database
     */
    public static String[] listAllNames() {
        List<OrgUnitDB> orgUnitDBs = getAllOrgUnit();
        List<String> orgUnitsName = new ArrayList<String>();
        //String[] orgUnitNames = new String[mOrgUnitDBs.size()];
        for (int i = 0; i < orgUnitDBs.size(); i++) {
            if (orgUnitDBs.get(i).getName() != null && !orgUnitDBs.get(i).getName().equals("")) {
                orgUnitsName.add(orgUnitDBs.get(i).getName());
            }
        }
        return orgUnitsName.toArray(new String[orgUnitsName.size()]);
    }

    /**
     * Returns the UID of an mOrgUnitDB with the given name
     *
     * @param name Name of the orgunit
     */
    public static String findUIDByName(String name) {
        OrgUnitDB orgUnitDB = new Select().from(OrgUnitDB.class)
                .where(OrgUnitDB_Table.name.eq(name)).querySingle();
        if (orgUnitDB == null) {
            return null;
        }
        return orgUnitDB.getUid();
    }

    /**
     * Returns the Orgunit of an mOrgUnitDB with the given name
     *
     * @param name Name of the orgunit
     */
    public static OrgUnitDB findByName(String name) {
        return new Select().from(OrgUnitDB.class)
                .where(OrgUnitDB_Table.name.eq(name)).querySingle();
    }
    public Long getId_org_unit() {
        return id_org_unit;
    }

    public void setId_org_unit(Long id_org_unit) {
        this.id_org_unit = id_org_unit;
    }

    public String getUid() {
        return uid_org_unit;
    }

    public void setUid(String uid) {
        this.uid_org_unit = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OrgUnitDB getOrgUnitDB() {
        if (mOrgUnitDB == null) {
            if (this.id_org_unit_parent == null) return null;
            mOrgUnitDB = new Select()
                    .from(OrgUnitDB.class)
                    .where(OrgUnitDB_Table.id_org_unit
                            .is(id_org_unit_parent)).querySingle();
        }
        return mOrgUnitDB;
    }

    public void setOrgUnitDB(OrgUnitDB orgUnitDB) {
        this.mOrgUnitDB = orgUnitDB;
        this.id_org_unit_parent = (orgUnitDB != null) ? orgUnitDB.getId_org_unit() : null;
    }

    public void setOrgUnit(Long id_parent) {
        this.id_org_unit_parent = id_parent;
        this.mOrgUnitDB = null;
    }

    public OrgUnitLevelDB getOrgUnitLevelDB() {
        if (mOrgUnitLevelDB == null) {
            if (this.id_org_unit_level == null) return null;
            mOrgUnitLevelDB = new Select()
                    .from(OrgUnitLevelDB.class)
                    .where(OrgUnitLevelDB_Table.id_org_unit_level
                            .is(id_org_unit_level)).querySingle();
        }
        return mOrgUnitLevelDB;
    }

    public void setOrgUnitLevelDB(OrgUnitLevelDB orgUnitLevelDB) {
        this.mOrgUnitLevelDB = orgUnitLevelDB;
        this.id_org_unit_level =
                (orgUnitLevelDB != null) ? orgUnitLevelDB.getId_org_unit_level() : null;
    }

    public void setOrgUnitLevel(Long id_org_unit_level) {
        this.id_org_unit_level = id_org_unit_level;
        this.mOrgUnitLevelDB = null;
    }

    public boolean isBanned() {
        if (is_banned == null) {
            return false;
        }
        return is_banned;
    }

    public void setBan(boolean isBanned) {
        this.is_banned = isBanned;
    }

    public List<OrgUnitDB> getChildren() {
        if (this.children == null) {
            this.children = new Select().from(OrgUnitDB.class)
                    .where(OrgUnitDB_Table.id_org_unit_parent.eq(
                            this.getId_org_unit())).queryList();
        }
        return children;
    }

    public List<SurveyDB> getSurveyDBs() {
        if (this.mSurveyDBs == null) {
            this.mSurveyDBs = new Select().from(SurveyDB.class)
                    .where(SurveyDB_Table.id_org_unit_fk.eq(
                            this.getId_org_unit())).queryList();
        }
        return mSurveyDBs;
    }

    public List<ProgramDB> getProgramDBs() {
        if (mProgramDBs == null) {
            List<OrgUnitProgramRelationDB> orgUnitProgramRelationDBs = new Select().from(
                    OrgUnitProgramRelationDB.class)
                    .where(OrgUnitProgramRelationDB_Table.id_org_unit_fk.eq(
                            this.getId_org_unit()))
                    .queryList();
            this.mProgramDBs = new ArrayList<>();
            for (OrgUnitProgramRelationDB programRelation : orgUnitProgramRelationDBs) {
                mProgramDBs.add(programRelation.getProgramDB());
            }
        }
        return mProgramDBs;
    }

    public void addProgram(ProgramDB programDB) {
        //Null -> nothing
        if (programDB == null) {
            return;
        }

        //Save a new relationship
        OrgUnitProgramRelationDB orgUnitProgramRelationDB = new OrgUnitProgramRelationDB(this,
                programDB);
        orgUnitProgramRelationDB.save();

        //Clear cache to enable reloading
        mProgramDBs = null;
    }

    public static void refresh(OrganisationUnit organisationUnit) {
        OrgUnitDB orgUnitDB = findByUID(organisationUnit.getUid());

        orgUnitDB.setBan(organisationUnit.isBanned());
        orgUnitDB.setName(organisationUnit.getName());

        orgUnitDB.save();
    }

    public static OrganisationUnit getByName(String name) {
        OrgUnitDB orgUnitDB = findByName(name);

        return new OrganisationUnit(orgUnitDB.getUid(), orgUnitDB.getName(), orgUnitDB.isBanned());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgUnitDB orgUnitDB = (OrgUnitDB) o;

        if (id_org_unit != orgUnitDB.id_org_unit) return false;
        if (is_banned != orgUnitDB.is_banned) return false;
        if (uid_org_unit != null ? !uid_org_unit.equals(orgUnitDB.uid_org_unit) : orgUnitDB.uid_org_unit != null) return false;
        if (name != null ? !name.equals(orgUnitDB.name) : orgUnitDB.name != null) return false;
        if (id_org_unit_parent != null ? !id_org_unit_parent.equals(
                orgUnitDB.id_org_unit_parent) : orgUnitDB.id_org_unit_parent != null) {
            return false;
        }
        return !(id_org_unit_level != null ? !id_org_unit_level.equals(orgUnitDB.id_org_unit_level)
                : orgUnitDB.id_org_unit_level != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_org_unit ^ (id_org_unit >>> 32));
        result = 31 * result + (uid_org_unit != null ? uid_org_unit.hashCode() : 0);
        result = 31 * result + (is_banned != null ? is_banned.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (id_org_unit_parent != null ? id_org_unit_parent.hashCode() : 0);
        result = 31 * result + (id_org_unit_level != null ? id_org_unit_level.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrgUnitDB{" +
                "id_org_unit_fk=" + id_org_unit +
                ", uid_org_unit='" + uid_org_unit + '\'' +
                ", is_banned='" + is_banned + '\'' +
                ", name='" + name + '\'' +
                ", id_org_unit_parent=" + id_org_unit_parent +
                ", id_org_unit_level=" + id_org_unit_level +
                '}';
    }

    public static OrgUnitDB findByUID(String UID) {
        return new Select()
                .from(OrgUnitDB.class)
                .where(OrgUnitDB_Table.uid_org_unit
                        .is(UID)).querySingle();
    }
    public static boolean hasOrgUnits(){
        return (SQLite.selectCountOf().from(OrgUnitDB.class).count() == 0);
    }

}
