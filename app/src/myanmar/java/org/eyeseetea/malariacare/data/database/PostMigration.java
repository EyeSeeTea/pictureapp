package org.eyeseetea.malariacare.data.database;

import org.eyeseetea.malariacare.data.database.migrations.Migration2ChangeTravelQuestionIcon;
import org.eyeseetea.malariacare.data.database.migrations.Migration4ChangeTreatmentTable;

/**
 * Created by idelcano on 29/09/2016.
 */

public class PostMigration {

    public static void launchPostMigration() {
        Migration2ChangeTravelQuestionIcon.postMigrate();
        Migration4ChangeTreatmentTable.postMigrate();
    }
}
