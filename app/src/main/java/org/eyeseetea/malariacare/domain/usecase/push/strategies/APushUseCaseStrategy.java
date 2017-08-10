package org.eyeseetea.malariacare.domain.usecase.push.strategies;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
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
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;

import java.util.List;

public abstract class APushUseCaseStrategy {
    protected IPushController mPushController;
    private IOrganisationUnitRepository mOrganisationUnitRepository;
    private SurveysThresholds mSurveysThresholds;
    private ISurveyRepository mSurveyRepository;
    private IMainExecutor mMainExecutor;
    protected PushUseCase.Callback mCallback;

    public APushUseCaseStrategy(IPushController pushController,
            IOrganisationUnitRepository organisationUnitRepository,
            SurveysThresholds surveysThresholds,
            ISurveyRepository surveyRepository,
            IMainExecutor mainExecutor) {
        mPushController = pushController;
        mOrganisationUnitRepository = organisationUnitRepository;
        mSurveysThresholds = surveysThresholds;
        mSurveyRepository = surveyRepository;
        mMainExecutor = mainExecutor;
    }

    public void run(PushUseCase.Callback callback) {
        mCallback = callback;
        IPushController pushController = mPushController;

        if (pushController.isPushInProgress()) {
            notifyPushInProgressError();
            return;
        }

        pushController.changePushInProgress(true);

        try {
            configureBanOrgUnitChangeListener();

            boolean isBanned = isOrgUnitBanned();

            if (isBanned) {
                pushController.changePushInProgress(false);
            } else {
                runPush();
            }

        } catch (Exception e) {
            pushController.changePushInProgress(false);
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

    private boolean isOrgUnitBanned() throws NetworkException, ApiCallException {
        OrganisationUnit orgUnit = null;
        try {
            orgUnit = mOrganisationUnitRepository.getCurrentOrganisationUnit(ReadPolicy.REMOTE);
        } catch (NetworkException e) {
            mPushController.changePushInProgress(false);
            notifyNetworkError();
        } catch (ApiCallException e) {
            mPushController.changePushInProgress(false);
            notifyApiCallError(e);
        }
        return orgUnit.isBanned();
    }

    protected void runPush() {

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

    protected void notifyPushInProgressError() {
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
