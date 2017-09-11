package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class)
public class Partner extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    long id_partner;
    @Column
    String uid_partner;
    @Column
    String name;

    public Partner() {
    }

    static final String DEFAULT_PARTNER = "MATRIX";


    public static Partner findById(long id) {
        return new Select()
                .from(Partner.class)
                .where(Partner_Table.id_partner.is(id))
                .querySingle();
    }

    public static List<Partner> getAllOrganisations() {
        return new Select().from(Partner.class).queryList();
    }

    public static Partner getDefaultOrganization() {
        return new Select()
                .from(Partner.class)
                .where(Partner_Table.name.is(DEFAULT_PARTNER))
                .querySingle();
    }

    public long getId_partner() {
        return id_partner;
    }

    public void setId_partner(long id_partner) {
        this.id_partner = id_partner;
    }

    public String getUid() {
        return uid_partner;
    }

    public void setUid(String uid) {
        this.uid_partner = uid;
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

        Partner that = (Partner) o;

        if (id_partner != that.id_partner) return false;
        if (uid_partner != null ? !uid_partner.equals(that.uid_partner)
                : that.uid_partner != null) {
            return false;
        }
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_partner ^ (id_partner >>> 32));
        result = 31 * result + (uid_partner != null ? uid_partner.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Partner{" +
                "id_partner=" + id_partner +
                ", uid_partner='" + uid_partner + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}