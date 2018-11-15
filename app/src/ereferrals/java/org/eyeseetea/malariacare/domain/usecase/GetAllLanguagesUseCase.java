package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ILanguageRepository;
import org.eyeseetea.malariacare.domain.entity.Language;

import java.util.List;

public class GetAllLanguagesUseCase implements UseCase {

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private ILanguageRepository mLanguageRepository;
    private Callback mCallback;

    public interface Callback {
        void onSuccess(List<Language> languages);
    }

    public GetAllLanguagesUseCase(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            ILanguageRepository languageRepository) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mLanguageRepository = languageRepository;
    }

    @Override
    public void run() {
        notifyOnSuccess(mLanguageRepository.getAllLanguage());
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }


    private void notifyOnSuccess(final List<Language> allLanguages) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(allLanguages);
            }
        });
    }

}
