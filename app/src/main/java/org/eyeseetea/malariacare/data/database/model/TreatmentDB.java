package org.eyeseetea.malariacare.data.database.model;

import static org.eyeseetea.malariacare.data.database.AppDatabase.drugAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.drugCombinationAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.drugCombinationName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.drugName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.matchAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.matchName;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.NameAlias;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class, name = "Treatment")
public class TreatmentDB extends BaseModel {

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
    Long id_partner_fk;
    @Column
    Long diagnosis;
    @Column
    Long message;
    @Column
    int type;
    /**
     * Reference to mPartnerDB (loaded lazily)
     */
    PartnerDB mPartnerDB;


    public TreatmentDB() {
    }

    public TreatmentDB(long id_treatment, long id_organisation, Long diagnosis, Long message,
            int type) {
        this.id_treatment = id_treatment;
        this.id_partner_fk = id_organisation;
        this.diagnosis = diagnosis;
        this.message = message;
    }

    public static TreatmentDB findById(long id) {
        return new Select()
                .from(TreatmentDB.class)
                .where(TreatmentDB_Table.id_treatment.is(id))
                .querySingle();

    }

    public static List<TreatmentDB> getAllTreatments() {
        return new Select().from(TreatmentDB.class).queryList();
    }

    public List<DrugDB> getDrugsForTreatment() {
        return new Select().from(DrugDB.class).as(drugName)
                .join(DrugCombinationDB.class, Join.JoinType.LEFT_OUTER).as(drugCombinationName)
                .on(DrugDB_Table.id_drug.withTable(drugAlias)
                        .eq(DrugCombinationDB_Table.id_drug_fk.withTable(drugCombinationAlias)))
                .where(DrugCombinationDB_Table.id_treatment_fk.withTable(drugCombinationAlias)
                        .is(id_treatment)).queryList();

    }

    public List<TreatmentDB> getAlternativeTreatments() {
        MatchDB matchDB =new Select()
                .from(MatchDB.class).as(matchName)
                .join(TreatmentMatchDB.class, Join.JoinType.INNER).as(treatmentMatchName)
                .on(MatchDB_Table.id_match.withTable(matchAlias).is(TreatmentMatchDB_Table.id_match_fk.withTable(treatmentMatchAlias)))
                .where(TreatmentMatchDB_Table.id_treatment_fk.withTable(treatmentMatchAlias).is(id_treatment))
                .querySingle();
        List<TreatmentDB> treatmentDBs = new Select()
                .from(TreatmentDB.class).as(treatmentName)
                .join(TreatmentMatchDB.class, Join.JoinType.INNER).as(treatmentMatchName)
                .on(TreatmentDB_Table.id_treatment.withTable(treatmentAlias)
                        .eq(TreatmentMatchDB_Table.id_treatment_fk.withTable(treatmentMatchAlias)))
                .where(TreatmentMatchDB_Table.id_match_fk.withTable(treatmentMatchAlias)
                        .is(matchDB.getId_match()))
                .and(TreatmentDB_Table.type.withTable(treatmentAlias).is(TYPE_NOT_MAIN))
                .queryList();
        //FIXME: select split in two because DBFLOW bug
        return treatmentDBs;
    }

    public long getId_treatment() {
        return id_treatment;
    }

    public void setId_treatment(long id_treatment) {
        this.id_treatment = id_treatment;
    }


    public PartnerDB getPartnerDB() {
        if (mPartnerDB == null) {
            if (id_partner_fk == null) {
                return null;
            }
            mPartnerDB = new Select()
                    .from(PartnerDB.class)
                    .where(PartnerDB_Table.id_partner
                            .is(id_partner_fk)).querySingle();
        }
        return mPartnerDB;
    }

    public void setPartnerDB(Long id_organisation) {
        this.id_partner_fk = id_organisation;
        mPartnerDB = null;
    }

    public void setOrganisation(PartnerDB partnerDB) {
        this.mPartnerDB = partnerDB;
        this.id_partner_fk = (partnerDB != null) ? partnerDB.getId_partner() : null;
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

        TreatmentDB treatmentDB = (TreatmentDB) o;

        if (id_treatment != treatmentDB.id_treatment) return false;
        if (type != treatmentDB.type) return false;
        if (id_partner_fk != null ? !id_partner_fk.equals(treatmentDB.id_partner_fk)
                : treatmentDB.id_partner_fk != null) {
            return false;
        }
        if (diagnosis != null ? !diagnosis.equals(treatmentDB.diagnosis)
                : treatmentDB.diagnosis != null) {
            return false;
        }
        return message != null ? message.equals(treatmentDB.message) : treatmentDB.message == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_treatment ^ (id_treatment >>> 32));
        result = 31 * result + (id_partner_fk != null ? id_partner_fk.hashCode() : 0);
        result = 31 * result + (diagnosis != null ? diagnosis.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }


    @Override
    public String toString() {
        return "TreatmentDB{" +
                "id_treatment=" + id_treatment +
                ", id_partner_fk=" + id_partner_fk +
                ", diagnosis='" + diagnosis + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", mPartnerDB=" + mPartnerDB +
                '}';
    }


}
