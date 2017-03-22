package org.eyeseetea.malariacare.data.database.migrations;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.UpdateDB;

import java.io.IOException;
import java.util.List;

@Migration(version = 3, database = AppDatabase.class)
public class Migration3RefactorHelptext extends BaseMigration {
    private static String TAG = ".Migration3";

    private static Migration3RefactorHelptext instance;
    private boolean postMigrationRequired;

    public Migration3RefactorHelptext() {
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
                UpdateDB.updateAndAddQuestions(PreferencesState.getInstance().getContext());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //This operation wont be done again
        instance.postMigrationRequired = false;
    }


    @Override
    public void migrate(DatabaseWrapper database) {
        postMigrationRequired = true;
    }


    private boolean hasData() {
        List<Question> questions = Question.getAllQuestions();

        return questions != null && questions.size() > 0;
    }
}
