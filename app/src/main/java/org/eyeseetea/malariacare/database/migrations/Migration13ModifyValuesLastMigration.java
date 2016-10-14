package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

import java.io.IOException;

/**
 * Created by idelcano on 03/08/2016.
 */
@Migration(version = 13, databaseName = AppDatabase.NAME)
public class Migration13ModifyValuesLastMigration extends BaseMigration {

    private static String TAG=".Migration13";

    private static Migration13ModifyValuesLastMigration instance;
    private boolean postMigrationRequired;

    public Migration13ModifyValuesLastMigration() {
        super();
        instance = this;
        postMigrationRequired=false;
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        postMigrationRequired=true;
    }

    @Override
    public void onPostMigrate() {
    }


    public static void postMigrate(){
        //Migration NOT required -> done
        Log.d(TAG,"Post migrate");
        if(!instance.postMigrationRequired){
            return;
        }


        //Data? Add new default data
        if(instance.hasData()) {
            try {
                PopulateDB.addOptionAttributes(PreferencesState.getInstance().getContext().getAssets());
                PopulateDB.updateOptionNames(PreferencesState.getInstance().getContext().getAssets());
                PopulateDB.updateQuestions(PreferencesState.getInstance().getContext().getAssets());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //This operation wont be done again
        instance.postMigrationRequired=false;
    }

    /**
     * Checks if the current db has data or not
     * @return
     */
    private boolean hasData() {
        return Program.getFirstProgram()!=null;
    }
}