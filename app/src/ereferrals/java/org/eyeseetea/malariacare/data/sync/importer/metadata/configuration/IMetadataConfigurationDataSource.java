package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public interface IMetadataConfigurationDataSource {

    List<Question> getQuestions() throws Exception;
}
