package org.eyeseetea.malariacare.data.database.utils;

import org.eyeseetea.malariacare.data.database.model.Survey;

public class MyanmarSession {
    /**
     * The current stock malariaSurvey
     */
    private static Survey stockSurvey;


    public static synchronized void setStockSurvey(Survey stockSurvey) {
        MyanmarSession.stockSurvey = stockSurvey;
    }

    public static Survey getStockSurvey() {
        return stockSurvey;
    }
}
