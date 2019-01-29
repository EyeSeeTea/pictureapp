package org.eyeseetea.malariacare.data.remote;


import org.eyeseetea.malariacare.domain.entity.Configuration;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public interface IMetadataConfigurationDataSource {

    List<Question> getQuestionsByCountryCode(String countryCode) throws Exception;

    List<Configuration.CountryVersion> getCountriesCodesAndVersions() throws Exception;
}
