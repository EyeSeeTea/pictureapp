package org.eyeseetea.malariacare.data.database.migrations;


import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.TranslationDB_Table;

@Migration(version = 11, priority = 2, database = AppDatabase.class)
public class Migration11AddTranslationLanguage extends BaseMigration {


    @Override
    public void migrate(DatabaseWrapper database) {

        addTableTranslationTemp(database);

        insertRecordsFromTableTranslationToTranslationTemp(database);

        dropTableTranslation(database);

        renameTableTranslationTempToTranslation(database);

        addIndexToTableTranslation(database);
    }

    // language="RoomSql"
    private void addTableTranslationTemp(DatabaseWrapper database) {
        String sql =
                "CREATE TABLE TranslationTemp("
                        + "id_translation INTEGER PRIMARY KEY, "
                        + "string_key TEXT, "
                        + "language_code TEXT, "
                        + "translation TEXT); ";

        database.execSQL(sql);

    }

    // language="RoomSql"
    private void insertRecordsFromTableTranslationToTranslationTemp(DatabaseWrapper database) {
        String sql =
                "INSERT INTO TranslationTemp("
                        + "id_translation, "
                        + "string_key, "
                        + "language_code, "
                        + "translation) "
                        + "SELECT id_translation,StringKey.key, "
                        + "language,translation "
                        + "FROM Translation "
                        + "INNER JOIN StringKey "
                        + "ON StringKey.id_string_key = Translation.id_string_key; ";

        database.execSQL(sql);

    }

    // language="RoomSql"
    private void dropTableTranslation(DatabaseWrapper database) {
        String sql =
                "DROP TABLE IF EXISTS Translation;";

        database.execSQL(sql);
    }

    // language="RoomSql"
    private void renameTableTranslationTempToTranslation(DatabaseWrapper database) {
        String sql =
                "ALTER TABLE TranslationTemp RENAME TO Translation;";

        database.execSQL(sql);
    }

    // language="RoomSql"
    private void addIndexToTableTranslation(DatabaseWrapper database) {
        TranslationDB_Table
                .index_TranslationIndexStringKeyAndLanguageCode
                .createIfNotExists(database);
    }

}
