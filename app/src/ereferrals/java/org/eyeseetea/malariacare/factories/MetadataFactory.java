package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;


public class MetadataFactory {
    protected IMainExecutor mainExecutor = new UIThreadExecutor();
    protected IAsyncExecutor asyncExecutor = new AsyncExecutor();

    protected GetSettingsUseCase getSettingsUseCase(Context context) {
        ISettingsRepository settingsDataSource = new SettingsDataSource(context);
        return new GetSettingsUseCase(mainExecutor, asyncExecutor,
                settingsDataSource);
    }
}
