package org.eyeseetea.malariacare.database;

import org.eyeseetea.malariacare.database.migrations.Migration15AddOptionAttributeColumns;
import org.eyeseetea.malariacare.database.migrations.Migration23AddTreatmentCsvs;
import org.eyeseetea.malariacare.database.migrations.Migration22AddStockCsvs;

/**
 * Created by idelcano on 29/09/2016.
 */

public class PostMigration {

    public static void launchPostMigration() {
        Migration15AddOptionAttributeColumns.postMigrate();
        Migration22AddStockCsvs.postMigrate();
        Migration23AddTreatmentCsvs.postMigrate();
    }
}
