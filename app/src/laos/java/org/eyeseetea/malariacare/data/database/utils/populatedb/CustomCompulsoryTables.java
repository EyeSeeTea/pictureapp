package org.eyeseetea.malariacare.data.database.utils.populatedb;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.Tab;

import java.util.Arrays;
import java.util.List;

public class CustomCompulsoryTables {

    public static List<Class<? extends BaseModel>> allMandatoryTables= Arrays.asList(
            Program.class,
            Tab.class,
            HeaderDB.class,
            AnswerDB.class,
            OptionAttributeDB.class,
            OptionDB.class,
            Question.class,
            QuestionRelation.class,
            MatchDB.class,
            QuestionOption.class
    );

    public static List<Class<? extends BaseModel>> getAllMandatoryTables() {
        return allMandatoryTables;
    }
}
