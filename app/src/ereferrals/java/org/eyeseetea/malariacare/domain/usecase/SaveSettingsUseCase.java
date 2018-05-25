package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Settings;

public class SaveSettingsUseCase implements UseCase {
    private Callback mCallback;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private ISettingsRepository mSettingsRepository;
    private Settings settings;

    public SaveSettingsUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            ISettingsRepository settingsRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mSettingsRepository = settingsRepository;
    }

    public void execute(Callback callback, Settings settings) {
        mCallback = callback;
        this.settings = settings;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mSettingsRepository.saveSettings(settings);
                mCallback.onSuccess();
            }
        });
    }

    public interface Callback {
        void onSuccess();
    }
}