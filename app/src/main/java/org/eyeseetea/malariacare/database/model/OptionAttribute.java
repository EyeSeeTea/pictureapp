package org.eyeseetea.malariacare.database.model;

import com.orm.SugarRecord;


public class OptionAttribute extends SugarRecord<OptionAttribute> {


    String background_colour;

    public OptionAttribute() {
    }

    public OptionAttribute(String background_colour) {
        this.background_colour = background_colour;
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
                "background_colour='" + background_colour + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OptionAttribute that = (OptionAttribute) o;

        return !(background_colour != null ? !background_colour.equals(that.background_colour) : that.background_colour != null);

    }

    @Override
    public int hashCode() {
        return background_colour != null ? background_colour.hashCode() : 0;
    }
}
