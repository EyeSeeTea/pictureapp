package org.eyeseetea.malariacare.strategies;


import static org.eyeseetea.malariacare.data.database.AppDatabase.programAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyName;
import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurvey;

import android.content.Context;

import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Program_Table;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Survey_Table;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;

import java.util.Date;

public abstract class ASurveyFragmentStrategy {

    public static Survey getRenderSurvey(Question screenQuestion) {
        return getMalariaSurvey();
    }

    public static boolean isDynamicStockQuestion(Question screenQuestion) {
        return false;
    }

    public static boolean isStockSurvey(Survey survey) {
        return false;
    }
    public static String getMalariaProgram() {
        return Program.getFirstProgram().getUid();
    }


    public static Survey getStockSurveyWithEventDate(Date event_date) {
        return null;
    }
}
