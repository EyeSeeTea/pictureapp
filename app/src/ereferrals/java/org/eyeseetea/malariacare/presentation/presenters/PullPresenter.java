package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.exception.WarningException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;

public class PullPresenter {
    private View view;

    PullUseCase pullUseCase;

    public PullPresenter(PullUseCase pullUseCase) {
        this.pullUseCase = pullUseCase;
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

        System.out.println("!!!!!!Executing pull");

        pullUseCase.execute(pullFilters, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                System.out.println("!!!!!!Finished pull");
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

    public interface View {
        void showProgress();

        void pullSuccess();

        void showPullError();

        void showNetworkError();

        void showPullConversionError();

        void showWarningError(String warning);
    }
}
