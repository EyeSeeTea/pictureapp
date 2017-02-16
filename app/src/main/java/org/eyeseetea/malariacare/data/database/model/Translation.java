package org.eyeseetea.malariacare.data.database.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;


import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;


@Table(database = AppDatabase.class)
public class Translation extends BaseModel {

    public static String DEFAULT_LANGUAGE = "default";

    @Column
    @PrimaryKey(autoincrement = true)
    Long id_translation;
    @Column
    Long id_string_key;
    @Column
    String translation;
    @Column
    String language;

    public Translation() {
    }

    public Translation(Long id_translation, Long id_string_key,
            String translation, String language) {
        this.id_translation = id_translation;
        this.id_string_key = id_string_key;
        this.translation = translation;
        this.language = language;
    }

    public static List<Translation> getAllTranslations() {
        return new Select()
                .from(Translation.class)
                .queryList();
    }

    public Long getId_translation() {
        return id_translation;
    }

    public void setId_translation(Long id_translation) {
        this.id_translation = id_translation;
    }

    public Long getId_string_key() {
        return id_string_key;
    }

    public void setId_string_key(Long id_string_key) {
        this.id_string_key = id_string_key;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public static String getLocalizedString(Long id_string, String language) {
        Translation translation = new Select()
                .from(Translation.class)
                .where(Translation_Table.id_string_key.is(id_string))
                .and(Translation_Table.language.is(language))
                .querySingle();
        if (translation == null || translation.getTranslation() == null
                || translation.getTranslation().isEmpty()) {
            String generalLanguage = language.split("_")[0];
            translation = new Select()
                    .from(Translation.class)
                    .where(Translation_Table.id_string_key.is(id_string))
                    .and(Translation_Table.language.is(generalLanguage))
                    .querySingle();
        }
        if (translation == null || translation.getTranslation() == null
                || translation.getTranslation().isEmpty()) {
            translation = new Select()
                    .from(Translation.class)
                    .where(Translation_Table.id_string_key.is(id_string))
                    .and(Translation_Table.language.is(DEFAULT_LANGUAGE))
                    .querySingle();
        }

        return translation != null && translation.getTranslation() != null
                ? translation.getTranslation() : id_string + "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Translation that = (Translation) o;

        if (id_translation != null ? !id_translation.equals(that.id_translation)
                : that.id_translation != null) {
            return false;
        }
        if (id_string_key != null ? !id_string_key.equals(that.id_string_key) : that.id_string_key
                != null) {
            return false;
        }
        if (translation != null ? !translation.equals(that.translation)
                : that.translation != null) {
            return false;
        }
        return language != null ? language.equals(that.language) : that.language == null;

    }

    @Override
    public int hashCode() {
        int result = id_translation != null ? id_translation.hashCode() : 0;
        result = 31 * result + (id_string_key != null ? id_string_key.hashCode() : 0);
        result = 31 * result + (translation != null ? translation.hashCode() : 0);
        result = 31 * result + (language != null ? language.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "id_translation=" + id_translation +
                ", id_string=" + id_string_key +
                ", translation=" + translation +
                ", language=" + language +
                '}';
    }


}
