package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;

@Migration(version = 19, database = AppDatabase.class)
public class Migration19AddVoucherUidColumn extends AlterTableMigration<SurveyDB> {

    public Migration19AddVoucherUidColumn(
            Class<SurveyDB> table) {
        super(table);
        addColumn(SQLiteType.TEXT, "voucher_uid");
    }
}
