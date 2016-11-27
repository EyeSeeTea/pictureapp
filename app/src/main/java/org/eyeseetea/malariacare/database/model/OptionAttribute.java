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

import android.view.Gravity;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

@Table(databaseName = AppDatabase.NAME)
public class OptionAttribute extends BaseModel {

    /**
     * Constant that reflects a left alignment
     */
    public static final int HORIZONTAL_ALIGNMENT_LEFT = 0;
    /**
     * Constant that reflects a right alignment
     */
    public static final int HORIZONTAL_ALIGNMENT_CENTER = 1;
    /**
     * Constant that reflects a center alignment
     */
    public static final int HORIZONTAL_ALIGNMENT_RIGHT = 2;
    /**
     * Constant that reflects a not applicable horizontal alignment
     */
    public static final int HORIZONTAL_ALIGNMENT_NONE = 3;
    /**
     * Constant that reflects a DEFAULT alignment
     */
    public static final int DEFAULT_HORIZONTAL_ALIGNMENT = HORIZONTAL_ALIGNMENT_NONE;
    /**
     * Constant that reflects a top alignment
     */
    public static final int VERTICAL_ALIGNMENT_TOP = 0;
    /**
     * Constant that reflects a middle alignment
     */
    public static final int VERTICAL_ALIGNMENT_MIDDLE = 1;
    /**
     * Constant that reflects a bottom alignment
     */
    public static final int VERTICAL_ALIGNMENT_BOTTOM = 2;
    /**
     * Constant that reflects a not applicable vertical alignment
     */
    public static final int VERTICAL_ALIGNMENT_NONE = 3;
    /**
     * Constant that reflects a DEFAULT alignment
     */
    public static final int DEFAULT_VERTICAL_ALIGNMENT = VERTICAL_ALIGNMENT_NONE;
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
    @Column
    int text_size;
    @Column
    int default_option;

    public OptionAttribute() {
    }

    public OptionAttribute(String background_colour, String path) {
        this.background_colour = background_colour;
        this.path = path;
    }

    public static List<OptionAttribute> getAllOptionAttributes() {
        return new Select().from(OptionAttribute.class).queryList();
    }

    public static OptionAttribute findById(Long id) {
        return new Select()
                .from(OptionAttribute.class)
                .where(Condition.column(OptionAttribute$Table.ID_OPTION_ATTRIBUTE).eq(
                        id)).querySingle();
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

    public String getInternationalizedPath() {
        return Utils.getInternationalizedString(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getText_size() {
        return text_size;
    }

    public void setText_size(int size) {
        this.text_size = size;
    }

    public int getDefaultOption() {
        return default_option;
    }

    public void setDefaultOption(int default_option) {
        this.default_option = default_option;
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

    public boolean hasHorizontalAlignment() {
        return horizontal_alignment != HORIZONTAL_ALIGNMENT_NONE;
    }

    public boolean hasVerticalAlignment() {
        return vertical_alignment != VERTICAL_ALIGNMENT_NONE;
    }

    public boolean isHorizontalCenter() {
        return horizontal_alignment == HORIZONTAL_ALIGNMENT_CENTER;
    }

    public boolean isHorizontalLeft() {
        return horizontal_alignment == HORIZONTAL_ALIGNMENT_LEFT;
    }

    public boolean isHorizontalRight() {
        return horizontal_alignment == HORIZONTAL_ALIGNMENT_RIGHT;
    }

    public boolean isVerticalBottom() {
        return vertical_alignment == VERTICAL_ALIGNMENT_BOTTOM;
    }

    public boolean isVerticalTop() {
        return vertical_alignment == VERTICAL_ALIGNMENT_TOP;
    }

    public boolean isVerticalMiddle() {
        return vertical_alignment == VERTICAL_ALIGNMENT_MIDDLE;
    }

    /**
     * The option gravity is the result of the (vertical | horizontal) gravity.
     */
    public int getGravity() {
        int verticalGravity = Gravity.CENTER_HORIZONTAL;
        int horizontalGravity = Gravity.CENTER_VERTICAL;
        if (isHorizontalRight()) {
            horizontalGravity = Gravity.RIGHT;
        } else if (isHorizontalLeft()) {
            horizontalGravity = Gravity.LEFT;
        } else if (isHorizontalCenter()) {
            horizontalGravity = Gravity.CENTER_HORIZONTAL;
        }
        if (isVerticalBottom()) {
            verticalGravity = Gravity.BOTTOM;
        } else if (isVerticalTop()) {
            verticalGravity = Gravity.TOP;
        } else if (isVerticalMiddle()) {
            verticalGravity = Gravity.CENTER_VERTICAL;
        }

        return (verticalGravity | horizontalGravity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionAttribute that = (OptionAttribute) o;

        if (id_option_attribute != that.id_option_attribute) return false;
        if (horizontal_alignment != that.horizontal_alignment) return false;
        if (vertical_alignment != that.vertical_alignment) return false;
        if (text_size != that.text_size) return false;
        if (default_option != that.default_option) return false;
        if (background_colour != null ? !background_colour.equals(that.background_colour)
                : that.background_colour != null) {
            return false;
        }
        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_option_attribute ^ (id_option_attribute >>> 32));
        result = 31 * result + (background_colour != null ? background_colour.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + horizontal_alignment;
        result = 31 * result + vertical_alignment;
        result = 31 * result + text_size;
        result = 31 * result + default_option;
        return result;
    }

    @Override
    public String toString() {
        return "OptionAttribute{" +
                "id_option_attribute=" + id_option_attribute +
                ", background_colour='" + background_colour + '\'' +
                ", path='" + path + '\'' +
                ", horizontal_alignment=" + horizontal_alignment +
                ", vertical_alignment=" + vertical_alignment +
                ", text_size=" + text_size +
                ", default_option=" + default_option +
                '}';
    }
}
