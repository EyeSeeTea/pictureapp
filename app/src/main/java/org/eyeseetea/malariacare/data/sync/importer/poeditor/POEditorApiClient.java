package org.eyeseetea.malariacare.data.sync.importer.poeditor;


import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.Language;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.LanguagesResult;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.POEditorResponse;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.Term;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.TermsResult;
import org.eyeseetea.malariacare.data.sync.importer.strategies.ILanguagesClient;
import org.eyeseetea.malariacare.network.factory.HTTPClientFactory;

import java.util.List;

import retrofit2.*;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class POEditorApiClient implements ILanguagesClient {
    private String projectID;
    private String apiToken;
    private IPOApiEditor poEditorApi;

    public POEditorApiClient(String projectID, String apiToken) {
        this.projectID = projectID;
        this.apiToken = apiToken;

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .client(HTTPClientFactory.getHTTPClientWithLogging())
                .baseUrl(BuildConfig.poEditorApiUrl)
                .build();

        poEditorApi = retrofit.create(IPOApiEditor.class);
    }


    @NonNull
    public List<Language> getLanguages() throws Exception {
        Response<POEditorResponse<LanguagesResult>> response =
                poEditorApi.getLanguages(apiToken,
                        projectID).execute();

        return getResultsOrThrowException(response).result.languages;
    }

    public List<Term> getTranslationBy(String language) throws Exception {
        Response<POEditorResponse<TermsResult>> response =
                poEditorApi.getTranslations(apiToken,
                        projectID, language).execute();

        return getResultsOrThrowException(response).result.terms;
    }

    @NonNull
    private <T> POEditorResponse<T> getResultsOrThrowException(
            Response<POEditorResponse<T>> response)
            throws Exception {

        if (response.isSuccessful()) {
            return response.body();
        } else {
            String error = response.errorBody().string();
            throw new Exception(error);
        }
    }

}
