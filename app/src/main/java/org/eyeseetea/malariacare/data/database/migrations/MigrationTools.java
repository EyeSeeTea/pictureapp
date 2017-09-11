package org.eyeseetea.malariacare.data.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

/**
 * Created by idelcano on 25/11/2016.
 */

public class MigrationTools {

    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";

    public static void addColumn(DatabaseWrapper database, Class model, String columnName,
            String type) {
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        database.execSQL(
                String.format(ALTER_TABLE_ADD_COLUMN, myAdapter.getTableName(), columnName, type));
    }
}
