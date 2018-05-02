package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;

@Migration(version = 16, database = AppDatabase.class)
public class Migration16DropAllTables extends BaseMigration {
    @Override
    public void migrate(DatabaseWrapper database) {

        dropAndRecreateDBSchema(database);

    }

    private void dropAndRecreateDBSchema(DatabaseWrapper database) {
        for (Class<? extends Model> s : PopulateDB.allTables) {
            ModelAdapter modelAdapter = FlowManager.getModelAdapter(s);
            String tableName = modelAdapter.getTableName();
            database.execSQL("DROP TABLE IF EXISTS " + tableName);
            database.execSQL(modelAdapter.getCreationQuery());
        }
    }
}
