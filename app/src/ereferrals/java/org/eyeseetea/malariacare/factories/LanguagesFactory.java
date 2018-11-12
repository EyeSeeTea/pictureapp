package org.eyeseetea.malariacare.factories;

import org.eyeseetea.malariacare.data.database.datasources.LanguagesLocalDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ILanguageRepository;
import org.eyeseetea.malariacare.domain.usecase.GetAllLanguagesUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public class LanguagesFactory {
    IAsyncExecutor mAsyncExecutor = new AsyncExecutor();
    IMainExecutor mMainExecutor = new UIThreadExecutor();
    ILanguageRepository mLanguageRepository = new LanguagesLocalDataSource();

    public GetAllLanguagesUseCase getGetLanguagesUseCase() {
        return new GetAllLanguagesUseCase(mAsyncExecutor, mMainExecutor, mLanguageRepository);
    }
}
