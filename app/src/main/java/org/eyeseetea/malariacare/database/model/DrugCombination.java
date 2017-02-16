package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class DrugCombination extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_drug_combination;
    @Column
    Long id_drug;
    @Column
    Long id_treatment;

    /**
     * Reference to drug (loaded lazily)
     */
    Drug drug;
    /**
     * Reference to treatment (loaded lazily)
     */
    Treatment treatment;

    public DrugCombination() {
    }

    public DrugCombination(long id_drug_combination, long id_drug, long id_treatment) {
        this.id_drug_combination = id_drug_combination;
        this.id_drug = id_drug;
        this.id_treatment = id_treatment;
    }

    public static List<DrugCombination> getAllDrugCombination() {
        return new Select().from(DrugCombination.class).queryList();
    }

    public long getId_drug_combination() {
        return id_drug_combination;
    }

    public void setId_drug_combination(long id_drug_combination) {
        this.id_drug_combination = id_drug_combination;
    }

    public Drug getDrug() {
        if (drug == null) {
            if (id_drug == null) {
                return null;
            }
            drug = new Select()
                    .from(Drug.class)
                    .where(Drug_Table.id_drug
                            .is(id_drug)).querySingle();
        }
        return drug;
    }

    public void setDrug(Drug drug) {
        this.drug = drug;
        id_drug = (drug != null) ? drug.getId_drug() : null;
    }

    public void setDrug(Long id_drug) {
        this.id_drug = id_drug;
        drug = null;
    }

    public Treatment getTreatment() {
        if (treatment == null) {
            if (id_treatment == null) {
                return null;
            }
            treatment = new Select()
                    .from(Treatment.class)
                    .where(Treatment_Table.id_treatment
                            .is(id_treatment)).querySingle();
        }
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
        id_treatment = (treatment != null) ? treatment.id_treatment : null;
    }

    public void setTreatment(Long id_treatment) {
        this.id_treatment = id_treatment;
        treatment = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrugCombination that = (DrugCombination) o;

        if (id_drug_combination != that.id_drug_combination) return false;
        if (id_drug != that.id_drug) return false;
        return id_treatment == that.id_treatment;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_drug_combination ^ (id_drug_combination >>> 32));
        result = 31 * result + (int) (id_drug ^ (id_drug >>> 32));
        result = 31 * result + (int) (id_treatment ^ (id_treatment >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DrugCombination{" +
                "id_drug_combination=" + id_drug_combination +
                ", id_drug=" + id_drug +
                ", id_treatment=" + id_treatment +
                '}';
    }
}
