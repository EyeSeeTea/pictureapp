package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.entity.Survey;

import java.util.List;

public interface ISurveyRepository {

    List<Survey> getLastSentSurveys(int count);

    void deleteSurveys();

    void getUnsentSurveys(IDataSourceCallback<List<Survey>> callback);

    List<Survey> getUnsentSurveys();

    List<Survey> getAllQuarantineSurveys();

    List<Survey> getAllCompletedSurveys();

    Survey save(Survey survey);
}
