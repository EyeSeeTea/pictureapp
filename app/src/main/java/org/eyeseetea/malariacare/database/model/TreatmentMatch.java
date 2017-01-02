package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

/**
 * Created by manuel on 2/01/17.
 */
@Table(databaseName = AppDatabase.NAME)
public class TreatmentMatch extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_treatment_match;
    @Column
    long id_treatment;
    @Column
    long id_match;


    public TreatmentMatch() {
    }

    public TreatmentMatch(long id_treatment_match, long id_treatment, long id_match) {
        this.id_treatment_match = id_treatment_match;
        this.id_treatment = id_treatment;
        this.id_match = id_match;
    }


    public long getId_treatment_match() {
        return id_treatment_match;
    }

    public void setId_treatment_match(long id_treatment_match) {
        this.id_treatment_match = id_treatment_match;
    }

    public long getId_treatment() {
        return id_treatment;
    }

    public void setId_treatment(long id_treatment) {
        this.id_treatment = id_treatment;
    }

    public long getId_match() {
        return id_match;
    }

    public void setId_match(long id_match) {
        this.id_match = id_match;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreatmentMatch that = (TreatmentMatch) o;

        if (id_treatment_match != that.id_treatment_match) return false;
        if (id_treatment != that.id_treatment) return false;
        return id_match == that.id_match;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_treatment_match ^ (id_treatment_match >>> 32));
        result = 31 * result + (int) (id_treatment ^ (id_treatment >>> 32));
        result = 31 * result + (int) (id_match ^ (id_match >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "TreatmentMatch{" +
                "id_treatment_match=" + id_treatment_match +
                ", id_treatment=" + id_treatment +
                ", id_match=" + id_match +
                '}';
    }
}
