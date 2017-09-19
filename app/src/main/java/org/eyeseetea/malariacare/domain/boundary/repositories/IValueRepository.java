package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Value;

import java.util.List;

public interface IValueRepository {

    List<Value> getValuesFromSurvey(Long idSurvey);
}
