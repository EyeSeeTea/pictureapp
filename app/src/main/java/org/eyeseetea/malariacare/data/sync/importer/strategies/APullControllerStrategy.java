package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;

public abstract class APullControllerStrategy {

    public abstract void convertMetadata(ConvertFromSDKVisitor converter);

}
