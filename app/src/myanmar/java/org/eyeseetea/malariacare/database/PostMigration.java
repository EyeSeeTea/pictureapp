package org.eyeseetea.malariacare.database;

import org.eyeseetea.malariacare.database.migrations.Migration15AddOptionAttributeColumns;
import org.eyeseetea.malariacare.database.migrations.Migration23AddStockCsvs;
import org.eyeseetea.malariacare.database.migrations.Migration23AddTreatmentCsvs;
import org.eyeseetea.malariacare.database.migrations.Migration23ModifyValuesLastMigration;

public class PostMigration {

    public static void launchPostMigration() {
        Migration15AddOptionAttributeColumns.postMigrate();
        Migration23ModifyValuesLastMigration.postMigrate();
        Migration23AddStockCsvs.postMigrate();
        Migration23AddTreatmentCsvs.postMigrate();
    }
}
