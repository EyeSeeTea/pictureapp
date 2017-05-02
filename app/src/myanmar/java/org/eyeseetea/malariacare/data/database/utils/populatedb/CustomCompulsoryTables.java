package org.eyeseetea.malariacare.data.database.utils.populatedb;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Match;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OptionAttribute;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.OrgUnitLevel;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.model.User;

import java.util.Arrays;
import java.util.List;

public class CustomCompulsoryTables {

    public static List<Class<? extends BaseModel>> allMandatoryTables = Arrays.asList(
            User.class,
            Program.class,
            Tab.class,
            Header.class,
            Answer.class,
            OptionAttribute.class,
            Option.class,
            Question.class,
            QuestionRelation.class,
            Match.class,
            QuestionOption.class,
            QuestionThreshold.class,
            OrgUnitLevel.class,
            OrgUnit.class
    );

    public static List<Class<? extends BaseModel>> getAllMandatoryTables() {
        return allMandatoryTables;
    }
}
