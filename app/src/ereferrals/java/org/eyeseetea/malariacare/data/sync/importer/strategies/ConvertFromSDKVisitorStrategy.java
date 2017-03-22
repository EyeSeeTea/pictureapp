package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.utils.Constants;

public class ConvertFromSDKVisitorStrategy implements IConvertFromSDKVisitorStrategy {

    private final Context mContext;

    public ConvertFromSDKVisitorStrategy(Context context) {
        mContext = context;
    }

    public void visit(EventExtended sdkEventExtended, Survey convertingSurvey) {
        if (convertingSurvey.getProgram().getUid().equals(mContext.getString(
                R.string.stockProgramUID))) {
            convertingSurvey.setType(Constants.SURVEY_ISSUE);
        } else {
            convertingSurvey.setType(Constants.SURVEY_NO_TYPE);
        }
    }
}
