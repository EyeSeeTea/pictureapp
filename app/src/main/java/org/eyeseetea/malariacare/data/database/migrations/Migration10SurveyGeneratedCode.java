package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;

@Migration(version = 10, database = AppDatabase.class)
public class Migration10SurveyGeneratedCode extends AlterTableMigration<SurveyDB> {
    public Migration10SurveyGeneratedCode(
            Class<SurveyDB> table) {
        super(table);
        addColumn(SQLiteType.TEXT, "ws_generated_code");
    }
}
