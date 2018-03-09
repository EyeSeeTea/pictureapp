package org.eyeseetea.malariacare.data.database.migrations;


import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;

@Migration(version = 13, database = AppDatabase.class)
public class Migration12AddColumnReferenceToCountryDB extends
        AlterTableMigration<CountryVersionDB> {


    public Migration12AddColumnReferenceToCountryDB(Class<CountryVersionDB> table) {
        super(table);
        addColumn(SQLiteType.TEXT, "reference");
    }

}
