package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;

public interface ICanAddSurveysRepository {
    void canAddSurveys(IDataSourceCallback<Boolean> callback);

    void setCanAddSurveys(boolean canAddSurveys);
}
