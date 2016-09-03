package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

import java.io.IOException;

/**
 * Created by idelcano on 14/06/2016.
 */
@Migration(version = 6, databaseName = AppDatabase.NAME)
public class Migration6AddOptionTextSizeColumn extends BaseMigration {

    private static String TAG=".Migration6";

    private static Migration6AddOptionTextSizeColumn instance;

    public Migration6AddOptionTextSizeColumn() {
        super();
        instance = this;
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        MigrationTools.addColumn(database, OptionAttribute.class, "text_size", "Integer");
    }

    @Override
    public void onPostMigrate() {
    }
}