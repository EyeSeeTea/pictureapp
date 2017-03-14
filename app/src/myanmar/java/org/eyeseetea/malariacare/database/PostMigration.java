package org.eyeseetea.malariacare.database;

import org.eyeseetea.malariacare.data.database.migrations.Migration2ChangeOptionPfPv;

public class PostMigration {

    public static void launchPostMigration() {
        Migration2ChangeOptionPfPv.postMigrate();
    }
}
