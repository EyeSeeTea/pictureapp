package org.eyeseetea.malariacare.data.sync.importer.strategies;


import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.eyeseetea.malariacare.data.database.model.TranslationDB;
import org.eyeseetea.malariacare.data.database.model.TranslationDB_Table;
import org.eyeseetea.malariacare.data.database.model.TranslationLanguageDB;
import org.eyeseetea.malariacare.data.database.model.TranslationLanguageDB_Table;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.Language;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.Term;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageDownloader {


    private ILanguagesClient client;
    private IConnectivityManager connectivity;


    public LanguageDownloader(ILanguagesClient client, IConnectivityManager connectivity) {
        this.client = client;
        this.connectivity = connectivity;
    }


    public void start() throws Exception {
        if (connectivity.isDeviceOnline()) {
            List<Language> languagesFromServer = client.getLanguages();

            List<Language> languagesToDownload = getLanguagesToDownload(languagesFromServer);
            updateLocalTranslations(languagesToDownload, client);
        }
    }

    private void updateLocalTranslations(List<Language> toUpdate, ILanguagesClient client)
            throws Exception {

        for (Language language : toUpdate) {
            List<Term> terms = client.getTranslationBy(language.code);

            deletePreviousTranslations(language.code);
            for (Term term : terms) {
                save(term, language.code);
            }
            save(language);
        }
    }

    private void save(Term term, String languageCode) {
        TranslationDB trans = new TranslationDB();
        trans.setString_key(term.term);
        trans.setLanguage_code(languageCode);
        trans.setTranslation(term.translation.content);
        trans.save();

    }

    private void deletePreviousTranslations(String languageCode) {
        SQLite.delete().from(TranslationLanguageDB.class).where(
                TranslationLanguageDB_Table.language_code.eq(languageCode)).query();

        SQLite.delete().from(TranslationDB.class).where(
                TranslationDB_Table.language_code.eq(languageCode)).query();
    }

    @NonNull
    private List<Language> getLanguagesToDownload(List<Language> languagesFromServer) {

        List<Language> languagesToDownload = new ArrayList<>();
        List<TranslationLanguageDB> languagesFromDB = SQLite.select().
                from(TranslationLanguageDB.class).queryList();

        Map<String, TranslationLanguageDB> mapLanguagesByCodesInDB = mapLanguagesByCodeFrom(
                languagesFromDB);

        for (Language languageServer : languagesFromServer) {

            if (requiresToBeDownloaded(languageServer, mapLanguagesByCodesInDB)) {
                languagesToDownload.add(languageServer);
            }
        }

        return languagesToDownload;

    }

    private boolean requiresToBeDownloaded(Language languageServer,
            Map<String, TranslationLanguageDB> mapLanguagesByCodesInDB) {
        boolean requiresToBeDownloaded = false;

        if (isAlreadyInDB(languageServer, mapLanguagesByCodesInDB)) {

            if (isNewer(languageServer, mapLanguagesByCodesInDB)) {
                requiresToBeDownloaded = true;
            }

        } else {
            requiresToBeDownloaded = true;
        }
        return requiresToBeDownloaded;
    }

    private boolean isNewer(@NonNull Language fromServer,
            @NonNull Map<String, TranslationLanguageDB> mapLanguagesDB) {

        TranslationLanguageDB fromDB = mapLanguagesDB.get(fromServer.code);

        return fromServer.updated.after(fromDB.getLast_update());
    }

    @NonNull
    private Map<String, TranslationLanguageDB> mapLanguagesByCodeFrom(
            @NonNull List<TranslationLanguageDB> languagesDB) {

        Map<String, TranslationLanguageDB> map = new HashMap<>();

        for (TranslationLanguageDB language : languagesDB) {
            map.put(language.getLanguage_code(), language);
        }
        return map;
    }

    private boolean isAlreadyInDB(@NonNull Language languageServer,
            @NonNull Map<String, TranslationLanguageDB> mapLanguagesDB) {
        return mapLanguagesDB.containsKey(languageServer.code);
    }

    private void save(Language language) {
        TranslationLanguageDB languageDB = new TranslationLanguageDB();
        languageDB.setLanguage_name(language.name);
        languageDB.setLanguage_code(language.code);
        languageDB.setLast_update(language.updated);
        languageDB.save();

    }
}
