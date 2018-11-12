package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.TranslationDB;
import org.eyeseetea.malariacare.data.database.model.TranslationLanguageDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.ILanguageRepository;
import org.eyeseetea.malariacare.domain.entity.Language;

import java.util.ArrayList;
import java.util.List;

public class LanguagesLocalDataSource implements ILanguageRepository {
    @Override
    public boolean translationsWereDownloaded() {
        return !TranslationLanguageDB.isEmpty()
                && !TranslationDB.isEmpty();
    }

    @Override
    public List<Language> getAllLanguage() {
        List<TranslationLanguageDB> translationLanguageDBS =
                TranslationLanguageDB.getAllTranslationLanguages();
        List<Language> languages = new ArrayList<>();
        for (TranslationLanguageDB translationLanguageDB : translationLanguageDBS) {
            languages.add(mapLanguageFromTranslationLanguageDB(translationLanguageDB));
        }
        return languages;
    }

    private Language mapLanguageFromTranslationLanguageDB(
            TranslationLanguageDB translationLanguageDB) {
        return new Language(translationLanguageDB.getLanguage_code(),
                translationLanguageDB.getLanguage_name());
    }
}
