package org.eyeseetea.malariacare.data.sync.exporter.strategies;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;

public class ConvertToSdkVisitorStrategy {

    public static void setAttributeCategoryOptionsInEvent(EventExtended event) {

    }

    public boolean putAlternativeControlDataElements(SurveyDB surveyDB, EventExtended event) {
        return false;
    }
}
