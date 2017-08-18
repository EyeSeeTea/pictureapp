package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;

public interface ICurrentLanguageRepository {
    void getCurrentLanguage(IDataSourceCallback<String> callback);
}
