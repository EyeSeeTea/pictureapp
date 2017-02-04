package org.eyeseetea.malariacare.database.migrations;

import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.populatedb.UpdateDB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manuel on 10/01/17.
 */
@Migration(version = 23, databaseName = AppDatabase.NAME)
public class Migration23AddTreatmentCsvs extends BaseMigration {
    private static String TAG = ".Migration23";
    private static Migration23AddTreatmentCsvs instance;
    private boolean postMigrationRequired;

    public Migration23AddTreatmentCsvs() {
        super();
        instance = this;
        postMigrationRequired = false;
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        postMigrationRequired = true;
    }

    public static void postMigrate() {
        if (!instance.postMigrationRequired) {
            return;
        }
        Log.d(TAG, "Post Migrate");
        if (instance.hasData()) {
            try {
                List<String> tabsToDelete = new ArrayList<>();
                tabsToDelete.add("drugs_referral");
                Tab.deleteTab(tabsToDelete);
                AssetManager assetManager = PreferencesState.getInstance().getContext().getAssets();
                UpdateDB.updateTabs(assetManager);
                UpdateDB.updateHeaders(assetManager);
                UpdateDB.updateAnswers(assetManager);
                UpdateDB.updateAndAddQuestions(assetManager);
                UpdateDB.updateQuestionRelation(assetManager);
                UpdateDB.updateMatches(assetManager);
                UpdateDB.updateQuestionOption(assetManager);
                UpdateDB.updateQuestionThresholds(assetManager);
                UpdateDB.updateDrugs(assetManager);
                UpdateDB.updateOrganisations(assetManager);
                UpdateDB.updateTreatments(assetManager);
                UpdateDB.updateDrugCombination(assetManager);
                UpdateDB.updateTreatmentMatches(assetManager);
                UpdateDB.updateQuestionOption(assetManager);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Error updating database" + e.getMessage());
            }
        }

    }

    /**
     * Checks if the current db has data or not
     */
    private boolean hasData() {
        return Program.getFirstProgram() != null;
    }


}
