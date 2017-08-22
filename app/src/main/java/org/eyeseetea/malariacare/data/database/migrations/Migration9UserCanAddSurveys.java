package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.UserDB;

@Migration(version = 9, database = AppDatabase.class)
public class Migration9UserCanAddSurveys extends AlterTableMigration<UserDB> {
    public Migration9UserCanAddSurveys(
            Class<UserDB> table) {
        super(table);
        addColumn(SQLiteType.INTEGER, "canAddSurveys");
    }
}
