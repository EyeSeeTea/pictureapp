package org.eyeseetea.malariacare.data.database.migrations;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB_Table;

@Migration(version = 20, database = AppDatabase.class)
public class Migration20CopyValuesOnVoucherColumn extends BaseMigration {

    @Override
    public void migrate(DatabaseWrapper database) {
        moveEventUidToVoucherUid(database);
    }


    private void moveEventUidToVoucherUid(DatabaseWrapper database) {
        String sql =
                "UPDATE Survey set voucher_uid = uid_event_fk; ";

        database.execSQL(sql);
    }
}
