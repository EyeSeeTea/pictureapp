package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class Drug extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_drug;
    @Column
    String name;
    @Column
    int dose;
    @Column
    String question_code;

    public Drug() {
    }

    public Drug(long id_drug, String name, int dose, String question_code) {
        this.id_drug = id_drug;
        this.name = name;
        this.dose = dose;
        this.question_code = question_code;
    }

    public static Drug findById(long id) {
        return new Select()
                .from(Drug.class)
                .where(Drug_Table.id_drug.is(id))
                .querySingle();
    }

    public static List<Drug> getAllDrugs() {
        return new Select().from(Drug.class).queryList();
    }

    public long getId_drug() {
        return id_drug;
    }

    public void setId_drug(long id_drug) {
        this.id_drug = id_drug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDose() {
        return dose;
    }

    public void setDose(int dose) {
        this.dose = dose;
    }

    public String getQuestion_code() {
        return question_code;
    }

    public void setQuestion_code(String question_code) {
        this.question_code = question_code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Drug drug = (Drug) o;

        if (id_drug != drug.id_drug) return false;
        if (dose != drug.dose) return false;
        if (name != null ? !name.equals(drug.name) : drug.name != null) return false;
        return question_code != null ? question_code.equals(drug.question_code)
                : drug.question_code == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_drug ^ (id_drug >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + dose;
        result = 31 * result + (question_code != null ? question_code.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Drug{" +
                "id_drug=" + id_drug +
                ", name='" + name + '\'' +
                ", dose=" + dose +
                ", question_code='" + question_code + '\'' +
                '}';
    }


}
