/*package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.database.AppDatabase;

import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.database.utils.populatedb.UpdateDB;

import java.io.IOException;

@Migration(version = 23, databaseName = AppDatabase.NAME)
public class Migration23ModifyValuesLastMigration extends BaseMigration {

    private static String TAG = ".Migration21";

    private static Migration23ModifyValuesLastMigration instance;
    private boolean postMigrationRequired;

    public Migration23ModifyValuesLastMigration() {
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
            try {
                PopulateDB.addOptionAttributes(
                        PreferencesState.getInstance().getContext());
                UpdateDB.updateAnswers(PreferencesState.getInstance().getContext());
                UpdateDB.updateOptions(
                        PreferencesState.getInstance().getContext());
                UpdateDB.updateAndAddQuestions(
                        PreferencesState.getInstance().getContext());
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