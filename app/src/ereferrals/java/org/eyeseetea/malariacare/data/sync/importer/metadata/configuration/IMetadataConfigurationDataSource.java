package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import org.eyeseetea.malariacare.domain.entity.Configuration;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public interface IMetadataConfigurationDataSource {

    List<Question> getQuestionsFor(String countryCode) throws Exception;
    List<Configuration.CountryVersion> getCountriesVersions() throws Exception;
}