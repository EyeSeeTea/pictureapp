package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.data.sync.exporter.PushController;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.IPushController;

public class SyncFactoryStrategy extends ASyncFactory {
    @Override
    protected IPullController getPullController(Context context) {
        return new PullController(context);
    }

    @Override
    protected IPushController getPushController(Context context) {
        IAuthenticationManager authenticationManager =
                new AuthenticationFactoryStrategy().getAuthenticationManager(context);
        return new PushController(context, authenticationManager);
    }
}
