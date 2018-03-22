package org.eyeseetea.malariacare.database.migrations;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.utils.populatedb.TreatmentTable;

import java.io.IOException;


@Migration(version = 3, database = AppDatabase.class)
public class Migration3ChangeTreatmentTable extends BaseMigration {
    private static String TAG = ".Migration3";
    private static Migration3ChangeTreatmentTable instance;
    private boolean postMigrationRequired;

    public Migration3ChangeTreatmentTable() {
        super();
        instance = this;
        postMigrationRequired = false;
    }

    @Override
    public void migrate(DatabaseWrapper database) {
        postMigrationRequired = true;
    }

    public static void postMigrate() {
        //Migration NOT required -> done
        Log.d(TAG, "Post migrate");
        if (!instance.postMigrationRequired) {
            return;
        }
        TreatmentTable treatmentTable = new TreatmentTable();
        try {
            treatmentTable.generateTreatmentMatrix();
        } catch (IOException e) {
            Log.e(TAG, "Error generating treatment Matrix " + e.getMessage());
            e.printStackTrace();
        }
        instance.postMigrationRequired = false;
    }

    @Override
    public void onPostMigrate() {
        postMigrationRequired = true;
    }

    private boolean hasData() {
        return Program.getFirstProgram() != null;
    }

}
