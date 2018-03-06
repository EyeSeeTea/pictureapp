package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.IConfigurationRepository;

public class ConfigurationLocalDataSource implements IConfigurationRepository {
    @Override
    public boolean configurationFilesWereDownloaded() {
        return !CountryVersionDB.isEmpty()
                && !QuestionDB.isEmpty();    }
}
