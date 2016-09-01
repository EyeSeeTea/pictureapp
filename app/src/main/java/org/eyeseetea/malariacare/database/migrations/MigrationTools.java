package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

/**
 * Created by idelcano on 01/09/2016.
 */
public class MigrationTools {

    private static final String TAG="MigrationUtils";


    public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";
    public static final String UPDATE_COLUMN = "UPDATE %s SET %s =\"%s\"";

    public static void addColumn(SQLiteDatabase database, Class model, String columnName,String type){
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        database.execSQL(String.format(ALTER_TABLE_ADD_COLUMN, myAdapter.getTableName(), columnName, type));

    }

    public static void updateColumn(SQLiteDatabase database, Class model, String columnName,String value){
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        Log.d(TAG, String.format(UPDATE_COLUMN, myAdapter.getTableName(), columnName, value));
        database.execSQL(String.format(UPDATE_COLUMN, myAdapter.getTableName(),columnName,value) );
    }

    public static void recreateTables(SQLiteDatabase database,Class[] tables){
        for(int i=0;i<tables.length;i++){
            ModelAdapter myAdapter = FlowManager.getModelAdapter(tables[i]);
            database.execSQL(DROP_TABLE_IF_EXISTS + myAdapter.getTableName());
            database.execSQL(myAdapter.getCreationQuery());
        }
    }

}
