package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.TranslationDB;
import org.eyeseetea.malariacare.data.database.model.TranslationLanguageDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.ILanguageRepository;

public class LanguagesLocalDataSource implements ILanguageRepository {
    @Override
    public boolean translationsWereDownloaded() {
        return !TranslationLanguageDB.isEmpty()
                && !TranslationDB.isEmpty();
    }
}
