package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.exception.QuestionNotFoundException;

public class DataConverterStrategy implements IDataConverterStrategy {

    @Override
    public void convert(ConvertFromSDKVisitor converter, EventExtended event)
            throws QuestionNotFoundException {

    }
}
