package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

@Table(database = AppDatabase.class, name = "PhoneFormat")
public class PhoneFormatDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    Long id;
    @Column
    Long id_program_fk;
    @Column
    String phoneMask;
    @Column
    String trunkPrefix;
    @Column
    String prefixToPut;

    public PhoneFormatDB() {
    }

    public PhoneFormatDB(Long id_program_fk, String phoneMask, String trunkPrefix,
            String prefixToPut) {
        this.id_program_fk = id_program_fk;
        this.phoneMask = phoneMask;
        this.trunkPrefix = trunkPrefix;
        this.prefixToPut = prefixToPut;
    }

    public Long getId() {
        return id;
    }

    public Long getId_program_fk() {
        return id_program_fk;
    }

    public void setId_program_fk(Long id_program_fk) {
        this.id_program_fk = id_program_fk;
    }

    public String getPhoneMask() {
        return phoneMask;
    }

    public void setPhoneMask(String phoneMask) {
        this.phoneMask = phoneMask;
    }

    public String getTrunkPrefix() {
        return trunkPrefix;
    }

    public void setTrunkPrefix(String trunkPrefix) {
        this.trunkPrefix = trunkPrefix;
    }

    public String getPrefixToPut() {
        return prefixToPut;
    }

    public void setPrefixToPut(String prefixToPut) {
        this.prefixToPut = prefixToPut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneFormatDB that = (PhoneFormatDB) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (id_program_fk != null ? !id_program_fk.equals(that.id_program_fk)
                : that.id_program_fk != null) {
            return false;
        }
        if (phoneMask != null ? !phoneMask.equals(that.phoneMask) : that.phoneMask != null) {
            return false;
        }
        if (trunkPrefix != null ? !trunkPrefix.equals(that.trunkPrefix)
                : that.trunkPrefix != null) {
            return false;
        }
        return prefixToPut != null ? prefixToPut.equals(that.prefixToPut)
                : that.prefixToPut == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (id_program_fk != null ? id_program_fk.hashCode() : 0);
        result = 31 * result + (phoneMask != null ? phoneMask.hashCode() : 0);
        result = 31 * result + (trunkPrefix != null ? trunkPrefix.hashCode() : 0);
        result = 31 * result + (prefixToPut != null ? prefixToPut.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PhoneFormatDB{" +
                "id=" + id +
                ", id_program_fk=" + id_program_fk +
                ", phoneMask='" + phoneMask + '\'' +
                ", trunkPrefix='" + trunkPrefix + '\'' +
                ", prefixToPut='" + prefixToPut + '\'' +
                '}';
    }
}
