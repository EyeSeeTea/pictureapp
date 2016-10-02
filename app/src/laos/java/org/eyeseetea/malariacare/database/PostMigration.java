package org.eyeseetea.malariacare.database;

import org.eyeseetea.malariacare.database.migrations.Migration10UpdateOptionAttributes;
import org.eyeseetea.malariacare.database.migrations.Migration12AddQuestionReminder;
import org.eyeseetea.malariacare.database.migrations.Migration2Database;
import org.eyeseetea.malariacare.database.migrations.Migration3AddQuestionColumn;
import org.eyeseetea.malariacare.database.migrations.Migration4AddQuestionVisibleColumn;
import org.eyeseetea.malariacare.database.migrations.Migration5AddOptionAttributeColumns;
import org.eyeseetea.malariacare.database.migrations.Migration6AddOptionTextSizeColumn;
import org.eyeseetea.malariacare.database.migrations.Migration7AddQuestionPathAttributeColumn;

/**
 * Created by idelcano on 29/09/2016.
 */

public class PostMigration {

    public static void launchPostMigration() {
        Migration2Database.postMigrate();
        Migration3AddQuestionColumn.postMigrate();
        Migration4AddQuestionVisibleColumn.postMigrate();
        Migration5AddOptionAttributeColumns.postMigrate();
        Migration6AddOptionTextSizeColumn.postMigrate();
        Migration7AddQuestionPathAttributeColumn.postMigrate();
        Migration12AddQuestionReminder.postMigrate();
    }
}
