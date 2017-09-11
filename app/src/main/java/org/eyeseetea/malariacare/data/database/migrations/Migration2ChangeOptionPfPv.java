package org.eyeseetea.malariacare.data.database.migrations;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.PostMigrationException;

import java.io.IOException;

@Migration(version = 2, database = AppDatabase.class)
public class Migration2ChangeOptionPfPv extends BaseMigration {

    private static String TAG = ".Migration2";

    private static Migration2ChangeOptionPfPv instance;
    private boolean postMigrationRequired;

    public Migration2ChangeOptionPfPv() {
        instance = this;
        postMigrationRequired = false;
    }

    public static void postMigrate() throws PostMigrationException{
        //Migration NOT required -> done
        Log.d(TAG, "Post migrate");
        if (!instance.postMigrationRequired) {
            return;
        }
        //Data? Add new default data
        if (instance.hasData()) {
            try {
                UpdateDBStrategy.updateOptions(PreferencesState.getInstance().getContext());
            } catch (IOException e) {
                throw new PostMigrationException(e);
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
        return Option.getAllOptions().size() > 0;
    }
}
