package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICurrentLanguageRepository;

public class CurrentLanguageDataSource implements ICurrentLanguageRepository {

    private static final String DEFAULT_LANGUAGE = "en";

    @Override
    public void getCurrentLanguage(IDataSourceCallback<String> callback) {
        String language = PreferencesState.getInstance().getLanguageCode();
        if (language == null || language.isEmpty()) language = DEFAULT_LANGUAGE;
        callback.onSuccess(language);
    }
}
