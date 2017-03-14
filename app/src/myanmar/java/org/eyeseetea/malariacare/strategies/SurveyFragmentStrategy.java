package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.data.database.AppDatabase.programAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyName;
import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurvey;

import android.content.Context;
import android.util.Log;

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
import org.eyeseetea.malariacare.data.model.QuestionStrategy;

import java.util.Date;

public class SurveyFragmentStrategy extends ASurveyFragmentStrategy {
    public static Survey getRenderSurvey(Question screenQuestion) {
        return (QuestionStrategy.isStockQuestion(screenQuestion) || isDynamicStockQuestion(
                screenQuestion))
                ? Session.getStockSurvey()
                : getMalariaSurvey();
    }

    public static boolean isDynamicStockQuestion(Question screenQuestion) {
        if (screenQuestion.getUid() != null) {
            return screenQuestion.getUid().equals(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.dynamicStockQuestionUID));
        }
        return false;
    }


    public static boolean isStockSurvey(Survey survey) {
        return survey.getProgram().getUid().equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.stockProgramUID));
    }

    public static String getMalariaProgram() {
        return Program.findByUID(PreferencesState.getInstance().getContext().getString(
                R.string.malariaProgramUID)).getUid();
    }


    public static Survey getStockSurveyWithEventDate(Date event_date) {
        Context context = PreferencesState.getInstance().getContext();

        return new Select().from(Survey.class).as(surveyName)
                .join(Program.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(Survey_Table.id_program_fk.withTable(surveyAlias)
                        .eq(Program_Table.id_program.withTable(programAlias)))
                .where(Survey_Table.event_date.withTable(surveyAlias)
                        .eq(event_date))
                .and(Program_Table.uid_program.withTable(programAlias).is(
                        context.getString(R.string.stockProgramUID))).querySingle();

    }
}
