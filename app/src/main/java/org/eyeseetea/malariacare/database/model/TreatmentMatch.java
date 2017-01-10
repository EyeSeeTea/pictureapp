package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
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
    Long id_treatment;
    @Column
    Long id_match;
    /**
     * Reference to the treatment (loaded lazily)
     */
    Treatment treatment;
    /**
     * Reference to the match (loaded lazily)
     */
    Match match;


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

    public Treatment getTreatment() {
        if (treatment == null) {
            if (id_treatment == null) {
                return null;
            }
            treatment = new Select()
                    .from(Treatment.class)
                    .where(Condition.column(Treatment$Table.ID_TREATMENT)
                            .is(id_treatment)).querySingle();

    }
    return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
        id_treatment = (treatment != null) ? treatment.id_treatment : null;
    }

    public void setTreatment(long id_treatment) {
        this.id_treatment = id_treatment;
        treatment = null;
    }

    public Match getMatch() {
        if (match == null) {
            if (id_match == null) {
                return null;
            }
            match = new Select()
                    .from(Match.class)
                    .where(Condition.column(Match$Table.ID_MATCH)
                            .is(id_match)).querySingle();
        }
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
        id_match = (match != null) ? match.id_match : null;
    }

    public void setMatch(Long id_match) {
        this.id_match = id_match;
        match = null;
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
