package org.eyeseetea.malariacare.data.remote;


import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public interface IMetadataConfigurationDataSource {

    List<Question> getQuestions() throws Exception;
}
