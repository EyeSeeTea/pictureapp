package org.eyeseetea.malariacare.database.migrations;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.Question;

@Migration(version = 17, databaseName = AppDatabase.NAME)
public class Migration17RenameShortNameToHelpText extends BaseMigration {

    public Migration17RenameShortNameToHelpText() {
        super();
    }

    public void onPreMigrate() {
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        //The column name can't be renamed in sqlite. It is needed create a temporal table with
        // the new column name.
        ModelAdapter myAdapter = FlowManager.getModelAdapter(Question.class);

        //Create temporal table
        String sql = myAdapter.getCreationQuery();

        sql = sql.replace("Question", "Question_temp");

        database.execSQL(sql);

        //Insert the data in temporal table
        String sqlCopy =
                "INSERT INTO Question_temp(id_question, code, de_name, help_text, "
                        + "form_name, uid, order_pos, numerator_w, feedback, denominator_w, "
                        + "id_header, id_header, id_answer, output, id_parent, "
                        + "id_composite_score, total_questions, visible, path)"
                        + " SELECT id_question, code, de_name, short_name, form_name, uid, "
                        + "order_pos, numerator_w, feedback, denominator_w, id_header, id_header,"
                        + "id_answer, output, id_parent, id_composite_score, total_questions,"
                        + " visible, path FROM Question";


        database.execSQL(sqlCopy);

        //Replace old table by new table with the new column name.
        database.execSQL("DROP TABLE IF EXISTS Question");
        database.execSQL("ALTER TABLE Question_temp RENAME TO Question");
    }

    @Override
    public void onPostMigrate() {
    }

}
