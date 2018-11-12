package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Language;

import java.util.List;

public interface ILanguageRepository {

    boolean translationsWereDownloaded();

    List<Language> getAllLanguage();

}
