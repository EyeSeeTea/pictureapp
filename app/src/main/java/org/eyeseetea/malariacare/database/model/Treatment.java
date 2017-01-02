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
public class Treatment extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_treatment;
    @Column
    long id_drug_combination;
    @Column
    long id_organisation;
    @Column
    String diagnosis;
    @Column
    String message;


    public Treatment() {
    }

    public Treatment(long id_treatment, long id_drug_combination, long id_organisation,
            String diagnosis, String message) {
        this.id_treatment = id_treatment;
        this.id_drug_combination = id_drug_combination;
        this.id_organisation = id_organisation;
        this.diagnosis = diagnosis;
        this.message = message;
    }

    public long getId_treatment() {
        return id_treatment;
    }

    public void setId_treatment(long id_treatment) {
        this.id_treatment = id_treatment;
    }

    public long getId_drug_combination() {
        return id_drug_combination;
    }

    public void setId_drug_combination(long id_drug_combination) {
        this.id_drug_combination = id_drug_combination;
    }

    public long getId_organisation() {
        return id_organisation;
    }

    public void setId_organisation(long id_organisation) {
        this.id_organisation = id_organisation;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Treatment treatment = (Treatment) o;

        if (id_treatment != treatment.id_treatment) return false;
        if (id_drug_combination != treatment.id_drug_combination) return false;
        if (id_organisation != treatment.id_organisation) return false;
        if (diagnosis != null ? !diagnosis.equals(treatment.diagnosis)
                : treatment.diagnosis != null) {
            return false;
        }
        return message != null ? message.equals(treatment.message) : treatment.message == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_treatment ^ (id_treatment >>> 32));
        result = 31 * result + (int) (id_drug_combination ^ (id_drug_combination >>> 32));
        result = 31 * result + (int) (id_organisation ^ (id_organisation >>> 32));
        result = 31 * result + (diagnosis != null ? diagnosis.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Treatment{" +
                "id_treatment=" + id_treatment +
                ", id_drug_combination=" + id_drug_combination +
                ", id_organisation=" + id_organisation +
                ", diagnosis='" + diagnosis + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
