package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;

public interface IConvertFromSDKVisitorStrategy {
    void visit(EventExtended sdkEventExtended, Survey convertingSurvey);
}
