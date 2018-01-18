package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.Date;
import java.util.List;


@Table(database = AppDatabase.class, name = "CountryVersion")
public class CountryVersionDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    Long id_country_version;

    @Unique
    @Column
    String country;

    @Column
    int version;

    @Column
    Date last_update;

    @Unique
    @Column
    String uid;

    public CountryVersionDB() {
    }

    public CountryVersionDB(Long id_country_version, String country, int version,
            Date last_update, String uid) {
        this.id_country_version = id_country_version;
        this.country = country;
        this.version = version;
        this.last_update = last_update;
        this.uid = uid;
    }

    public static boolean isCountryAlreadyAdded(String countryCode) {
        long count = new Select(Method.count())
                .from(CountryVersionDB.class).where(
                        (CountryVersionDB_Table.country).is(countryCode.toLowerCase())).count();
        return count == 1;
    }

    public static boolean isVersionGreater(String countryCode, int version) {
        long versionDB = new Select(CountryVersionDB_Table.version)
                .from(CountryVersionDB.class).where(
                        (CountryVersionDB_Table.country).is(countryCode)).count();
        return version > versionDB;
    }

    public static void deleteAll() {
        for (CountryVersionDB countryVersionDB : getAllCountryVersionDB()) {
            countryVersionDB.delete();
        }
    }

    public static List<CountryVersionDB> getAllCountryVersionDB() {
        return new Select().from(CountryVersionDB.class).queryList();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CountryVersionDB that = (CountryVersionDB) o;

        if (version != that.version) return false;
        if (id_country_version != null ? !id_country_version.equals(that.id_country_version)
                : that.id_country_version != null) {
            return false;
        }
        if (country != null ? !country.equals(that.country) : that.country != null) return false;
        if (last_update != null ? !last_update.equals(that.last_update)
                : that.last_update != null) {
            return false;
        }
        return uid != null ? uid.equals(that.uid) : that.uid == null;
    }

    @Override
    public int hashCode() {
        int result = id_country_version != null ? id_country_version.hashCode() : 0;
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + (last_update != null ? last_update.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }

    public Long getId_country_version() {
        return id_country_version;
    }

    public void setId_country_version(Long id_country_version) {
        this.id_country_version = id_country_version;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getLast_update() {
        return last_update;
    }

    public void setLast_update(Date last_update) {
        this.last_update = last_update;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}