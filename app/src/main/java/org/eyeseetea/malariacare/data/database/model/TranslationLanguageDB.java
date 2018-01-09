package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.Date;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;


@Table(database = AppDatabase.class, name = "TranslationLanguage")
public class TranslationLanguageDB extends BaseModel {

    public static String DEFAULT_LANGUAGE = "default";

    @Column
    @PrimaryKey(autoincrement = true)
    Long id_translation_language;
    @Unique
    @Column
    String language_code;
    @Column
    String language_name;
    @Column
    Date last_update;

    public TranslationLanguageDB() {
    }

    public TranslationLanguageDB(Long id_translation_language, String language_code,
            String language_name, Date last_update) {
        this.id_translation_language = id_translation_language;
        this.language_code = language_code;
        this.language_name = language_name;
        this.last_update = last_update;
    }


    public static List<TranslationLanguageDB> getAllTranslationLanguages() {
        return new Select()
                .from(TranslationLanguageDB.class)
                .queryList();
    }

    public static String getDefaultLanguage() {
        return DEFAULT_LANGUAGE;
    }

    public static void setDefaultLanguage(String defaultLanguage) {
        DEFAULT_LANGUAGE = defaultLanguage;
    }

    public Long getId_translation_language() {
        return id_translation_language;
    }

    public void setId_translation_language(Long id_translation_language) {
        this.id_translation_language = id_translation_language;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

    public Date getLast_update() {
        return last_update;
    }

    public void setLast_update(Date last_update) {
        this.last_update = last_update;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranslationLanguageDB that = (TranslationLanguageDB) o;

        if (!id_translation_language.equals(that.id_translation_language)) return false;
        if (!language_code.equals(that.language_code)) return false;
        if (!language_name.equals(that.language_name)) return false;
        return last_update.equals(that.last_update);
    }

    @Override
    public int hashCode() {
        int result = id_translation_language.hashCode();
        result = 31 * result + language_code.hashCode();
        result = 31 * result + language_name.hashCode();
        result = 31 * result + last_update.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TranslationLanguageDB{" +
                "id_translation_language=" + id_translation_language +
                ", language_code='" + language_code + '\'' +
                ", language_name='" + language_name + '\'' +
                ", last_update=" + last_update +
                '}';
    }
}
