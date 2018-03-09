package org.eyeseetea.malariacare.data.database.migrations;


import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;

@Migration(version = 11, priority = 3, database = AppDatabase.class)
public class Migration11UpdateTreatment extends BaseMigration {


    @Override
    public void migrate(DatabaseWrapper database) {

        addTableTreatmentTemp(database);

        insertRecordsFromTreatmentToTreatmentTemp(database);

        dropTableTreatment(database);

        renameTableTreatmentTempToTreatment(database);
    }

    // language="RoomSql"
    private void addTableTreatmentTemp(DatabaseWrapper database) {
        String sql =
                "CREATE TABLE TreatmentTemp("
                        + "id_treatment INTEGER PRIMARY KEY, "
                        + "id_partner_fk INTEGER, "
                        + "diagnosis TEXT, "
                        + "message TEXT, "
                        + "type INTEGER); ";

        database.execSQL(sql);

    }

    // language="RoomSql"
    private void insertRecordsFromTreatmentToTreatmentTemp(DatabaseWrapper database) {
        String sql =
                "INSERT INTO TreatmentTemp("
                        + "id_treatment, "
                        + "id_partner_fk, "
                        + "diagnosis, "
                        + "message, "
                        + "type) " +
                        "SELECT id_treatment,id_partner_fk, "
                        + "(SELECT StringKey.key FROM StringKey WHERE id_string_key = diagnosis), "
                        + "(SELECT StringKey.key FROM StringKey WHERE id_string_key = message), "
                        + "type "
                        + "FROM Treatment; ";

        database.execSQL(sql);

    }

    // language="RoomSql"
    private void dropTableTreatment(DatabaseWrapper database) {
        String sql =
                "DROP TABLE IF EXISTS Treatment;";

        database.execSQL(sql);
    }

    // language="RoomSql"
    private void renameTableTreatmentTempToTreatment(DatabaseWrapper database) {
        String sql =
                "ALTER TABLE TreatmentTemp RENAME TO Treatment;";

        database.execSQL(sql);
    }

}
