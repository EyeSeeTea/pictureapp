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

@Table(database = AppDatabase.class)
public class Tab extends BaseModel {

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
     * Reference to parent program (loaded lazily)
     */
    Program program;

    /**
     * List of headers that belongs to this tab
     */
    List<Header> headers;

    public Tab() {
    }

    public Tab(String name, Integer order_pos, Integer type, Program program) {
        this.name = name;
        this.order_pos = order_pos;
        this.type = type;
        setProgram(program);
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

    public Program getProgram() {
        if (program == null) {
            if (id_program_fk == null) return null;

            program = new Select()
                    .from(Program.class)
                    .where((Program_Table.id_program)
                            .is(id_program_fk)).querySingle();
        }
        return program;
    }

    public void setProgram(Long id_program) {
        this.id_program_fk = id_program;
        this.program = null;
    }

    public void setProgram(Program program) {
        this.program = program;
        this.id_program_fk = (program != null) ? program.getId_program() : null;
    }

    public static List<Tab> getAllTabs() {
        return new Select().from(Tab.class).queryList();
    }

    /*
     * Return tabs filter by program and order by orderpos field
     */
    public static List<Tab> getTabsBySession() {
        return new Select().from(Tab.class)
                .where(Tab_Table.id_program_fk.eq(
                        Session.getMalariaSurvey().getProgram().getId_program()))
                .orderBy(OrderBy.fromProperty(Tab_Table.order_pos))
                .queryList();
    }
    /**
     * Method to delete tabs in cascade.
     *
     * @param names The list of names to delete.
     */
    public static void deleteTab(List<String> names) {
        List<Tab> tabs = getAllTabs();
        for (Tab tab : tabs) {
            for (String name : names) {
                if (tab.getName().equals(name)) {
                    Header.deleteHeaders(tab.getHeaders());
                    tab.delete();
                    break;
                }
            }
        }
    }

    public List<Header> getHeaders() {
        if (headers == null) {
            headers = new Select().from(Header.class)
                    .where(Header_Table.id_tab_fk.eq(this.getId_tab()))
                    .orderBy(OrderBy.fromProperty(Header_Table.order_pos))
                    .queryList();
        }
        return headers;
    }

    /**
     * Checks if TAB table is empty or has no data
     */
    public static boolean isEmpty() {
        return SQLite.selectCountOf().from(Tab.class).count() == 0;
    }

    /**
     * Checks if this tab is a general score tab.
     */
    public boolean isGeneralScore() {
        return getType() == Constants.TAB_SCORE_SUMMARY && !isCompositeScore();
    }

    /**
     * Checks if this tab is the composite score tab
     */
    public boolean isCompositeScore() {
        return getType().equals(Constants.TAB_COMPOSITE_SCORE);
    }

    /**
     * Checks if this tab is a custom tab
     */
    public boolean isACustomTab() {
        return getType().equals(Constants.TAB_ADHERENCE) || getType().equals(Constants.TAB_IQATAB)
                ||
                getType().equals(Constants.TAB_REPORTING);
    }

    /**
     * Checks if this tab is the adherence tab
     */
    public boolean isAdherenceTab() {
        return getType() == Constants.TAB_ADHERENCE;
    }

    /**
     * Checks if this tab is the IQA tab
     */
    public boolean isIQATab() {
        return getType() == Constants.TAB_IQATAB;
    }

    /**
     * Checks if this tab is a dynamic tab (sort of a wizard)
     */
    public boolean isDynamicTab() {
        return getType() == Constants.TAB_DYNAMIC_AUTOMATIC_TAB;
    }

    /**
     * Checks if this tab is a dynamic tab (sort of a wizard)
     */
    public boolean isMultiQuestionTab() {
        return isMultiQuestionTab(getType());
    }

    public static boolean isMultiQuestionTab(int tabType) {
        return tabType == Constants.TAB_MULTI_QUESTION;
    }

    public static Tab findById(Long id) {
        return new Select()
                .from(Tab.class)
                .where(Tab_Table.id_tab.eq(id)).querySingle();
    }

    public static Tab getFirstTab() {
        return new Select().from(Tab.class).querySingle();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tab tab = (Tab) o;

        if (id_tab != tab.id_tab) return false;
        if (name != null ? !name.equals(tab.name) : tab.name != null) return false;
        if (order_pos != null ? !order_pos.equals(tab.order_pos) : tab.order_pos != null) {
            return false;
        }
        if (type != null ? !type.equals(tab.type) : tab.type != null) return false;
        return !(id_program_fk != null ? !id_program_fk.equals(tab.id_program_fk)
                : tab.id_program_fk != null);

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
        return "Tab{" +
                "id_tab=" + id_tab +
                ", name='" + name + '\'' +
                ", order_pos=" + order_pos +
                ", type=" + type +
                ", id_program_fk=" + id_program_fk +
                '}';
    }
}
