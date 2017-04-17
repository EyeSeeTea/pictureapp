package org.eyeseetea.malariacare.data.database;

import org.eyeseetea.malariacare.data.database.migrations.Migration2ChangeOptionPfPv;
import org.eyeseetea.malariacare.data.database.migrations.Migration2ChangeTravelQuestionIcon;
import org.eyeseetea.malariacare.data.database.migrations.Migration3RefactorHelptext;


public class PostMigration {

    public static void launchPostMigration() {
        Migration2ChangeTravelQuestionIcon.postMigrate();
        Migration2ChangeOptionPfPv.postMigrate();
        Migration3RefactorHelptext.postMigrate();
    }
}
