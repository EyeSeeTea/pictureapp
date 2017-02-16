/*
package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;

@Migration(version = 21, database = AppDatabase.class)
public class Migration21LaoNewRelation extends BaseMigration {

    private static String TAG = ".Migration21 only lao";

    private static Migration21LaoNewRelation instance;
    private boolean postMigrationRequired;

    public Migration21LaoNewRelation() {
        super();
        instance = this;
        postMigrationRequired = false;
    }

    public static void postMigrate() {
        //Migration NOT required -> done
        Log.d(TAG, "Post migrate");
        if (!instance.postMigrationRequired) {
            return;
        }


        //Data? Add new default data
        if (instance.hasData()) {
            PopulateDB.createMissingRelationInLao();
        }

        //This operation wont be done again
        instance.postMigrationRequired = false;
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        postMigrationRequired = true;
    }

    @Override
    public void onPostMigrate() {
    }

    */
/**
     * Checks if the current db has data or not
 *//*

    private boolean hasData() {
        return Program.getFirstProgram() != null;
    }

}*/