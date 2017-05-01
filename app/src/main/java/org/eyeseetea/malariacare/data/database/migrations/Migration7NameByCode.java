package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;

@Migration(version = 7, database = AppDatabase.class)
public class Migration7NameByCode extends BaseMigration {
    private static String TAG = ".Migration5";

    @Override
    public void migrate(DatabaseWrapper database) {
        String sqlOptionTemp =
                "CREATE TABLE OptionTemp(id_option INTEGER PRIMARY KEY, code TEXT, name TEXT, "
                        + "factor REAL, id_answer_fk INTEGER, id_option_attribute_fk INTEGER)";
        database.execSQL(sqlOptionTemp);

        String sqlCopyOption =
                "INSERT INTO OptionTemp(id_option, code, name, factor, id_answer_fk, "
                        + "id_option_attribute_fk) "
                        + "SELECT id_option, name, code, factor, id_answer_fk, "
                        + "id_option_attribute_fk "
                        + "FROM Option;";
        database.execSQL(sqlCopyOption);

        String sqlDeleteOption = "DROP TABLE Option";
        database.execSQL(sqlDeleteOption);

        String sqlRenameOptionTemp = "ALTER TABLE OptionTemp RENAME TO Option";
        database.execSQL(sqlRenameOptionTemp);
    }
}
