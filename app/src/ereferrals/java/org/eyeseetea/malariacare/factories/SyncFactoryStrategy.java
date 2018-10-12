package org.eyeseetea.malariacare.factories;


import android.content.Context;

import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.sync.exporter.WSPushController;
import org.eyeseetea.malariacare.data.sync.importer.WSPullController;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.IPushController;

public class SyncFactoryStrategy extends ASyncFactory {

    @Override
    protected IPullController getPullController(Context context) {
        return new WSPullController(context);
    }

    @Override
    protected IPushController getPushController(Context context) {
        return new WSPushController(context, new SurveyLocalDataSource());
    }
}
