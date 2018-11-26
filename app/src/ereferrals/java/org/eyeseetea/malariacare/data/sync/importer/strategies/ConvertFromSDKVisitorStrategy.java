package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;

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

    public static void visit(CategoryOptionGroupExtended categoryOptionGroupExtended) {
    }

    public void visit(EventExtended sdkEventExtended, SurveyDB convertingSurvey) {
        convertingSurvey.setType(Constants.SURVEY_NO_TYPE);
    }

    @Override
    public void visit(DataValueExtended dataValueExtended, SurveyDB surveyDB) {

    }
}
