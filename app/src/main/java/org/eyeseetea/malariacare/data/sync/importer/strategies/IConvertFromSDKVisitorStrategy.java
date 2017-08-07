package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;

public interface IConvertFromSDKVisitorStrategy {
    void visit(EventExtended sdkEventExtended, SurveyDB convertingSurveyDB);
}
