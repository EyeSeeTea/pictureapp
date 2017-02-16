package org.eyeseetea.malariacare.data.database.model;

import static org.eyeseetea.malariacare.data.database.AppDatabase.drugAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.drugCombinationAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.drugCombinationName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.drugName;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class)
public class Treatment extends BaseModel {

    public static final int TYPE_MAIN = 0;
    public static final int TYPE_NOT_MAIN = 1;
    private static final String treatmentName = "t";
    private static final String treatmentMatchName = "tm";

    public static final NameAlias treatmentAlias = NameAlias.builder(treatmentName).build();
    public static final NameAlias treatmentMatchAlias = NameAlias.builder(
            treatmentMatchName).build();

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
                .where(Treatment_Table.id_treatment.is(id))
                .querySingle();

    }

    public static List<Treatment> getAllTreatments() {
        return new Select().from(Treatment.class).queryList();
    }

    public List<Drug> getDrugsForTreatment() {
        return new Select().from(Drug.class).as(drugName)
                .join(DrugCombination.class, Join.JoinType.LEFT_OUTER).as(drugCombinationName)
                .on(Drug_Table.id_drug.withTable(drugAlias)
                        .eq(DrugCombination_Table.id_drug.withTable(drugCombinationAlias)))
                .where(DrugCombination_Table.id_treatment.withTable(drugCombinationAlias)
                        .is(id_treatment)).queryList();

    }

    public List<Treatment> getAlternativeTreatments() {
        List<Treatment> treatments = new Select()
                .from(Treatment.class).as(treatmentName)
                .join(TreatmentMatch.class, Join.JoinType.LEFT_OUTER).as(treatmentMatchName)
                .on(Treatment_Table.id_treatment.withTable(treatmentAlias)
                        .eq(TreatmentMatch_Table.id_match.withTable(treatmentMatchAlias)))
                .where(TreatmentMatch_Table.id_treatment.withTable(treatmentMatchAlias)
                        .is(id_treatment))
                .and(Treatment_Table.type.withTable(treatmentAlias).is(TYPE_NOT_MAIN))
                .queryList();

        //TODO remove this related with DBFlow bug overriding ids
        List<Treatment> correctIdTreatments = new ArrayList<>();
        for (Treatment treatment : treatments) {
            Treatment correctTreatment = new Select()
                    .from(Treatment.class)
                    .where(Treatment_Table.diagnosis.is(treatment.getDiagnosis()))
                    .and(Treatment_Table.message.is(treatment.getMessage()))
                    .and(Treatment_Table.id_organisation.is(
                            treatment.getOrganisation().getId_organisation()))
                    .and(Treatment_Table.type.is(treatment.getType()))
                    .querySingle();
            correctIdTreatments.add(correctTreatment);
        }
        return correctIdTreatments;
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
                    .where(Organisation_Table.id_organisation
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
