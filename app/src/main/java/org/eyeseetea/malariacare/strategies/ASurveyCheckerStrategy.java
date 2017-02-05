package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.database.model.Survey;
import org.hisp.dhis.android.sdk.persistence.models.Event;

import java.util.List;

public abstract class ASurveyCheckerStrategy {
    protected static String TAG = ".CheckSurveys";

    public abstract void updateQuarantineSurveysStatus(List<Event> events, Survey survey);

}
