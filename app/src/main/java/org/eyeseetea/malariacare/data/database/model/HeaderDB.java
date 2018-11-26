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

import java.util.List;

@Table(database = AppDatabase.class, name="Header")
public class HeaderDB extends BaseModel {

    /**
     * Required for creating the dynamic mTreatmentDB mQuestionDB in SCMM
     */
    public final static Long DYNAMIC_TREATMENT_HEADER_ID = 7l;

    @Column
    @PrimaryKey(autoincrement = true)
    long id_header;
    @Column
    String short_name;
    @Column
    String name;
    @Column
    Integer order_pos;
    @Column
    Long id_tab_fk;

    /**
     * Reference to parent mTabDB (loaded lazily)
     */
    TabDB mTabDB;

    /**
     * List of mQuestionDBs that belongs to this mHeaderDB
     */
    List<QuestionDB> mQuestionDBs;

    public HeaderDB() {
    }

    public HeaderDB(String short_name, String name, Integer order_pos, Integer master, TabDB tabDB) {
        this.short_name = short_name;
        this.name = name;
        this.order_pos = order_pos;
        setTabDB(tabDB);
    }

    public static List<HeaderDB> getAllHeaders() {
        return new Select().from(HeaderDB.class).queryList();
    }

    public static void deleteAll() {
        for (HeaderDB headerDB : getAllHeaders()) {
            headerDB.delete();
        }
    }

    public static List<HeaderDB> findHeadersByTab(Long id_tab) {
        return new Select()
                .from(HeaderDB.class)
                .where(HeaderDB_Table.id_tab_fk.eq(id_tab))
                .queryList();
    }

    public static HeaderDB findFirstHeaderByTab(Long id_tab) {
        return new Select()
                .from(HeaderDB.class)
                .where(HeaderDB_Table.id_tab_fk.eq(id_tab))
                .querySingle();
    }
    public static HeaderDB findById(Long id) {
        return new Select()
        .from(HeaderDB.class)
        .where(HeaderDB_Table.id_header.eq(id)).querySingle();
    }

    /**
     * Method to delete headerDBs in cascade
     */
    public static void deleteHeaders(List<HeaderDB> headerDBs) {
        for (HeaderDB headerDB : headerDBs) {
            QuestionDB.deleteQuestions(headerDB.getQuestionDBs());
            headerDB.delete();
        }
    }


    public Long getId_header() {
        return id_header;
    }

    public void setId_header(Long id_header) {
        this.id_header = id_header;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder_pos() {
        return order_pos;
    }

    public void setOrder_pos(Integer order_pos) {
        this.order_pos = order_pos;
    }

    public TabDB getTabDB() {
        if(mTabDB ==null){
            if(id_tab_fk==null) return null;
            mTabDB = new Select()
                    .from(TabDB.class)
                    .where((TabDB_Table.id_tab)
                            .is(id_tab_fk)).querySingle();
        }
        return mTabDB;
    }

    public void setTabDB(TabDB tabDB) {
        this.mTabDB = tabDB;
        this.id_tab_fk = (tabDB != null) ? tabDB.getId_tab() : null;
    }

    public void setTab(Long id_tab) {
        this.id_tab_fk = id_tab;
        this.mTabDB = null;
    }

    public List<QuestionDB> getQuestionDBs() {
        if (this.mQuestionDBs == null){
            this.mQuestionDBs = new Select().from(QuestionDB.class)
                    .where(QuestionDB_Table.id_header_fk.eq(this.getId_header()))
                    .orderBy(OrderBy.fromProperty(QuestionDB_Table.order_pos)).queryList();
        }
        return mQuestionDBs;
    }

    /**
     * getNumber Of QuestionDB Parents HeaderDB
     */
    public long getNumberOfQuestionParents() {
        return SQLite.selectCountOf().from(QuestionDB.class)
                .where(QuestionDB_Table.id_header_fk.eq(getId_header()))
                .and(QuestionDB_Table.id_question_parent.isNull()).count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HeaderDB headerDB = (HeaderDB) o;

        if (id_header != headerDB.id_header) return false;
        if (short_name != null ? !short_name.equals(headerDB.short_name)
                : headerDB.short_name != null) {
            return false;
        }
        if (name != null ? !name.equals(headerDB.name) : headerDB.name != null) return false;
        if (order_pos != null ? !order_pos.equals(headerDB.order_pos) : headerDB.order_pos != null) {
            return false;
        }
        return !(id_tab_fk != null ? !id_tab_fk.equals(headerDB.id_tab_fk) : headerDB.id_tab_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_header ^ (id_header >>> 32));
        result = 31 * result + (short_name != null ? short_name.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (order_pos != null ? order_pos.hashCode() : 0);
        result = 31 * result + (id_tab_fk != null ? id_tab_fk.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HeaderDB{" +
                "id_header=" + id_header +
                ", help_text='" + short_name + '\'' +
                ", name='" + name + '\'' +
                ", order_pos=" + order_pos +
                ", id_tab_fk=" + id_tab_fk +
                '}';
    }


}
