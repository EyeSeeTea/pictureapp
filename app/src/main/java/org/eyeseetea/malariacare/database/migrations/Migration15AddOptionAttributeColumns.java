package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

import java.io.IOException;

/**
 * Created by idelcano on 14/06/2016.
 */
@Migration(version = 15, databaseName = AppDatabase.NAME)
public class Migration15AddOptionAttributeColumns extends BaseMigration {

    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";
    private static String TAG = ".Migration5";
    private static Migration15AddOptionAttributeColumns instance;
    private boolean postMigrationRequired;

    public Migration15AddOptionAttributeColumns() {
        super();
        instance = this;
        postMigrationRequired = false;
    }

    public static void addColumn(SQLiteDatabase database, Class model, String columnName,
            String type) {
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        database.execSQL(
                String.format(ALTER_TABLE_ADD_COLUMN, myAdapter.getTableName(), columnName, type));
    }

    public static void postMigrate() {
        //Migration NOT required -> done
        Log.d(TAG, "Post migrate");
        if (!instance.postMigrationRequired) {
            return;
        }


        //this migration is moved to last migration
        //Data? Add new default data
        if (instance.hasData()) {
            try {
                PopulateDB.addOptionAttributes(
                        PreferencesState.getInstance().getContext().getAssets());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //This operation wont be done again
        instance.postMigrationRequired = false;
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        postMigrationRequired = false;
        addColumn(database, OptionAttribute.class, "default_option", "Integer");
    }

    @Override
    public void onPostMigrate() {
    }

    /**
     * Checks if the current db has data or not
     */
    private boolean hasData() {
        return Program.getFirstProgram() != null;
    }
}