package org.eyeseetea.malariacare.strategies;

import android.util.Log;

import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.EventExtended;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;

import java.util.List;

public class SurveyCheckerStrategy extends ASurveyCheckerStrategy {

    @Override
    public void updateQuarantineSurveysStatus(List<Event> events, Survey survey) {
        boolean isSent = false;
        for (Event event : events) {
            isSent = surveyDateExistsInEventTimeCaptureControlDE(survey, event);
            if (isSent) {
                break;
            }
        }
        if (isSent) {
            Log.d(TAG, "Set quarantine survey as sent" + survey.getId_survey());
            survey.setStatus(Constants.SURVEY_SENT);

        } else {
            //When the completion date for a survey is not present in the server, this survey is
            // not in the server.
            //This survey is set as "completed" and will be send in the future.
            Log.d(TAG, "Set quarantine survey as completed" + survey.getId_survey());
            survey.setStatus(Constants.SURVEY_COMPLETED);
        }
        survey.save();
    }


    /**
     * Given an event, check through all its DVs if the survey completion date is present in the
     * event in the form of the control DE "Time Capture" whose UID is hardcoded
     */
    private static boolean surveyDateExistsInEventTimeCaptureControlDE(Survey survey, Event event) {
        for (DataValue dataValue : event.getDataValues()) {
            if (dataValue.getDataElement().equals(PushClient.DATETIME_CAPTURE_UID)
                    && dataValue.getValue().equals(EventExtended.format(survey.getCompletionDate(),
                    EventExtended.COMPLETION_DATE_FORMAT))) {
                Log.d(TAG, "Found survey" + survey.getId_survey() + "date "
                        + survey.getCompletionDate() + "dateevent" + dataValue.getValue());
                return true;
            }
        }
        return false;
    }
}
