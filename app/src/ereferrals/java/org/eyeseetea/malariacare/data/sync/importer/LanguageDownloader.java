package org.eyeseetea.malariacare.data.sync.importer;


import android.support.annotation.NonNull;
import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.eyeseetea.malariacare.data.database.model.TranslationDB;
import org.eyeseetea.malariacare.data.database.model.TranslationDB_Table;
import org.eyeseetea.malariacare.data.database.model.TranslationLanguageDB;
import org.eyeseetea.malariacare.data.database.model.TranslationLanguageDB_Table;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.Language;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.Term;
import org.eyeseetea.malariacare.data.sync.importer.strategies.ILanguagesClient;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageDownloader {
    private static String TAG = "LanguageDownloader";

    private ILanguagesClient client;
    private IConnectivityManager connectivity;


    public LanguageDownloader(ILanguagesClient client, IConnectivityManager connectivity) {
        this.client = client;
        this.connectivity = connectivity;
    }


    public void start(String languageCodeFilter) throws Exception {
        if (connectivity.isDeviceOnline()) {


            Log.d(TAG, "Starting download languages and terms by filter:" + languageCodeFilter);

            Date downloadedDate = new Date();

            Log.d(TAG, "Retrieving languages from server");

            List<Language> languagesFromServer = client.getLanguages();

            Log.d(TAG, "getLanguagesToDownload");
            List<Language> languagesToDownload = getLanguagesToDownload(languageCodeFilter,
                    languagesFromServer, downloadedDate);

            updateLocalTranslations(languagesToDownload, client);
            Log.d(TAG, "Finished download languagesand terms");
        }
    }

    private void updateLocalTranslations(List<Language> toUpdate, ILanguagesClient client)
            throws Exception {

        for (Language language : toUpdate) {
            Log.d(TAG, "Download terms of " + language.name);
            List<Term> terms = client.getTranslationBy(language.code);

            Log.d(TAG, "Delete previous  terms of " + language.name);
            deletePreviousTranslations(language.code);
            for (Term term : terms) {
                save(term, language.code);
            }

            Log.d(TAG, "Saving terms of " + language.name);
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
    private List<Language> getLanguagesToDownload(String languageCodeFilter,
            List<Language> languagesFromServer, Date downloadDateTime) {

        List<Language> languagesToDownload = new ArrayList<>();
        List<TranslationLanguageDB> languagesFromDB = SQLite.select().
                from(TranslationLanguageDB.class).queryList();

        Map<String, TranslationLanguageDB> mapLanguagesByCodesInDB = mapLanguagesByCodeFrom(
                languagesFromDB);

        for (Language languageServer : languagesFromServer) {
            if (languageCodeFilter != null && !languageCodeFilter.isEmpty()) {
                if (languageCodeFilter.equals(languageServer.code)) {
                    languagesToDownload.add(languageServer);
                    break;
                }
            } else {
                if (languageServer.updated == null) {
                    languageServer.updated = downloadDateTime;
                }
                if (requiresToBeDownloaded(languageServer, mapLanguagesByCodesInDB)) {
                    languagesToDownload.add(languageServer);
                }
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
