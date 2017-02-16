/*
package org.eyeseetea.malariacare.data.database.migrations;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;


import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.Drug;
import org.eyeseetea.malariacare.data.database.model.DrugCombination;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.UpdateDB;

import java.io.IOException;
import java.util.List;

@Migration(version = 26, database = AppDatabase.class)
public class Migration26PutDoseDrugCombination extends BaseMigration {
    private static String TAG = ".Migration25";

    private static Migration26PutDoseDrugCombination instance;
    private boolean postMigrationRequired;

    public Migration26PutDoseDrugCombination() {
        instance = this;
    }

    @Override
    public void migrate(DatabaseWrapper database) {
//        MigrationTools.addColumn(database, DrugCombination.class, "dose", "Real");
        postMigrationRequired = true;
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
                List<Drug> drugs = Drug.getAllDrugs();
                for (Drug drug : drugs) {
                    drug.delete();
                }
                List<DrugCombination> drugCombinations=DrugCombination.getAllDrugCombination();
                for(DrugCombination drugCombination:drugCombinations){
                    drugCombination.delete();
                }
                Context context = PreferencesState.getInstance().getContext();
                UpdateDB.updateDrugs(context);
                UpdateDB.updateDrugCombination(context, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //This operation wont be done again
        instance.postMigrationRequired = false;
    }

    */
/**
     * Checks if the current db has data or not
     *//*

    private boolean hasData() {
        return Program.getFirstProgram() != null;
    }
}
*/
