package org.eyeseetea.malariacare.configurationimporter;


import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.PhoneFormatDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;

import java.util.List;

public class ConfigurationImporterUtil {

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
