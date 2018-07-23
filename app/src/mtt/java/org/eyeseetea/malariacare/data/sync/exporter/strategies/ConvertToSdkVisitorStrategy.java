package org.eyeseetea.malariacare.data.sync.exporter.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.utils.Constants;

public class ConvertToSdkVisitorStrategy {

    public static void setAttributeCategoryOptionsInEvent(EventExtended event) {

    }

    public boolean putAlternativeControlDataElements(SurveyDB surveyDB, EventExtended event) {
        Context context = PreferencesState.getInstance().getContext();
        if (surveyDB.getProgramDB().getUid().equals(context.getString(
                R.string.stock_program_uid))) {
            putControlDataElements(surveyDB, event, context);
            return true;
        }
        return false;
    }

    private void putControlDataElements(SurveyDB surveyDB, EventExtended event, Context context) {

        buildAndSaveDataValue((context.getString(
                R.string.control_data_element_stock_type)), getSurveyType(surveyDB, context),
                event);

    }

    private String getSurveyType(SurveyDB surveyDB, Context context) {
        switch (surveyDB.getType()) {
            case Constants.SURVEY_RECEIPT:
                return context.getString(R.string.control_data_element_stock_type_receipt);
            case Constants.SURVEY_RESET:
                return context.getString(R.string.control_data_element_stock_type_balance);
            case Constants.SURVEY_ISSUE:
                return context.getString(R.string.control_data_element_stock_type_issue);
        }
        return context.getString(R.string.control_data_element_stock_type_issue);
    }

    /**
     * Adds value in Datavalue
     *
     * @param UID   is the dataElement uid
     * @param value is the value
     */
    private void buildAndSaveDataValue(String UID, String value, EventExtended event) {
        DataValueExtended dataValue = new DataValueExtended();
        dataValue.setDataElement(UID);
        dataValue.setEvent(event.getEvent());
        dataValue.setProvidedElsewhere(false);
        if (Session.getUserDB() != null) {
            dataValue.setStoredBy(Session.getUserDB().getName());
        }
        dataValue.setValue(value);
        dataValue.save();
    }
}
