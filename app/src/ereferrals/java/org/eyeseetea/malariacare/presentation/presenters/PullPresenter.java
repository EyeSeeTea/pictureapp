package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.exception.WarningException;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;

public class PullPresenter {
    private View view;

    private final PullUseCase pullUseCase;
    private final GetSettingsUseCase getSettingsUseCase;
    private final SaveSettingsUseCase saveSettingsUseCase;

    public PullPresenter(PullUseCase pullUseCase,
            GetSettingsUseCase getSettingsUseCase, SaveSettingsUseCase saveSettingsUseCase) {
        this.pullUseCase = pullUseCase;
        this.getSettingsUseCase = getSettingsUseCase;
        this.saveSettingsUseCase = saveSettingsUseCase;
    }

    public void attachView(View view) {
        this.view = view;

        launchPull();
    }

    public void detachView() {
        view = null;
    }


    private void launchPull() {
        PullFilters pullFilters = new PullFilters();
        pullFilters.setDownloadDataRequired(true);
        pullFilters.setPullDataAfterMetadata(true);
        pullFilters.setPullMetaData(true);
        pullFilters.setDemo(false);

        pullUseCase.execute(pullFilters, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                changePullToNotRequired();
                if (view != null) {
                    view.pullSuccess();
                }
            }

            @Override
            public void onStep(PullStep pullStep) {
                System.out.println("Pull Step: " + pullStep);

                if (pullStep == PullStep.METADATA) {
                    view.showProgress();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                if (view != null) {
                    view.showPullError();
                }
            }

            @Override
            public void onNetworkError() {
                if (view != null) {
                    view.showNetworkError();
                }
            }

            @Override
            public void onPullConversionError() {
                if (view != null) {
                    view.showPullConversionError();
                }
            }

            @Override
            public void onWarning(WarningException warning) {
                if (view != null) {
                    view.showWarningError(warning.getMessage());
                }
            }

            @Override
            public void onCancel() {
                //TODO: has no sense this callback in this context
            }
        });
    }

    private void changePullToNotRequired() {
        getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings settings) {
                settings.changePullRequired(false);
                saveSettings(settings);
            }
        });
    }

    private void saveSettings(Settings settings) {

        saveSettingsUseCase.execute(new SaveSettingsUseCase.Callback() {
            @Override
            public void onSuccess() {
                System.out.println("Saved - Soft Login is not required");
            }
        }, settings);
    }

    public interface View {
        void showProgress();

        void pullSuccess();

        void showPullError();

        void showNetworkError();

        void showPullConversionError();

        void showWarningError(String warning);
    }
}
