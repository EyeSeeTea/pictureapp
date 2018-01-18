package org.eyeseetea.malariacare.common.configurationimporter;


import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.PhoneFormatDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.QuestionThresholdDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;

import java.util.List;

public class ConfigurationImporterUtil {

    public static void cleanUsedTables() {
        CountryVersionDB.deleteAll();
        PhoneFormatDB.deleteAll();
        SurveyDB.deleteAll();
        ProgramDB.deleteAll();
        TabDB.deleteAll();
        HeaderDB.deleteAll();
        AnswerDB.deleteAll();
        OptionAttributeDB.deleteAll();
        OptionDB.deleteAll();
        QuestionDB.deleteAll();
        QuestionRelationDB.deleteAll();
        MatchDB.deleteAll();
        QuestionOptionDB.deleteAll();
        QuestionThresholdDB.deleteAll();
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

    public static int getProgramsDBCount() {
        List<ProgramDB> programs = ProgramDB.getAllPrograms();

        return programs.size();
    }

    public static int getPhoneFormatDBCount() {
        List<PhoneFormatDB> formats = PhoneFormatDB.getAllPhoneFormats();

        return formats.size();
    }
}
