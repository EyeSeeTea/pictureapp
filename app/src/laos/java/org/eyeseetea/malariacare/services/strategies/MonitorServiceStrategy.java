package org.eyeseetea.malariacare.services.strategies;

import org.eyeseetea.malariacare.data.database.model.Survey;

import java.util.Date;
import java.util.List;

public class MonitorServiceStrategy {
    public static List<Survey> getMonitoringSurveys(Date minDateForMonitor) {
        return Survey.findAllSurveysAfterDate(minDateForMonitor);
    }
}
