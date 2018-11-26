package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.sync.importer.models.CategoryOptionGroupExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.utils.Constants;

public class ConvertFromSDKVisitorStrategy implements IConvertFromSDKVisitorStrategy {

    private final Context mContext;

    public ConvertFromSDKVisitorStrategy(Context context) {
        mContext = context;
    }

    public void visit(EventExtended sdkEventExtended, SurveyDB convertingSurveyDB) {
        convertingSurveyDB.setType(Constants.SURVEY_NO_TYPE);
    }

    public static void visit(CategoryOptionGroupExtended categoryOptionGroupExtended) {
    }

    @Override
    public void visit(DataValueExtended dataValueExtended, SurveyDB surveyDB) {

    }
}
