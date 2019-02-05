package org.eyeseetea.malariacare.factories;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSettingsUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;


public class SettingsFactory {
    protected IMainExecutor mainExecutor = new UIThreadExecutor();
    protected IAsyncExecutor asyncExecutor = new AsyncExecutor();

    public GetSettingsUseCase getSettingsUseCase(Context context) {
        ISettingsRepository settingsDataSource = getSettingsDataSource(context);
        return new GetSettingsUseCase(mainExecutor, asyncExecutor,
                settingsDataSource);
    }

    public SaveSettingsUseCase saveSettingsUseCase(Context context) {
        ISettingsRepository settingsDataSource = getSettingsDataSource(context);
        return new SaveSettingsUseCase(mainExecutor, asyncExecutor,
                settingsDataSource);
    }

    @NonNull
    private SettingsDataSource getSettingsDataSource(Context context) {
        return new SettingsDataSource(context);
    }
}
