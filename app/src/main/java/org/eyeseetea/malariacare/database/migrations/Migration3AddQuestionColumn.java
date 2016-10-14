package org.eyeseetea.malariacare.database.migrations;

import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.opencsv.CSVReader;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.CompositeScore;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.OrgUnitLevel;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.Score;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.TabGroup;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.utils.Constants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by idelcano on 10/06/2016.
 */
@Migration(version = 3, databaseName = AppDatabase.NAME)
public class Migration3AddQuestionColumn extends BaseMigration {

    private static String TAG=".Migration3";
    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";

    private static Migration3AddQuestionColumn instance;
    private boolean postMigrationRequired;

    public Migration3AddQuestionColumn() {
        super();
        instance = this;
        postMigrationRequired=false;
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        postMigrationRequired=true;
        addColumn(database, Question.class, "total_questions", "Integer");
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

        //this migration is moved to last migration

        //Data? Add new default data
        /*if(instance.hasData()) {
            List<Question> questions = Question.getAllQuestions();
            try {
                PopulateDB.addTotalQuestions(PreferencesState.getInstance().getContext().getAssets(), questions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

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