package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Match;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OptionAttribute;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.utils.populatedb.IPopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class PopulateDBStrategy implements IPopulateDBStrategy {

    public static List<Class<? extends BaseModel>> allMandatoryTables = Arrays.asList(
            Program.class,
            Tab.class,
            Header.class,
            Answer.class,
            OptionAttribute.class,
            Option.class,
            Question.class,
            QuestionRelation.class,
            Match.class,
            QuestionOption.class
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
