package org.eyeseetea.malariacare.domain.boundary.repositories;


public interface IConfigurationAndLanguagesStatusRepository {

    boolean translationsWereDownloaded();

    boolean configurationFilesWereDownloaded();
}
