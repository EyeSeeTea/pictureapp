package org.eyeseetea.malariacare.data.database;

import org.eyeseetea.malariacare.data.database.migrations.Migration7UpdateOptionAttributeBackground;

public class PostMigration {

    public static void launchPostMigration() {
        Migration7UpdateOptionAttributeBackground.postMigrate();
    }
}
