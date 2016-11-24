/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.database.migrations;

import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.opencsv.CSVReader;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.model.CompositeScore;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.OptionAttribute;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.OrgUnitLevel;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.Score;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.TabGroup;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.utils.Constants;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ignac on 30/11/2015.
 */
@Migration(version = 2, databaseName = AppDatabase.NAME)
public class Migration2Database extends BaseMigration {

    public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";
    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";
    private final static Class NEW_APP_TABLES[] = {
            OrgUnitLevel.class,
            Match.class,
            QuestionOption.class,
            TabGroup.class
    };
    private static String TAG = ".Migration2Database";
    private static Migration2Database instance;
    private boolean postMigrationRequired;

    public Migration2Database() {
        super();
        instance = this;
        postMigrationRequired = false;
    }

    public static void postMigrate() {
        //Migration NOT required -> done
        if (!instance.postMigrationRequired) {
            return;
        }

        //Data? Add new default data
        if (instance.hasData()) {
            instance.addTabGroup();
            instance.linkTabGroup();
            instance.moveOutputToQuestion();
        }

        //This operation wont be done again
        instance.postMigrationRequired = false;
    }

    public void onPreMigrate() {
    }

    /**
     * Adds new columns to database
     */
    @Override
    public void migrate(SQLiteDatabase database) {

        Log.d(TAG, "adding new columns...");
        postMigrationRequired = true;
        addColumn(database, CompositeScore.class, "hierarchical_code", "text");

        addColumn(database, OrgUnit.class, "id_parent", "integer");
        addColumn(database, OrgUnit.class, "id_org_unit_level", "integer");

        addColumn(database, Question.class, "feedback", "text");
        addColumn(database, Question.class, "output", "integer");

        addColumn(database, OptionAttribute.class, "path", "text");

        addColumn(database, QuestionRelation.class, "id_question", "integer");

        addColumn(database, Score.class, "id_survey", "integer");
        addColumn(database, Score.class, "score", "real");

        addColumn(database, Survey.class, "id_tab_group", "integer");
        addColumn(database, Survey.class, "creationDate", "integer");
        addColumn(database, Survey.class, "scheduledDate", "integer");

        addColumn(database, Tab.class, "id_tab_group", "integer");
    }

    @Override
    public void onPostMigrate() {
        //FIXME DBflow querys cannot be used at this point
//        if(hasData()) {
//            addTabGroup();
//            linkTabGroup();
//            moveOutputToQuestion();
//        }
    }

    /**
     * Checks if the current db has data or not
     */
    private boolean hasData() {
        return Program.getFirstProgram() != null;
    }

    /**
     * Adds a row to tabgroup table for data consistency
     */
    private void addTabGroup() {
        Log.d(TAG, "adding default tabgroup...");
        TabGroup tabGroup = new TabGroup("Health System QIS TabGroup", Program.getFirstProgram());
        tabGroup.save();
    }

    /**
     * Links the tabgroup in the current db
     */
    private void linkTabGroup() {
        Log.d(TAG, "linking default tabgroup to surveys and tabs...");

        TabGroup tabGroup = new Select().from(TabGroup.class).querySingle();

        //Add tabgroup to current surveys
        List<Survey> surveyList = new Select().from(Survey.class).queryList();
        for (Survey survey : surveyList) {
            survey.setTabGroup(tabGroup);
            survey.save();
        }

        //Add tabgroup to current tabs
        Tab tab = new Select().from(Tab.class).querySingle();
        tab.setTabGroup(tabGroup);
        tab.setType(Constants.TAB_DYNAMIC_AUTOMATIC_TAB);
        tab.save();
    }

    /**
     * Loads question.output from assets
     */
    private void moveOutputToQuestion() {
        Log.d(TAG, "moving type questions to question table...");

        Map<String, Integer> mapQuestionOutputs = loadAnswerOutputs(
                PreferencesState.getInstance().getContext().getAssets());
        List<Question> questions = new Select().from(Question.class).queryList();
        for (Question question : questions) {
            question.setOutput(mapQuestionOutputs.get(question.getCode()));
            question.save();
        }
    }

    /**
     * Returns a map with the output for each question:
     */
    private Map<String, Integer> loadAnswerOutputs(AssetManager assetManager) {
        //map<code,output>
        Map<String, Integer> mapQuestion = new HashMap<>();
        try {
            CSVReader reader = new CSVReader(
                    new InputStreamReader(assetManager.open(PopulateDB.QUESTIONS_CSV)), ';', '\'');
            String[] line;
            while ((line = reader.readNext()) != null) {
                mapQuestion.put(line[1], Integer.valueOf(line[12]));
            }
            reader.close();
        } catch (IOException ex) {

        }
        return mapQuestion;
    }


    private void addColumn(SQLiteDatabase database, Class model, String columnName, String type) {
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        database.execSQL(
                String.format(ALTER_TABLE_ADD_COLUMN, myAdapter.getTableName(), columnName, type));
    }
}