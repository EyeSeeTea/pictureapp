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
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

@Table(database = AppDatabase.class, name = "Tab")
public class TabDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_tab;
    @Column
    String name;
    @Column
    Integer order_pos;
    @Column
    Integer type;
    @Column
    Long id_program_fk;

    /**
     * Reference to parent mProgramDB (loaded lazily)
     */
    ProgramDB mProgramDB;

    /**
     * List of mHeaderDBs that belongs to this mTabDB
     */
    List<HeaderDB> mHeaderDBs;

    public TabDB() {
    }

    public TabDB(String name, Integer order_pos, Integer type, ProgramDB programDB) {
        this.name = name;
        this.order_pos = order_pos;
        this.type = type;
        setProgram(programDB);
    }

    public Long getId_tab() {
        return id_tab;
    }

    public void setId_tab(Long id_tab) {
        this.id_tab = id_tab;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternationalizedName() {
        return Utils.getInternationalizedString(name);
    }

    public Integer getOrder_pos() {
        return order_pos;
    }

    public void setOrder_pos(Integer order_pos) {
        this.order_pos = order_pos;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public ProgramDB getProgramDB() {
        if (mProgramDB == null) {
            if (id_program_fk == null) return null;

            mProgramDB = new Select()
                    .from(ProgramDB.class)
                    .where((ProgramDB_Table.id_program)
                            .is(id_program_fk)).querySingle();
        }
        return mProgramDB;
    }

    public void setProgramDB(Long id_program) {
        this.id_program_fk = id_program;
        this.mProgramDB = null;
    }

    public void setProgram(ProgramDB programDB) {
        this.mProgramDB = programDB;
        this.id_program_fk = (programDB != null) ? programDB.getId_program() : null;
    }

    public static List<TabDB> getAllTabs() {
        return new Select().from(TabDB.class).queryList();
    }

    /*
     * Return mTabDBs filter by mProgramDB and order by orderpos field
     */
    public static List<TabDB> getTabsBySession() {
        return new Select().from(TabDB.class)
                .where(TabDB_Table.id_program_fk.eq(
                        Session.getMalariaSurveyDB().getProgramDB().getId_program()))
                .orderBy(OrderBy.fromProperty(TabDB_Table.order_pos))
                .queryList();
    }
    /**
     * Method to delete mTabDBs in cascade.
     *
     * @param names The list of names to delete.
     */
    public static void deleteTab(List<String> names) {
        List<TabDB> tabDBs = getAllTabs();
        for (TabDB tabDB : tabDBs) {
            for (String name : names) {
                if (tabDB.getName().equals(name)) {
                    HeaderDB.deleteHeaders(tabDB.getHeaderDBs());
                    tabDB.delete();
                    break;
                }
            }
        }
    }

    public List<HeaderDB> getHeaderDBs() {
        if (mHeaderDBs == null) {
            mHeaderDBs = new Select().from(HeaderDB.class)
                    .where(HeaderDB_Table.id_tab_fk.eq(this.getId_tab()))
                    .orderBy(OrderBy.fromProperty(HeaderDB_Table.order_pos))
                    .queryList();
        }
        return mHeaderDBs;
    }

    /**
     * Checks if TAB table is empty or has no data
     */
    public static boolean isEmpty() {
        return SQLite.selectCountOf().from(TabDB.class).count() == 0;
    }

    /**
     * Checks if this mTabDB is a general score mTabDB.
     */
    public boolean isGeneralScore() {
        return getType() == Constants.TAB_SCORE_SUMMARY && !isCompositeScore();
    }

    /**
     * Checks if this mTabDB is the composite score mTabDB
     */
    public boolean isCompositeScore() {
        return getType().equals(Constants.TAB_COMPOSITE_SCORE);
    }

    /**
     * Checks if this mTabDB is a custom mTabDB
     */
    public boolean isACustomTab() {
        return getType().equals(Constants.TAB_ADHERENCE) || getType().equals(Constants.TAB_IQATAB)
                ||
                getType().equals(Constants.TAB_REPORTING);
    }

    /**
     * Checks if this mTabDB is the adherence mTabDB
     */
    public boolean isAdherenceTab() {
        return getType() == Constants.TAB_ADHERENCE;
    }

    /**
     * Checks if this mTabDB is the IQA mTabDB
     */
    public boolean isIQATab() {
        return getType() == Constants.TAB_IQATAB;
    }

    /**
     * Checks if this mTabDB is a dynamic mTabDB (sort of a wizard)
     */
    public boolean isDynamicTab() {
        return getType() == Constants.TAB_DYNAMIC_AUTOMATIC_TAB;
    }

    /**
     * Checks if this mTabDB is a dynamic mTabDB (sort of a wizard)
     */
    public boolean isMultiQuestionTab() {
        return isMultiQuestionTab(getType());
    }

    public static boolean isMultiQuestionTab(int tabType) {
        return tabType == Constants.TAB_MULTI_QUESTION;
    }

    public static TabDB findById(Long id) {
        return new Select()
                .from(TabDB.class)
                .where(TabDB_Table.id_tab.eq(id)).querySingle();
    }

    public static TabDB getFirstTab() {
        return new Select().from(TabDB.class).querySingle();
    }

    public static TabDB getFirstTabWithProgram(Long programId) {
        return new Select()
                .from(TabDB.class)
                .where(TabDB_Table.id_program_fk.is(programId)).querySingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TabDB tabDB = (TabDB) o;

        if (id_tab != tabDB.id_tab) return false;
        if (name != null ? !name.equals(tabDB.name) : tabDB.name != null) return false;
        if (order_pos != null ? !order_pos.equals(tabDB.order_pos) : tabDB.order_pos != null) {
            return false;
        }
        if (type != null ? !type.equals(tabDB.type) : tabDB.type != null) return false;
        return !(id_program_fk != null ? !id_program_fk.equals(tabDB.id_program_fk)
                : tabDB.id_program_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_tab ^ (id_tab >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (order_pos != null ? order_pos.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (id_program_fk != null ? id_program_fk.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TabDB{" +
                "id_tab=" + id_tab +
                ", name='" + name + '\'' +
                ", order_pos=" + order_pos +
                ", type=" + type +
                ", id_program_fk=" + id_program_fk +
                '}';
    }
}
