/*
package org.eyeseetea.malariacare.data.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.Treatment;


@Migration(version = 27, database = AppDatabase.class)
public class Migration27ChangeTypeDiagnossisMessage extends BaseMigration {

    @Override
    public void migrate(DatabaseWrapper database) {
        database.execSQL("DROP TABLE IF EXISTS Treatment");
        ModelAdapter myAdapter = FlowManager.getModelAdapter(Treatment.class);
        String sql = myAdapter.getCreationQuery();
        database.execSQL(sql);
    }
}
*/
