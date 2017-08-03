package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.UserDB;

@Migration(version = 5, database = AppDatabase.class)
public class Migration5UserAnnoucement extends AlterTableMigration<UserDB> {

    public Migration5UserAnnoucement(
            Class<UserDB> table) {
        super(table);
        addColumn(SQLiteType.TEXT, "announcement");
        addColumn(SQLiteType.INTEGER, "close_date");
        addColumn(SQLiteType.INTEGER, "last_updated");
    }
}
