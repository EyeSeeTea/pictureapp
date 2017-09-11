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

@Table(database = AppDatabase.class)
public class Header extends BaseModel {

    /**
     * Required for creating the dynamic treatment question in SCMM
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
     * Reference to parent tab (loaded lazily)
     */
    Tab tab;

    /**
     * List of questions that belongs to this header
     */
    List<Question> questions;

    public Header() {
    }

    public Header(String short_name, String name, Integer order_pos, Integer master, Tab tab) {
        this.short_name = short_name;
        this.name = name;
        this.order_pos = order_pos;
        setTab(tab);
    }

    public static List<Header> getAllHeaders() {
        return new Select().from(Header.class).queryList();
    }

    public static Header findById(Long id) {
        return new Select()
        .from(Header.class)
        .where(Header_Table.id_header.eq(id)).querySingle();
    }

    /**
     * Method to delete headers in cascade
     */
    public static void deleteHeaders(List<Header> headers) {
        for (Header header : headers) {
            Question.deleteQuestions(header.getQuestions());
            header.delete();
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

    public Tab getTab() {
        if(tab==null){
            if(id_tab_fk==null) return null;
            tab = new Select()
                    .from(Tab.class)
                    .where((Tab_Table.id_tab)
                            .is(id_tab_fk)).querySingle();
        }
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
        this.id_tab_fk = (tab != null) ? tab.getId_tab() : null;
    }

    public void setTab(Long id_tab) {
        this.id_tab_fk = id_tab;
        this.tab = null;
    }

    public List<Question> getQuestions() {
        if (this.questions == null){
            this.questions = new Select().from(Question.class)
                    .where(Question_Table.id_header_fk.eq(this.getId_header()))
                    .orderBy(OrderBy.fromProperty(Question_Table.order_pos)).queryList();
        }
        return questions;
    }

    /**
     * getNumber Of Question Parents Header
     */
    public long getNumberOfQuestionParents() {
        return SQLite.selectCountOf().from(Question.class)
                .where(Question_Table.id_header_fk.eq(getId_header()))
                .and(Question_Table.id_question_parent.isNull()).count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        if (id_header != header.id_header) return false;
        if (short_name != null ? !short_name.equals(header.short_name)
                : header.short_name != null) {
            return false;
        }
        if (name != null ? !name.equals(header.name) : header.name != null) return false;
        if (order_pos != null ? !order_pos.equals(header.order_pos) : header.order_pos != null) {
            return false;
        }
        return !(id_tab_fk != null ? !id_tab_fk.equals(header.id_tab_fk) : header.id_tab_fk != null);

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
        return "Header{" +
                "id_header=" + id_header +
                ", help_text='" + short_name + '\'' +
                ", name='" + name + '\'' +
                ", order_pos=" + order_pos +
                ", id_tab_fk=" + id_tab_fk +
                '}';
    }


}
