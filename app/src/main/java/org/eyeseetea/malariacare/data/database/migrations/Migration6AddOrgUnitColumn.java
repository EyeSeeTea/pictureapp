package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;

@Migration(version = 6, database = AppDatabase.class)
public class Migration6AddOrgUnitColumn extends BaseMigration {

    public Migration6AddOrgUnitColumn() {
    }

    @Override
    public void migrate(DatabaseWrapper database) {
        MigrationTools.addColumn(database, OrgUnit.class, "is_banned", "boolean");
    }

    public static void postMigrate() {
    }
}
