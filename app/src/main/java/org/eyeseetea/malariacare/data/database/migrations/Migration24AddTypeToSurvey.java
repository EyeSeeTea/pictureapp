package org.eyeseetea.malariacare.data.database.migrations;

/*
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Survey;

@Migration(version = 24, databaseName = AppDatabase.NAME)
public class Migration24AddTypeToSurvey extends BaseMigration {
    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";
    private static String TAG = ".Migration23";

    @Override
    public void migrate(SQLiteDatabase database) {
        addColumn(database, Survey.class,"type","Integer");
    }

    public static void addColumn(SQLiteDatabase database, Class model, String columnName,
            String type) {
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        database.execSQL(
                String.format(ALTER_TABLE_ADD_COLUMN, myAdapter.getTableName(), columnName, type));
    }
}
*/
