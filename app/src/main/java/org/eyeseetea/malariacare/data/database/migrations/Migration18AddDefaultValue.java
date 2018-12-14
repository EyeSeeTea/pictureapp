package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;

@Migration(version = 18, database = AppDatabase.class)
public class Migration18AddDefaultValue extends AlterTableMigration<QuestionDB> {

    public Migration18AddDefaultValue(
            Class<QuestionDB> table) {
        super(table);
        addColumn(SQLiteType.TEXT, "defaultValue");
    }
}
