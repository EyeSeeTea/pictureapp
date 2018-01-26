package org.eyeseetea.malariacare.data.database.model;


import static org.eyeseetea.malariacare.data.database.model.TranslationDB
        .INDEX_NAME_STRING_KEY_AND_LANGUAGE_CODE;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Index;
import com.raizlabs.android.dbflow.annotation.IndexGroup;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;


@Table(database = AppDatabase.class, name = "Translation",
        indexGroups = {@IndexGroup(number = 1,
                name = INDEX_NAME_STRING_KEY_AND_LANGUAGE_CODE)
        })
public class TranslationDB extends BaseModel {

    public static final String TRANSLATION_NOT_FOUND = "-1";
    public static final String INDEX_NAME_STRING_KEY_AND_LANGUAGE_CODE =
            "TranslationIndexStringKeyAndLanguageCode";

    @Column
    @PrimaryKey(autoincrement = true)
    Long id_translation;
    @Index(indexGroups = 1)
    @Column
    String string_key;
    @Column
    String translation;
    @Column
    @Index(indexGroups = 1)
    String language_code;

    public TranslationDB() {
    }

    public TranslationDB(Long id_translation, String string_key, String translation,
            String language_code) {
        this.id_translation = id_translation;
        this.string_key = string_key;
        this.translation = translation;
        this.language_code = language_code;
    }

    public static List<TranslationDB> getAllTranslations() {
        return new Select()
                .from(TranslationDB.class)
                .queryList();
    }

    public Long getId_translation() {
        return id_translation;
    }

    public void setId_translation(Long id_translation) {
        this.id_translation = id_translation;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }


    private static TranslationDB getTranslationByKey(String stringKey, String languageCode) {
        return new Select()
                .from(TranslationDB.class)
                .indexedBy(TranslationDB_Table
                        .index_TranslationIndexStringKeyAndLanguageCode)
                .where(TranslationDB_Table.string_key.is(stringKey))
                .and(TranslationDB_Table.language_code.is(languageCode))
                .querySingle();
    }

    private static boolean isTranslationEmptyOrNull(TranslationDB trans) {
        return isTranslationNull(trans)
                || trans.getTranslation().isEmpty();
    }

    private static boolean isTranslationNull(TranslationDB trans) {
        return trans == null || trans.getTranslation() == null;
    }

    public static String getLocalizedStringFromDB(String string_key, String language_code,
            String defaultLanguage) {

        TranslationDB translationDB = getTranslationByKey(string_key, language_code);

        if (isTranslationEmptyOrNull(translationDB)) {
            String generalLanguage = language_code.split("_")[0];

            translationDB = getTranslationByKey(string_key, generalLanguage);

            if (isTranslationEmptyOrNull(translationDB)) {
                translationDB = getTranslationByKey(string_key, defaultLanguage);
            }
        }

        return !isTranslationNull(translationDB)
                ? translationDB.getTranslation() : TRANSLATION_NOT_FOUND;
    }

    public static boolean wasTranslationFound(String translation){
        return !TRANSLATION_NOT_FOUND.equalsIgnoreCase(translation);
    }

    public String getString_key() {
        return string_key;
    }

    public void setString_key(String string_key) {
        this.string_key = string_key;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranslationDB that = (TranslationDB) o;

        if (id_translation != null ? !id_translation.equals(that.id_translation)
                : that.id_translation != null) {
            return false;
        }
        if (string_key != null ? !string_key.equals(that.string_key) : that.string_key != null) {
            return false;
        }
        if (translation != null ? !translation.equals(that.translation)
                : that.translation != null) {
            return false;
        }
        return language_code != null ? language_code.equals(that.language_code)
                : that.language_code == null;
    }

    @Override
    public int hashCode() {
        int result = id_translation != null ? id_translation.hashCode() : 0;
        result = 31 * result + (string_key != null ? string_key.hashCode() : 0);
        result = 31 * result + (translation != null ? translation.hashCode() : 0);
        result = 31 * result + (language_code != null ? language_code.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TranslationDB{" +
                "id_translation=" + id_translation +
                ", string_key='" + string_key + '\'' +
                ", translation='" + translation + '\'' +
                ", language_code='" + language_code + '\'' +
                '}';
    }
}
