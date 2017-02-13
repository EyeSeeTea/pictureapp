package org.eyeseetea.malariacare.database.migrations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.populatedb.UpdateDB;

import java.io.IOException;

@Migration(version = 25, databaseName = AppDatabase.NAME)
public class Migration25PutDoseDrugCombination extends BaseMigration {
    private static String TAG = ".Migration25";

    private static Migration25PutDoseDrugCombination instance;
    private boolean postMigrationRequired;

    public Migration25PutDoseDrugCombination() {
        instance = this;
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
                Context context = PreferencesState.getInstance().getContext();
                UpdateDB.updateDrugs(context);
                UpdateDB.updateDrugCombination(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //This operation wont be done again
        instance.postMigrationRequired = true;
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        MigrationTools.addColumn(database, Survey.class, "dose", "Real");
    }

    /**
     * Checks if the current db has data or not
     */
    private boolean hasData() {
        return Program.getFirstProgram() != null;
    }
}
