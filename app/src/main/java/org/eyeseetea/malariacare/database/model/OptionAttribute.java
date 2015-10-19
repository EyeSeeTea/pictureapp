package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

@Table(databaseName = AppDatabase.NAME)
public class OptionAttribute extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_option_attribute;

    @Column

    String background_colour;

    public OptionAttribute() {
    }

    public OptionAttribute(String background_colour) {
        this.background_colour = background_colour;
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

    @Override
    public String toString() {
        return "OptionAttribute{" +
                "id_option_attribute=" + id_option_attribute +
                ", background_colour='" + background_colour + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionAttribute that = (OptionAttribute) o;

        if (id_option_attribute != that.id_option_attribute) return false;
        return !(background_colour != null ? !background_colour.equals(that.background_colour) : that.background_colour != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_option_attribute ^ (id_option_attribute >>> 32));
        result = 31 * result + (background_colour != null ? background_colour.hashCode() : 0);
        return result;
    }
}
