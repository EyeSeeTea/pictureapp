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
import org.eyeseetea.malariacare.data.database.utils.QuestionStrategy;

import java.util.Date;

public class SurveyFragmentStrategy  implements ISurveyFragmentStrategy {

    public SurveyFragmentStrategy(){};
    @Override
    public Survey getRenderSurvey(Question screenQuestion) {
        return (new QuestionStrategy().isStockQuestion(screenQuestion) || isDynamicStockQuestion(
                screenQuestion))
                ? Session.getStockSurvey()
                : getMalariaSurvey();
    }

    @Override
    public boolean isDynamicStockQuestion(Question screenQuestion) {
        if (screenQuestion.getUid() != null) {
            return screenQuestion.getUid().equals(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.dynamicStockQuestionUID));
        }
        return false;
    }


    @Override
    public boolean isStockSurvey(Survey survey) {
        return survey.getProgram().getUid().equals(
                PreferencesState.getInstance().getContext().getString(
                        R.string.stockProgramUID));
    }

    public String getMalariaProgram() {
        return Program.findByUID(PreferencesState.getInstance().getContext().getString(
                R.string.malariaProgramUID)).getUid();
    }


    @Override
    public Survey getStockSurveyWithEventDate(Date event_date) {
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

    @Override
    public String getTitleDose(float dose, String drug) {
        return String.format(
                PreferencesState.getInstance().getContext().getResources().getString(R.string.drugs_dose_of_drug_review_title),
                dose, drug);
    }
    @Override
    public String getTreatmentError() {
        return PreferencesState.getInstance().getContext().getResources().getResourceName(R.string.error_no_treatment);
    }

    @Override
    public String getPqTitleDose(float dose) {
        return getTitleDose(dose,
                PreferencesState.getInstance().getContext().getResources().getString(R.string.drugs_referral_Pq_review_title));
    }

    @Override
    public String getCqTitleDose(float dose) {
        return  getTitleDose(dose,
                PreferencesState.getInstance().getContext().getResources().getString(R.string.drugs_referral_Cq_review_title));
    }
}
