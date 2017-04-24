package org.eyeseetea.malariacare.domain.usecase.push;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.exception.ClosedUserPushException;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.ImportSummaryErrorException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.domain.service.OverLimitSurveysService;
import org.eyeseetea.malariacare.domain.usecase.UseCase;

import java.util.List;

public class PushUseCase implements UseCase {

    public interface Callback {
        void onComplete();

        void onPushError();

        void onPushInProgressError();

        void onSurveysNotFoundError();

        void onConversionError();

        void onNetworkError();

        void onInformativeError(String message);

        void onClosedUser();

        void onBannedOrgUnitError();

        void onReOpenOrgUnit();
    }

    private IPushController mPushController;
    private ISurveyRepository mSurveyRepository;
    private IOrganisationUnitRepository mOrganisationUnitRepository;

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;

    private Callback mCallback;

    private SurveysThresholds mSurveysThresholds;

    public PushUseCase(IPushController pushController, IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor, SurveysThresholds surveysThresholds,
            ISurveyRepository surveyRepository,
            IOrganisationUnitRepository organisationUnitRepository) {
        mPushController = pushController;
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mSurveysThresholds = surveysThresholds;
        mSurveyRepository = surveyRepository;
        mOrganisationUnitRepository = organisationUnitRepository;
    }

    public void execute(final Callback callback) {
        mCallback = callback;

        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {

        if (mPushController.isPushInProgress()) {
            notifyPushInProgressError();
            return;
        }

        try {
            boolean isBanned = isOrgUnitBanned();
            OrganisationUnit localOrgUnit =
                    mOrganisationUnitRepository.getCurrentLocalOrganisationUnit();
            if (isBanned) {
                if (localOrgUnit != null && !localOrgUnit.isBanned()) {
                    mOrganisationUnitRepository.banLocalOrganisationUnit(isBanned);
                    notifyBannedOrgUnitError();
                }
            } else {
                if (localOrgUnit != null && localOrgUnit.isBanned()) {
                    mOrganisationUnitRepository.banLocalOrganisationUnit(isBanned);
                    notifyReOpenOrgUnit();
                }
                runPush();
            }
        } catch (Exception ex) {
            notifyPushError();
        }
    }

    private boolean isOrgUnitBanned() {
        OrganisationUnit orgUnit = mOrganisationUnitRepository.getCurrentOrganisationUnit();

        return orgUnit.isBanned();
    }

    private void runPush() {
        mPushController.changePushInProgress(true);

        mPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onComplete() {
                mPushController.changePushInProgress(false);

                notifyComplete();

                banOrgUnitIfRequired();
            }

            @Override
            public void onError(Throwable throwable) {
                mPushController.changePushInProgress(false);

                if (throwable instanceof NetworkException) {
                    notifyNetworkError();
                } else if (throwable instanceof ConversionException) {
                    notifyConversionError();
                } else if (throwable instanceof SurveysToPushNotFoundException) {
                    notifySurveysNotFoundError();
                } else if (throwable instanceof ImportSummaryErrorException) {
                    notifyInformativeError(throwable.getMessage());
                    banOrgUnitIfRequired();
                } else if (throwable instanceof ClosedUserPushException) {
                    notifyClosedUser();
                } else {
                    notifyPushError();
                }
            }
        });
    }

    private void banOrgUnitIfRequired() {
        if (!isOrgUnitBanned() && mSurveysThresholds.getCount() > 0
                && mSurveysThresholds.getTimeHours() > 0) {
            List<Survey> sentSurveys = mSurveyRepository.getLastSentSurveys(
                    mSurveysThresholds.getCount());

            if (OverLimitSurveysService.isSurveysOverLimit(sentSurveys, mSurveysThresholds)) {
                banOrgUnit();
            }
        }
    }

    private void banOrgUnit() {
        OrganisationUnit organisationUnit =
                mOrganisationUnitRepository.getCurrentOrganisationUnit();
        if (organisationUnit != null) {
            organisationUnit.ban();
            mOrganisationUnitRepository.saveOrganisationUnit(organisationUnit);
            System.out.println("OrgUnit banned successfully");
        }
    }

    private void notifyComplete() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onComplete();
            }
        });
    }

    private void notifyPushError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onPushError();
            }
        });
    }

    private void notifyPushInProgressError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onPushInProgressError();
            }
        });
    }

    private void notifySurveysNotFoundError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSurveysNotFoundError();
            }
        });
    }

    private void notifyConversionError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onConversionError();
            }
        });
    }

    private void notifyNetworkError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onNetworkError();
            }
        });
    }

    private void notifyInformativeError(final String message) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onInformativeError(message);
            }
        });
    }

    private void notifyClosedUser() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onClosedUser();
            }
        });
    }

    private void notifyBannedOrgUnitError() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onBannedOrgUnitError();
            }
        });
    }

    private void notifyReOpenOrgUnit() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onReOpenOrgUnit();
            }
        });
    }
}

