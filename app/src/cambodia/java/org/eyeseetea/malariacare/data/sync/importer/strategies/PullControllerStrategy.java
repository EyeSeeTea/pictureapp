package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.PullController;

public class PullControllerStrategy extends APullControllerStrategy {
    public PullControllerStrategy(PullController pullController) {
        super(pullController);
    }
    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter) {

    }
}
