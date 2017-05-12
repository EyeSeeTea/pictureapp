package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class TreatmentMatch extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_treatment_match;
    @Column
    Long id_treatment_fk;
    @Column
    Long id_match_fk;
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
        this.id_treatment_fk = id_treatment;
        this.id_match_fk = id_match;
    }

    public static List<TreatmentMatch> getAllTreatmentMatches() {
        return new Select().from(TreatmentMatch.class).queryList();
    }

    /**
     * Method to delete a list of treatmentMatches.
     *
     * @param treatmentMatches The list to delete.
     */
    public static void deleteTreatmentMatches(List<TreatmentMatch> treatmentMatches) {
        for (TreatmentMatch treatmentMatch : treatmentMatches) {
            treatmentMatch.delete();
        }
    }


    public long getId_treatment_match() {
        return id_treatment_match;
    }

    public void setId_treatment_match(long id_treatment_match) {
        this.id_treatment_match = id_treatment_match;
    }

    public Treatment getTreatment() {
        if (treatment == null) {
            if (id_treatment_fk == null) {
                return null;
            }
            treatment = new Select()
                    .from(Treatment.class)
                    .where(Treatment_Table.id_treatment
                            .is(id_treatment_fk)).querySingle();

        }
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
        id_treatment_fk = (treatment != null) ? treatment.id_treatment : null;
    }

    public void setTreatment(long id_treatment) {
        this.id_treatment_fk = id_treatment;
        treatment = null;
    }

    public Match getMatch() {
        if (match == null) {
            if (id_match_fk == null) {
                return null;
            }
            match = new Select()
                    .from(Match.class)
                    .where(Match_Table.id_match
                            .is(id_match_fk)).querySingle();
        }
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
        id_match_fk = (match != null) ? match.getId_match() : null;
    }

    public void setMatch(Long id_match) {
        this.id_match_fk = id_match;
        match = null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreatmentMatch that = (TreatmentMatch) o;

        if (id_treatment_match != that.id_treatment_match) return false;
        if (id_treatment_fk != that.id_treatment_fk) return false;
        return id_match_fk == that.id_match_fk;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_treatment_match ^ (id_treatment_match >>> 32));
        result = 31 * result + (int) (id_treatment_fk ^ (id_treatment_fk >>> 32));
        result = 31 * result + (int) (id_match_fk ^ (id_match_fk >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "TreatmentMatch{" +
                "id_treatment_match=" + id_treatment_match +
                ", id_treatment_fk=" + id_treatment_fk +
                ", id_match_fk=" + id_match_fk +
                '}';
    }


}
