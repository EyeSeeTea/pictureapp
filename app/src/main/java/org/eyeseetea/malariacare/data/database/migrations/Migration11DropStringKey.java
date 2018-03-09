package org.eyeseetea.malariacare.data.database.migrations;


import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;


@Migration(version = 11, priority = 1, database = AppDatabase.class)
public class Migration11DropStringKey extends BaseMigration {

    @Override
    public void migrate(DatabaseWrapper database) {

        dropTableStringKey(database);

    }

    // language="RoomSql"
    private void dropTableStringKey(DatabaseWrapper database) {
        String sql =
                "DROP TABLE IF EXISTS StringKey;";

        database.execSQL(sql);
    }

}
