package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Treatment;

@Migration(version = 27, databaseName = AppDatabase.NAME)
public class Migration27ChangeTypeDiagnossisMessage extends BaseMigration {
    @Override
    public void migrate(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS Treatment");
        ModelAdapter myAdapter = FlowManager.getModelAdapter(Treatment.class);
        String sql = myAdapter.getCreationQuery();
        database.execSQL(sql);
    }
}
