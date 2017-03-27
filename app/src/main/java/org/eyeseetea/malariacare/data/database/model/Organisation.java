package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class Organisation extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_organisation;
    @Column
    String uid_organisation;
    @Column
    String name;

    static final String DEFAULT_ORGANISATION = "MATRIX";
    public Organisation() {
    }

    public static Organisation findById(long id) {
        return new Select()
                .from(Organisation.class)
                .where(Organisation_Table.id_organisation.is(id))
                .querySingle();
    }


    public static Organisation getDefaultOrganization() {
        return new Select()
                .from(Organisation.class)
                .where(Organisation_Table.name.is(DEFAULT_ORGANISATION))
                .querySingle();
    }
    public static List<Organisation> getAllOrganisations() {
        return new Select().from(Organisation.class).queryList();
    }

    public long getId_organisation() {
        return id_organisation;
    }

    public void setId_organisation(long id_organisation) {
        this.id_organisation = id_organisation;
    }

    public String getUid() {
        return uid_organisation;
    }

    public void setUid(String uid) {
        this.uid_organisation = uid;
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
        if (uid_organisation != null ? !uid_organisation.equals(that.uid_organisation) : that.uid_organisation != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_organisation ^ (id_organisation >>> 32));
        result = 31 * result + (uid_organisation != null ? uid_organisation.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Organisation{" +
                "id_organisation=" + id_organisation +
                ", uid_organisation='" + uid_organisation + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}