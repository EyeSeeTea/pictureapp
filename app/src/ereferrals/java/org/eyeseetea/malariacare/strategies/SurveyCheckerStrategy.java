package org.eyeseetea.malariacare.strategies;

public class SurveyCheckerStrategy {

    private static final String DHIS_CHECK_EVENT_API =
            "/api/events.json?program=%s&orgUnit=%s&startDate=%s&endDate=%s"
                    + "&skipPaging=true"
                    + "&fields=event,orgUnit,program,dataValues";

    public static String getApiEventUrl(String DHIS_URL, String program, String orgUnit,
            String startDate, String endDate) {
        return String.format(DHIS_URL + DHIS_CHECK_EVENT_API, program, orgUnit, startDate,
                endDate);
    }
}
