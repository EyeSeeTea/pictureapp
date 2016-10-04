package org.eyeseetea.malariacare.database;

import org.eyeseetea.malariacare.database.migrations.Migration13ModifyValuesLastMigration;
import org.eyeseetea.malariacare.database.migrations.Migration14AddQuestionReminder;
import org.eyeseetea.malariacare.database.migrations.Migration2Database;

/**
 * Created by idelcano on 29/09/2016.
 */

public class PostMigration {

    public static void launchPostMigration() {
        Migration2Database.postMigrate();
        Migration13ModifyValuesLastMigration.postMigrate();
        Migration14AddQuestionReminder.postMigrate();
    }
}
