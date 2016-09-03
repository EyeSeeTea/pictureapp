package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

import java.io.IOException;

/**
 * Created by idelcano on 01/09/2016.
 */
@Migration(version = 9, databaseName = AppDatabase.NAME)
public class Migration9AddOptionPadding extends BaseMigration {

    private static String TAG=".Migration8";

    private static Migration9AddOptionPadding instance;
    private boolean postMigrationRequired;

    public Migration9AddOptionPadding() {
        super();
        instance = this;
        postMigrationRequired=false;
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        postMigrationRequired=true;
        MigrationTools.addColumn(database, OptionAttribute.class, "text_margin", "String");
        MigrationTools.addColumn(database, OptionAttribute.class, "image_margin", "String");
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
                PopulateDB.updateOptionAttributes(PreferencesState.getInstance().getContext().getAssets());
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