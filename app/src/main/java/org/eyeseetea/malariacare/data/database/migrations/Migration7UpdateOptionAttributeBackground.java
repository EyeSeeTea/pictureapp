package org.eyeseetea.malariacare.data.database.migrations;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.UpdateDB;
import org.eyeseetea.malariacare.domain.exception.PostMigrationException;

import java.io.IOException;
import java.util.List;

@Migration(version = 7, database = AppDatabase.class)
public class Migration7UpdateOptionAttributeBackground extends BaseMigration {

    private static String TAG = ".Migration7";

    private static Migration7UpdateOptionAttributeBackground instance;
    private boolean postMigrationRequired;

    public Migration7UpdateOptionAttributeBackground() {
        instance = this;
        postMigrationRequired = false;
    }

    public static void postMigrate() throws PostMigrationException {
        //Migration NOT required -> done
        Log.d(TAG, "Post migrate");
        if (!instance.postMigrationRequired) {
            return;
        }
        //Data? Add new default data
        if (instance.hasData()) {
            try {
                UpdateDB.updateOptionAttributes(PreferencesState.getInstance().getContext());
            } catch (IOException e) {
                throw new PostMigrationException(e);
            }
        }

        Log.d(TAG, "Post migrate finish");
        //This operation wont be done again
        instance.postMigrationRequired = false;
    }


    @Override
    public void migrate(DatabaseWrapper database) {
        Log.d(TAG, "Migration of "+TAG);
        postMigrationRequired = true;
    }


    private boolean hasData() {
        List<Question> questions = Question.getAllQuestions();

        return questions != null && questions.size() > 0;
    }
}
