package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Value;

import static org.eyeseetea.malariacare.database.migrations.MigrationUtils.addColumn;

/**
 * Created by idelcano on 12/08/2016.
 */
@Migration(version = 9, databaseName = AppDatabase.NAME)
public class Migration9AddConflictValue extends BaseMigration {

    public Migration9AddConflictValue() {
        super();
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        addColumn(database, Value.class, "conflict", "boolean");
    }

    @Override
    public void onPostMigrate() {
    }

}