package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.populatedb.FileCsvs;
import org.eyeseetea.malariacare.data.database.utils.populatedb.IPopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class PopulateDBStrategy implements IPopulateDBStrategy {
    public static List<Class<? extends BaseModel>> allMandatoryTables = Arrays.asList(
            ProgramDB.class,
            TabDB.class,
            HeaderDB.class,
            AnswerDB.class,
            OptionAttributeDB.class,
            OptionDB.class,
            QuestionDB.class,
            QuestionRelationDB.class,
            MatchDB.class,
            QuestionOptionDB.class
    );



    @Override
    public void createDummyOrganisationInDB() {
    }

    @Override
    public void createDummyOrgUnitsDataInDB(Context context) {

    }

    public void init() throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvsInFileIfNeeded();
    }

    public InputStream openFile(Context context, String table) throws IOException,
            FileNotFoundException {
        return context.openFileInput(table);
    }

    @Override
    public void logoutWipe() {
        PopulateDB.wipeDataBase();
    }


    public static List<Class<? extends BaseModel>> getAllMandatoryTables() {
        return allMandatoryTables;
    }
}
