package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

import java.util.List;

@Table(databaseName = AppDatabase.NAME)
public class Treatment extends BaseModel {

    public static int TYPE_MAIN = 0;
    public static int TYPE_NOT_MAIN = 1;

    @Column
    @PrimaryKey(autoincrement = true)
    long id_treatment;
    @Column
    Long id_organisation;
    @Column
    Long diagnosis;
    @Column
    Long message;
    @Column
    int type;
    /**
     * Reference to organisation (loaded lazily)
     */
    Organisation organisation;


    public Treatment() {
    }

    public Treatment(long id_treatment, long id_organisation, Long diagnosis, Long message,
            int type) {
        this.id_treatment = id_treatment;
        this.id_organisation = id_organisation;
        this.diagnosis = diagnosis;
        this.message = message;
    }

    public static Treatment findById(long id) {
        return new Select()
                .from(Treatment.class)
                .where(Condition.column(Treatment$Table.ID_TREATMENT).is(id))
                .querySingle();

    }

    public static List<Treatment> getAllTreatments() {
        return new Select().all().from(Treatment.class).queryList();
    }

    public List<Drug> getDrugsForTreatment() {
        return new Select().from(Drug.class).as("d")
                .join(DrugCombination.class, Join.JoinType.LEFT).as("dc")
                .on(Condition.column(ColumnAlias.columnWithTable("d", Drug$Table.ID_DRUG))
                        .eq(ColumnAlias.columnWithTable("dc", DrugCombination$Table.ID_DRUG)))
                .where(Condition.column(
                        ColumnAlias.columnWithTable("dc", DrugCombination$Table.ID_TREATMENT))
                        .is(id_treatment)).queryList();
    }


    public long getId_treatment() {
        return id_treatment;
    }

    public void setId_treatment(long id_treatment) {
        this.id_treatment = id_treatment;
    }


    public Organisation getOrganisation() {
        if (organisation == null) {
            if (id_organisation == null) {
                return null;
            }
            organisation = new Select()
                    .from(Organisation.class)
                    .where(Condition.column(Organisation$Table.ID_ORGANISATION)
                            .is(id_organisation)).querySingle();
        }
        return organisation;
    }

    public void setOrganisation(Long id_organisation) {
        this.id_organisation = id_organisation;
        organisation = null;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        this.id_organisation = (organisation != null) ? organisation.getId_organisation() : null;
    }

    public Long getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Long diagnosis) {
        this.diagnosis = diagnosis;
    }

    public Long getMessage() {
        return message;
    }

    public void setMessage(Long message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Treatment treatment = (Treatment) o;

        if (id_treatment != treatment.id_treatment) return false;
        if (type != treatment.type) return false;
        if (id_organisation != null ? !id_organisation.equals(treatment.id_organisation)
                : treatment.id_organisation != null) {
            return false;
        }
        if (diagnosis != null ? !diagnosis.equals(treatment.diagnosis)
                : treatment.diagnosis != null) {
            return false;
        }
        return message != null ? message.equals(treatment.message) : treatment.message == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_treatment ^ (id_treatment >>> 32));
        result = 31 * result + (id_organisation != null ? id_organisation.hashCode() : 0);
        result = 31 * result + (diagnosis != null ? diagnosis.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }


    @Override
    public String toString() {
        return "Treatment{" +
                "id_treatment=" + id_treatment +
                ", id_organisation=" + id_organisation +
                ", diagnosis='" + diagnosis + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", organisation=" + organisation +
                '}';
    }
}
