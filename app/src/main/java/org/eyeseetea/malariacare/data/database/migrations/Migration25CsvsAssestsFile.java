/*
package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.utils.populatedb.FileCsvs;

import java.io.IOException;

@Migration(version = 25, databaseName = AppDatabase.NAME)
public class Migration25CsvsAssestsFile extends BaseMigration {
    private static String TAG = ".Migration25";

    @Override
    public void migrate(SQLiteDatabase database) {
        FileCsvs fileCsvs = new FileCsvs();
        try {
            fileCsvs.saveCsvsInFileIfNeeded();
        } catch (IOException e) {
            Log.e(TAG, "Saving csvs in file error:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
*/
