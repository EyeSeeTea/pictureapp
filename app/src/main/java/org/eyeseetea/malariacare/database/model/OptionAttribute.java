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
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

import java.util.List;

@Table(databaseName = AppDatabase.NAME)
public class OptionAttribute extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_option_attribute;

    @Column
    String background_colour;

    @Column
    String path;

    @Column
    int horizontal_alignment;

    @Column
    int vertical_alignment;

    /**
     * Constant that reflects a left alignment
     */
    public static final int HORIZONTAL_ALIGMENT_LEFT=0;
    /**
     * Constant that reflects a right alignment
     */
    public static final int HORIZONTAL_ALIGMENT_CENTER=1;

    /**
     * Constant that reflects a center alignment
     */
    public static final int HORIZONTAL_ALIGMENT_RIGHT=2;
    /**
     * Constant that reflects a DEFAULT alignment
     */
    public static final int DEFAULT_HORIZONTAL_ALIGMENT=HORIZONTAL_ALIGMENT_CENTER;

    /**
     * Constant that reflects a top alignment
     */
    public static final int VERTICAL_ALIGMENT_TOP=0;
    /**
     * Constant that reflects a middle alignment
     */
    public static final int VERTICAL_ALIGMENT_MIDDLE=1;

    /**
     * Constant that reflects a bottom alignment
     */
    public static final int VERTICAL_ALIGMENT_BOTTOM=2;
    /**
     * Constant that reflects a DEFAULT alignment
     */
    public static final int DEFAULT_VERTICAL_ALIGMENT=VERTICAL_ALIGMENT_BOTTOM;

    public OptionAttribute() {
    }

    public OptionAttribute(String background_colour, String path) {
        this.background_colour = background_colour;
        this.path = path;
    }

    public static List<OptionAttribute> getAllOptionAttributes() {
        return new Select().from(OptionAttribute.class).queryList();
    }

    public long getId_option_attribute() {
        return id_option_attribute;
    }

    public void setId_option_attribute(long id_option_attribute) {
        this.id_option_attribute = id_option_attribute;
    }

    public String getBackground_colour() {
        return background_colour;
    }

    public void setBackground_colour(String background_colour) {
        this.background_colour = background_colour;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getHorizontal_alignment() {
        return horizontal_alignment;
    }

    public void setHorizontal_alignment(int horizontal_alignment) {
        this.horizontal_alignment = horizontal_alignment;
    }

    public int getVertical_alignment() {
        return vertical_alignment;
    }

    public void setVertical_alignment(int vertical_alignment) {
        this.vertical_alignment = vertical_alignment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionAttribute that = (OptionAttribute) o;

        if (id_option_attribute != that.id_option_attribute) return false;
        if (horizontal_alignment != that.horizontal_alignment) return false;
        if (vertical_alignment != that.vertical_alignment) return false;
        if (background_colour != null ? !background_colour.equals(that.background_colour) : that.background_colour != null)
            return false;
        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_option_attribute ^ (id_option_attribute >>> 32));
        result = 31 * result + (background_colour != null ? background_colour.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + horizontal_alignment;
        result = 31 * result + vertical_alignment;
        return result;
    }

    @Override
    public String toString() {
        return "OptionAttribute{" +
                "id_option_attribute=" + id_option_attribute +
                ", background_colour='" + background_colour + '\'' +
                ", path='" + path + '\'' +
                ", horizontal_alignment='" + horizontal_alignment + '\'' +
                ", vertical_alignment='" + vertical_alignment + '\'' +
                '}';
    }
}
