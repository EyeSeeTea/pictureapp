package org.eyeseetea.malariacare.data.database.migrations;

/*
import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.UserDB;

*/
/**
 * Created by manuel on 2/01/17.
 *//*

@Migration(version = 22, databaseName = AppDatabase.NAME)
public class Migration22TreatmentMatrix extends BaseMigration {

    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";
    private static String TAG = ".Migration22";

    public static void addColumn(SQLiteDatabase database, Class model, String columnName,
            String type) {
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        database.execSQL(
                String.format(ALTER_TABLE_ADD_COLUMN, myAdapter.getTableName(), columnName, type));
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        addColumn(database, UserDB.class, "organisation", "Integer");
        addColumn(database, UserDB.class, "supervisor", "Integer");
    }

}
*/
