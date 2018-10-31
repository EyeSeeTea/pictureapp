package org.eyeseetea.malariacare.data.remote;


import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.Metadata;
import org.eyeseetea.malariacare.domain.entity.Configuration;

import java.util.List;

public interface IMetadataConfigurationDataSource {

    Metadata getQuestionsByCountryCode(String countryCode) throws Exception;

    List<Configuration.CountryVersion> getCountriesVersions() throws Exception;
}
