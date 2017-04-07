package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;

public class PullControllerStrategy extends APullControllerStrategy {
    public PullControllerStrategy(PullController pullController) {
        super(pullController);
    }
    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter) {

    }
}
