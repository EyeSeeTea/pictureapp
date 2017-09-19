package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class, name = "TreatmentMatch")
public class TreatmentMatchDB extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_treatment_match;
    @Column
    Long id_treatment_fk;
    @Column
    Long id_match_fk;
    /**
     * Reference to the mTreatmentDB (loaded lazily)
     */
    TreatmentDB mTreatmentDB;
    /**
     * Reference to the mMatchDB (loaded lazily)
     */
    MatchDB mMatchDB;


    public TreatmentMatchDB() {
    }

    public TreatmentMatchDB(long id_treatment_match, long id_treatment, long id_match) {
        this.id_treatment_match = id_treatment_match;
        this.id_treatment_fk = id_treatment;
        this.id_match_fk = id_match;
    }

    public static List<TreatmentMatchDB> getAllTreatmentMatches() {
        return new Select().from(TreatmentMatchDB.class).queryList();
    }

    /**
     * Method to delete a list of treatmentMatchDBs.
     *
     * @param treatmentMatchDBs The list to delete.
     */
    public static void deleteTreatmentMatches(List<TreatmentMatchDB> treatmentMatchDBs) {
        for (TreatmentMatchDB treatmentMatchDB : treatmentMatchDBs) {
            treatmentMatchDB.delete();
        }
    }


    public long getId_treatment_match() {
        return id_treatment_match;
    }

    public void setId_treatment_match(long id_treatment_match) {
        this.id_treatment_match = id_treatment_match;
    }

    public TreatmentDB getTreatmentDB() {
        if (mTreatmentDB == null) {
            if (id_treatment_fk == null) {
                return null;
            }
            mTreatmentDB = new Select()
                    .from(TreatmentDB.class)
                    .where(TreatmentDB_Table.id_treatment
                            .is(id_treatment_fk)).querySingle();

        }
        return mTreatmentDB;
    }

    public void setTreatmentDB(TreatmentDB treatmentDB) {
        this.mTreatmentDB = treatmentDB;
        id_treatment_fk = (treatmentDB != null) ? treatmentDB.id_treatment : null;
    }

    public void setTreatment(long id_treatment) {
        this.id_treatment_fk = id_treatment;
        mTreatmentDB = null;
    }

    public MatchDB getMatchDB() {
        if (mMatchDB == null) {
            if (id_match_fk == null) {
                return null;
            }
            mMatchDB = new Select()
                    .from(MatchDB.class)
                    .where(MatchDB_Table.id_match
                            .is(id_match_fk)).querySingle();
        }
        return mMatchDB;
    }

    public void setMatchDB(MatchDB matchDB) {
        this.mMatchDB = matchDB;
        id_match_fk = (matchDB != null) ? matchDB.getId_match() : null;
    }

    public void setMatch(Long id_match) {
        this.id_match_fk = id_match;
        mMatchDB = null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TreatmentMatchDB that = (TreatmentMatchDB) o;

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
        return "TreatmentMatchDB{" +
                "id_treatment_match=" + id_treatment_match +
                ", id_treatment_fk=" + id_treatment_fk +
                ", id_match_fk=" + id_match_fk +
                '}';
    }


}
