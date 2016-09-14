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
 * Created by idelcano on 14/06/2016.
 */
@Migration(version = 5, databaseName = AppDatabase.NAME)
public class Migration5AddOptionAttributeColumns extends BaseMigration {

    private static String TAG=".Migration5";

    private static Migration5AddOptionAttributeColumns instance;
    private boolean postMigrationRequired;

    public Migration5AddOptionAttributeColumns() {
        super();
        instance = this;
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        MigrationTools.addColumn(database, OptionAttribute.class, "horizontal_alignment", "Integer");
        MigrationTools.addColumn(database, OptionAttribute.class, "vertical_alignment", "Integer");
    }

    @Override
    public void onPostMigrate() {
    }
}