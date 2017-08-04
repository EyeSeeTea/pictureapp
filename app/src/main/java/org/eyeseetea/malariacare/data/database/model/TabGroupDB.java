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

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class, name = "TabGroup")
public class TabGroupDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_tab_group;
    @Column
    String name;

    @Column
    Long id_program;

    /**
     * Reference to parent mProgramDB (loaded lazily)
     */
    ProgramDB mProgramDB;

    @Column
    String uid;

    /**
     * List of mTabDBs that belongs to this tabgroup
     */
    List<TabDB> mTabDBs;

    /**
     * List of surveys that belongs to this tabgroup
     */
    List<SurveyDB> mSurveyDBs;

    public TabGroupDB() {
    }

    public TabGroupDB(String name) {
        this.name = name;
    }

    public TabGroupDB(String name, ProgramDB programDB) {
        this.name = name;
        setProgram(programDB);
    }


    public void setId_tab_group(Long id_tab_group) {
        this.id_tab_group = id_tab_group;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setProgramDB(Long id_program) {
        this.id_program = id_program;
        this.mProgramDB = null;
    }


    public void setProgram(ProgramDB programDB) {
        this.mProgramDB = programDB;
        this.id_program = (programDB != null) ? programDB.getId_program() : null;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TabGroupDB tabGroupDB = (TabGroupDB) o;

        if (id_tab_group != tabGroupDB.id_tab_group) return false;
        if (name != null ? !name.equals(tabGroupDB.name) : tabGroupDB.name != null) return false;
        if (id_program != null ? !id_program.equals(tabGroupDB.id_program)
                : tabGroupDB.id_program != null) {
            return false;
        }
        return !(uid != null ? !uid.equals(tabGroupDB.uid) : tabGroupDB.uid != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_tab_group ^ (id_tab_group >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (id_program != null ? id_program.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TabGroupDB{" +
                "id_tab_group=" + id_tab_group +
                ", name='" + name + '\'' +
                ", id_program_fk=" + id_program +
                ", uid='" + uid + '\'' +
                '}';
    }
}
