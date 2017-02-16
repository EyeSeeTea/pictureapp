package org.eyeseetea.malariacare.database.model;

import android.content.Context;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

import java.util.List;

/**
 * Created by manuel on 2/01/17.
 */
@Table(databaseName = AppDatabase.NAME)
public class Drug extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_drug;
    @Column
    String name;
    @Column
    String question_code;

    public Drug() {
    }

    public Drug(long id_drug, String name, String question_code) {
        this.id_drug = id_drug;
        this.name = name;
        this.question_code = question_code;
    }

    public static Drug findById(long id) {
        return new Select()
                .from(Drug.class)
                .where(Condition.column(Drug$Table.ID_DRUG).is(id))
                .querySingle();
    }

    public static List<Drug> getAllDrugs() {
        return new Select().all().from(Drug.class).queryList();
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

    public String getQuestion_code() {
        return question_code;
    }

    public void setQuestion_code(String question_code) {
        this.question_code = question_code;
    }

    private Context getContext() {
        return PreferencesState.getInstance().getContext();
    }

    public boolean isACT24() {
        return question_code.equals(getContext().getResources().getString(R.string.act24QuestionUID));
    }

    public boolean isACT18() {
        return question_code.equals(getContext().getResources().getString(R.string.act18QuestionUID));
    }

    public boolean isACT12() {
        return question_code.equals(getContext().getResources().getString(R.string.act12QuestionUID));
    }

    public boolean isACT6() {
        return question_code.equals(getContext().getResources().getString(R.string.act6QuestionUID));
    }

    public boolean isPq() {
        return question_code.equals(getContext().getResources().getString(R.string.pqQuestionUID));
    }

    public boolean isCq() {
        return question_code.equals(getContext().getResources().getString(R.string.cqQuestionUID));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Drug drug = (Drug) o;

        if (id_drug != drug.id_drug) return false;
        if (name != null ? !name.equals(drug.name) : drug.name != null) return false;
        return question_code != null ? question_code.equals(drug.question_code)
                : drug.question_code == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_drug ^ (id_drug >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (question_code != null ? question_code.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Drug{" +
                "id_drug=" + id_drug +
                ", name='" + name + '\'' +
                ", question_code='" + question_code + '\'' +
                '}';
    }
}
