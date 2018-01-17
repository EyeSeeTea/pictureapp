package org.eyeseetea.malariacare.common.configurationimporter;


import android.content.Context;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.populatedb.UpdateDB;

import java.io.IOException;
import java.util.List;

public class ConfigurationImporterUtil {

    public static void cleanUsedTables() {
        HeaderDB.deleteAll();
        TabDB.deleteAll();
        ProgramDB.deleteAll();
        CountryVersionDB.deleteAll();


        QuestionDB.deleteAll();
        OptionDB.deleteAll();
        QuestionOptionDB.deleteAll();
    }

    public static void loadMetadataFromCSV(Context context) throws IOException {
        UpdateDB.updatePrograms(context);
        UpdateDB.updateTabs(context);
        UpdateDB.updateHeaders(context);

    }

    public static int getQuestionDBCount() {
        List<QuestionDB> questionDBS = QuestionDB.getAllQuestions();
        return questionDBS.size();
    }

    public static int getQuestionOptionDBCount() {
        List<QuestionOptionDB> questionOptionDBS = new Select().from(
                QuestionOptionDB.class).queryList();

        return questionOptionDBS.size();
    }


    public static int getOptionsDBCount() {
        List<OptionDB> optionDBS = OptionDB.getAllOptions();

        return optionDBS.size();
    }
}
