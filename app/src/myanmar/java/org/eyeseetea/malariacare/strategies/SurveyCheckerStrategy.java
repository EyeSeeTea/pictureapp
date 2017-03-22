package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.data.remote.SdkQueries.getCategoryOptionUIDByCurrentUser;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

public class SurveyCheckerStrategy {

    private static final String DHIS_CHECK_EVENT_API =
            "/api/events.json?program=%s&orgUnit=%s&startDate=%s&endDate=%s&attributeCos=%s"
                    + "&attributeCc=%s&skipPaging=true"
                    + "&fields=event,orgUnit,program,dataValues";

    public static String getApiEventUrl(String DHIS_URL, String program, String orgUnit,
            String startDate, String endDate){
     return String.format(DHIS_URL + DHIS_CHECK_EVENT_API, program, orgUnit, startDate,
    endDate, getCategoryOptionUIDByCurrentUser(),
                    PreferencesState.getInstance().getContext().getString(
            R.string.category_combination));
    }
}
