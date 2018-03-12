package org.eyeseetea.malariacare.data.database.migrations;


import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.language.property.IndexProperty;
import com.raizlabs.android.dbflow.sql.migration.IndexPropertyMigration;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.TranslationDB_Table;

/* Because DBFlow doesn't create a index without adding a migration
*  new users won't have this index unless this migration runs.
*  For this reason this migration is version 0 even thought
*  the index, was added in the migration 11.
*  More info: https://github.com/Raizlabs/DBFlow/issues/687
* */
@Migration(version = 0, priority = 0, database = AppDatabase.class)
public class Migration11AddIndexTranslation extends IndexPropertyMigration {

    @NonNull
    @Override
    public IndexProperty getIndexProperty() {
        return TranslationDB_Table
                .index_TranslationIndexStringKeyAndLanguageCode;
    }
}
