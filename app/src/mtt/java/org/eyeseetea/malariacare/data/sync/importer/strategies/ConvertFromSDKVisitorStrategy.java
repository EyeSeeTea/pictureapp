package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.sync.importer.models.CategoryOptionGroupExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.utils.Constants;

public class ConvertFromSDKVisitorStrategy implements IConvertFromSDKVisitorStrategy {

    private final Context mContext;

    public ConvertFromSDKVisitorStrategy(Context context) {
        mContext = context;
    }

    public void visit(EventExtended sdkEventExtended, SurveyDB convertingSurvey) {
    }

    @Override
    public void visit(DataValueExtended dataValueExtended, SurveyDB surveyDB) {
        if (surveyDB.getProgramDB().getUid().equals(mContext.getString(
                R.string.stock_program_uid))) {
            int type = Constants.SURVEY_NO_TYPE;
            if (dataValueExtended.getValue().equals(
                    mContext.getString(R.string.control_data_element_stock_type_receipt))) {
                type = Constants.SURVEY_RECEIPT;
            } else if (dataValueExtended.getValue().equals(
                    mContext.getString(R.string.control_data_element_stock_type_balance))) {
                type = Constants.SURVEY_RESET;
            } else if (dataValueExtended.getValue().equals(
                    mContext.getString(R.string.control_data_element_stock_type_issue))) {
                type = Constants.SURVEY_ISSUE;
            }
            surveyDB.setType(type);
            surveyDB.save();
        }
    }

    public static void visit(CategoryOptionGroupExtended categoryOptionGroupExtended) {
    }
}
