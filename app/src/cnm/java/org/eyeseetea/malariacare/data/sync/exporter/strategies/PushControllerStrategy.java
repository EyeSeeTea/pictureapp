package org.eyeseetea.malariacare.data.sync.exporter.strategies;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;

import java.util.List;

public class PushControllerStrategy extends APushControllerStrategy {

    @Override
    public List<SurveyDB> getSurveysToPush() {
        return SurveyDB.getAllCompletedSurveys();
    }
}
