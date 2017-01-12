package org.eyeseetea.malariacare.database.migrations;

import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.database.utils.populatedb.UpdateDB;

import java.io.IOException;

/**
 * Created by manuel on 3/01/17.
 */
@Migration(version = 22, databaseName = AppDatabase.NAME)
public class Migration22AddStockCsvs extends BaseMigration {
    private static String TAG = ".Migration22";
    private static Migration22AddStockCsvs instance;
    private boolean postMigrationRequired;

    public Migration22AddStockCsvs() {
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
                AssetManager assetManager = PreferencesState.getInstance().getContext().getAssets();
                UpdateDB.updateAnswers(assetManager);
                UpdateDB.updatePrograms(assetManager);
                UpdateDB.updateTabs(assetManager);
                UpdateDB.updateHeaders(assetManager);
                UpdateDB.updateAndAddQuestions(assetManager);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error updating database" + e.getMessage());
            }
        }

        //This operation wont be done again
        instance.postMigrationRequired = false;
    }


    @Override
    public void migrate(SQLiteDatabase database) {
        postMigrationRequired = true;
    }

    @Override
    public void onPostMigrate() {
        postMigrationRequired = true;
    }

    /**
     * Checks if the current db has data or not
     */
    private boolean hasData() {
        return Program.getFirstProgram() != null;
    }

}
