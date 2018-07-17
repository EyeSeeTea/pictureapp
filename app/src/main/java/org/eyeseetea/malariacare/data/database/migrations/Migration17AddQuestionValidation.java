package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;

@Migration(version = 17, database = AppDatabase.class)
public class Migration17AddQuestionValidation extends AlterTableMigration<QuestionDB> {

    public Migration17AddQuestionValidation(
            Class<QuestionDB> table) {
        super(table);
        addColumn(SQLiteType.TEXT, "validationRegExp");
        addColumn(SQLiteType.TEXT, "validationMessage");
    }
}
