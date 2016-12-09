package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;

/**
 * Created by idelcano on 10/06/2016.
 */
@Migration(version = 18, databaseName = AppDatabase.NAME)
public class Migration18AddQuestionCompulsoryColumn extends BaseMigration {

    private static String TAG=".Migration18";
    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";

    private static Migration18AddQuestionCompulsoryColumn instance;
    private boolean postMigrationRequired;

    public Migration18AddQuestionCompulsoryColumn() {
        super();
        instance = this;
        postMigrationRequired=false;
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        postMigrationRequired=true;
        addColumn(database, Question.class, "compulsory", "Integer");
    }

    @Override
    public void onPostMigrate() {
    }

    public static void addColumn(SQLiteDatabase database, Class model, String columnName, String type) {
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        database.execSQL(String.format(ALTER_TABLE_ADD_COLUMN, myAdapter.getTableName(), columnName, type));
    }


    public static void postMigrate(){
        //Migration NOT required -> done
        Log.d(TAG,"Post migrate");
        if(!instance.postMigrationRequired){
            return;
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