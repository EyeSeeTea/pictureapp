package org.eyeseetea.malariacare.data.database.migrations;


import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;

@Migration(version = 24, database = AppDatabase.class)
public class Migration24AddColumnVisibleVoucherUId extends AlterTableMigration<SurveyDB> {

    public Migration24AddColumnVisibleVoucherUId(Class<SurveyDB> table) {
        super(table);

        addColumn(SQLiteType.TEXT, "visible_voucher_uid");
    }

}
