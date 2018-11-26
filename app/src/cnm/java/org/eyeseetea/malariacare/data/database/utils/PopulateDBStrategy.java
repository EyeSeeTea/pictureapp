package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.DrugCombinationDB;
import org.eyeseetea.malariacare.data.database.model.DrugDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.ProgramProgramRelationDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.model.TreatmentDB;
import org.eyeseetea.malariacare.data.database.model.TreatmentMatchDB;
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
            QuestionOptionDB.class,
            ProgramProgramRelationDB.class,
            TreatmentDB.class,
            DrugCombinationDB.class,
            TreatmentMatchDB.class,
            DrugDB.class
    );

    @Override
    public void init() {
    }

    @Override
    public InputStream openFile(Context context, String table) throws IOException,
            FileNotFoundException {
        AssetManager assetMgr = context.getAssets();
        return assetMgr.open(table);
    }


    @Override
    public void logoutWipe() {
        PopulateDB.wipeOrgUnitsAndEvents();
    }

    @Override
    public void createDummyOrganisationInDB() {
    }

    @Override
    public void createDummyOrgUnitsDataInDB(Context context) {
    }

    public static List<Class<? extends BaseModel>> getAllMandatoryTables() {
        return allMandatoryTables;
    }
}
