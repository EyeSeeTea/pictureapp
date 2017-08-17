package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICanAddSurveysRepository;


public class CanAddSurveysLocalDataSource implements ICanAddSurveysRepository {
    @Override
    public void canAddSurveys(IDataSourceCallback<Boolean> callback) {
        callback.onSuccess(PreferencesEReferral.canAddNewSurveys());
    }

    @Override
    public void setCanAddSurveys(boolean canAddSurveys) {
        PreferencesEReferral.setCanAddNewSurveys(canAddSurveys);
    }
}
