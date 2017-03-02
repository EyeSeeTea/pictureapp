package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;


import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class StringKey extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_string_key;
    @Column
    String key;

    public StringKey() {
    }

    public StringKey(long id_string_key, String key) {
        this.id_string_key = id_string_key;
        this.key = key;
    }

    public static List<StringKey> getAllStringKeys() {
        return new Select()
                .from(StringKey.class)
                .queryList();
    }

    public long getId_string_key() {
        return id_string_key;
    }

    public void setId_string_key(long id_string_key) {
        this.id_string_key = id_string_key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringKey string = (StringKey) o;

        if (id_string_key != string.id_string_key) return false;
        return key != null ? key.equals(string.key) : string.key == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_string_key ^ (id_string_key >>> 32));
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "String{" +
                "id_string=" + id_string_key +
                ", key=" + key +
                '}';
    }
}
