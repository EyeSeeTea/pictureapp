package org.eyeseetea.malariacare.locale.factory;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.sync.importer.LanguageDownloader;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.POEditorApiClient;
import org.eyeseetea.malariacare.data.sync.importer.strategies.ILanguagesClient;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;

public class LanguageFactory {
    @NonNull
    public static ILanguagesClient getPOEditorApiClient(String projectID, String apiToken) {
        return new POEditorApiClient(projectID, apiToken);

    }

    @NonNull
    public static LanguageDownloader getLanguageDownloader(ILanguagesClient client,
            IConnectivityManager connectivity) {
        return new LanguageDownloader(client, connectivity);

    }
}
