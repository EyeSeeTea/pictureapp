package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Survey;

import java.util.List;

public interface ISurveyRepository {

    List<Survey> getLastSentSurveys(int count);

    void deleteSurveys();
}
