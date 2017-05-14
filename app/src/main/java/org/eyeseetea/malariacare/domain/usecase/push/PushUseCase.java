package org.eyeseetea.malariacare.domain.usecase.push;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ReadPolicy;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ClosedUserPushException;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.domain.service.OverLimitSurveysDomainService;
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

        void onBannedOrgUnit();

        void onReOpenOrgUnit();

        void onApiCallError(ApiCallException e);
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

        mPushController.changePushInProgress(true);

        try {
            configureBanOrgUnitChangeListener();

            boolean isBanned = isOrgUnitBanned();

            if (isBanned) {
                mPushController.changePushInProgress(false);
            } else {
                runPush();
            }

        } catch (Exception e) {
            mPushController.changePushInProgress(false);
            notifyPushError();
        }
    }

    private void configureBanOrgUnitChangeListener() {
        mOrganisationUnitRepository.setBanOrgUnitChangeListener(
                new IOrganisationUnitRepository.BanOrgUnitChangeListener() {
                    @Override
                    public void onBanOrgUnitChanged(OrganisationUnit organisationUnit) {
                        if (organisationUnit.isBanned()) {
                            notifyBannedOrgUnitError();
                        } else {
                            notifyReOpenOrgUnit();
                        }
                    }
                });
    }

    private boolean isOrgUnitBanned() {
        OrganisationUnit orgUnit = null;
        try {
            orgUnit = mOrganisationUnitRepository.getCurrentOrganisationUnit(
                    ReadPolicy.REMOTE);
        } catch (NetworkException e) {
            mPushController.changePushInProgress(false);
            notifyNetworkError();
        } catch (ApiCallException e) {
            mPushController.changePushInProgress(false);
            notifyApiCallError(e);
        }
        return orgUnit.isBanned();
    }

    private void runPush() {

        mPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onComplete() {
                mPushController.changePushInProgress(false);

                notifyComplete();

                banOrgUnitIfRequired();
            }

            @Override
            public void onInformativeError(Throwable throwable) {
                mPushController.changePushInProgress(false);
                notifyInformativeError(throwable.getMessage());
                banOrgUnitIfRequired();
            }

            @Override
            public void onError(Throwable throwable) {
                mPushController.changePushInProgress(false);
                System.out.println("PusUseCase error");
                if (throwable instanceof NetworkException) {
                    notifyNetworkError();
                } else if (throwable instanceof ConversionException) {
                    notifyConversionError();
                } else if (throwable instanceof SurveysToPushNotFoundException) {
                    notifySurveysNotFoundError();
                } else if (throwable instanceof ClosedUserPushException) {
                    notifyClosedUser();
                } else {
                    notifyPushError();
                }

            }
        });
    }

    private void banOrgUnitIfRequired() {
        if (mSurveysThresholds.getCount() > 0 && mSurveysThresholds.getTimeHours() > 0) {
            List<Survey> sentSurveys = mSurveyRepository.getLastSentSurveys(
                    mSurveysThresholds.getCount());

            if (OverLimitSurveysDomainService.isSurveysOverLimit(sentSurveys, mSurveysThresholds)) {
                banOrgUnit();
            }
        }
    }

    private void banOrgUnit() {
        OrganisationUnit organisationUnit = null;
        try {
            organisationUnit =
                    mOrganisationUnitRepository.getCurrentOrganisationUnit(ReadPolicy.CACHE);
        } catch (NetworkException e) {
            e.printStackTrace();
        } catch (ApiCallException e) {
            e.printStackTrace();
        }
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
                mCallback.onBannedOrgUnit();
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

    private void notifyApiCallError(final ApiCallException e) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onApiCallError(e);
            }
        });
    }
}

