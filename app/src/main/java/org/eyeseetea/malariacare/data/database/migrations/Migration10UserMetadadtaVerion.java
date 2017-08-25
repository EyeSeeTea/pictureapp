package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.UserDB;

@Migration(version = 10, database = AppDatabase.class)
public class Migration10UserMetadadtaVerion extends AlterTableMigration<UserDB> {
    public Migration10UserMetadadtaVerion(
            Class<UserDB> table) {
        super(table);
        addColumn(SQLiteType.TEXT, "metadataVersion");
    }
}
