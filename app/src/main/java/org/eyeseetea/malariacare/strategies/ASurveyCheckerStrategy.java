package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.data.database.model.Survey;

import java.util.List;

public abstract class ASurveyCheckerStrategy {
    protected static String TAG = ".CheckSurveys";

    public abstract void updateQuarantineSurveysStatus(List<EventExtended> events, Survey survey);

}
