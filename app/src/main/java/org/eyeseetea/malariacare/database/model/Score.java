package org.eyeseetea.malariacare.database.model;

import com.orm.SugarRecord;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

/**
 * Created by adrian on 14/02/15.
 */
@Table(databaseName = AppDatabase.NAME)
public class Score extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_score;

    @Column
    Float value;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_tab",
            columnType = Long.class,
            foreignColumnName = "id_tab")},
            saveForeignKeyModel = false)
    Tab tab;

    @Column
    String uid;

    public Score() {
    }

    public Score(Float real, Tab tab, String uid) {
        this.value = real;
        this.tab = tab;
        this.uid = uid;
    }

    public Long getId_score() {
        return id_score;
    }

    public void setId_score(Long id_score) {
        this.id_score = id_score;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float real) {
        this.value = real;
    }

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Score{" +
                "id='" + id_score + '\'' +
                "real=" + value +
                ", tab=" + tab +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Score score = (Score) o;

        if (value != null ? !value.equals(score.value) : score.value != null) return false;
        if (tab != null ? !tab.equals(score.tab) : score.tab != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (tab != null ? tab.hashCode() : 0);
        return result;
    }
}
