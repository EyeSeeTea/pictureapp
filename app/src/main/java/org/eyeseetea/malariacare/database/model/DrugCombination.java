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
public class DrugCombination extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_drug_combination;
    @Column
    long id_drug;

    public DrugCombination() {
    }

    public DrugCombination(long id_drug_combination, long id_drug) {
        this.id_drug_combination = id_drug_combination;
        this.id_drug = id_drug;
    }

    public long getId_drug_combination() {
        return id_drug_combination;
    }

    public void setId_drug_combination(long id_drug_combination) {
        this.id_drug_combination = id_drug_combination;
    }

    public long getId_drug() {
        return id_drug;
    }

    public void setId_drug(long id_drug) {
        this.id_drug = id_drug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrugCombination that = (DrugCombination) o;

        if (id_drug_combination != that.id_drug_combination) return false;
        return id_drug == that.id_drug;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_drug_combination ^ (id_drug_combination >>> 32));
        result = 31 * result + (int) (id_drug ^ (id_drug >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DrugCombination{" +
                "id_drug_combination=" + id_drug_combination +
                ", id_drug=" + id_drug +
                '}';
    }
}
