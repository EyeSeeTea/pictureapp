package org.eyeseetea.malariacare.data.database.datasources;


import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.TranslationDB;
import org.eyeseetea.malariacare.data.database.model.TranslationLanguageDB;
import org.eyeseetea.malariacare.domain.boundary.repositories
        .IConfigurationAndLanguagesStatusRepository;

public class ConfigurationAndLanguageStatusDataSource implements
        IConfigurationAndLanguagesStatusRepository {

    @Override
    public boolean translationsWereDownloaded() {
        return !TranslationLanguageDB.isEmpty()
                && !TranslationDB.isEmpty();
    }

    @Override
    public boolean configurationFilesWereDownloaded() {
        return !CountryVersionDB.isEmpty()
                && !QuestionDB.isEmpty();
    }
}
