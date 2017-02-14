package org.eyeseetea.malariacare.database;

import org.eyeseetea.malariacare.database.migrations.Migration23ModifyValuesLastMigration;
import org.eyeseetea.malariacare.database.migrations.Migration2Database;

/**
 * Created by idelcano on 29/09/2016.
 */

public class PostMigration {

    public static void launchPostMigration() {
        Migration2Database.postMigrate();
        Migration23ModifyValuesLastMigration.postMigrate();
    }
}
