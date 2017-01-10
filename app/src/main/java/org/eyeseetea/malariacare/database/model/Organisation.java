package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

/**
 * Created by manuel on 2/01/17.
 */
@Table(databaseName = AppDatabase.NAME)
public class Organisation extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_organisation;
    @Column
    String uid;
    @Column
    String name;

    public Organisation() {
    }
    public static Organisation findById(long id) {
        return new Select()
                .from(Organisation.class)
                .where(Condition.column(Organisation$Table.ID_ORGANISATION).is(id))
                .querySingle();
    }

    public long getId_organisation() {
        return id_organisation;
    }

    public void setId_organisation(long id_organisation) {
        this.id_organisation = id_organisation;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organisation that = (Organisation) o;

        if (id_organisation != that.id_organisation) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_organisation ^ (id_organisation >>> 32));
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Organisation{" +
                "id_organisation=" + id_organisation +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }


}
