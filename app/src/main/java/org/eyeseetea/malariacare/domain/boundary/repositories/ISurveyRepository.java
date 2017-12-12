package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

import java.util.List;

public interface ISurveyRepository {

    List<Survey> getLastSentSurveys(int count);

    void deleteSurveys();

    void getUnsentSurveys(IDataSourceCallback<List<Survey>> callback);

    List<Survey> getAllQuarantineSurveys();

    void save(Survey survey);

    Survey newSurvey(Program program, OrganisationUnit organisationUnit, UserAccount user,
            int type);
}
