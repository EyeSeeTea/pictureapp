package org.eyeseetea.malariacare.data.database;

import org.eyeseetea.malariacare.data.database.migrations.Migration7UpdateOptionAttributeBackground;
import org.eyeseetea.malariacare.domain.exception.PostMigrationException;

public class PostMigration {

    public static void launchPostMigration() throws PostMigrationException{
        Migration7UpdateOptionAttributeBackground.postMigrate();
    }
}
