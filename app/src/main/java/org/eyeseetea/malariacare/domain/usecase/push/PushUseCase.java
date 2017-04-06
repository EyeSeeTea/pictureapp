package org.eyeseetea.malariacare.domain.usecase.push;

import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.exception.ClosedUserPushException;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.ImportSummaryErrorException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.domain.service.OverLimitSurveysService;
import org.eyeseetea.malariacare.domain.usecase.UseCase;
import org.eyeseetea.malariacare.network.ServerAPIController;

import java.util.ArrayList;
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

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;

    private Callback mCallback;

    private SurveysThresholds mSurveysThresholds;

    public PushUseCase(IPushController pushController, IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor, SurveysThresholds surveysThresholds) {
        mPushController = pushController;
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mSurveysThresholds = surveysThresholds;
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

            OrgUnit orgUnit = OrgUnit.findByName(PreferencesState.getInstance().getOrgUnit());
            if (isBanned) {
                if (orgUnit != null && !orgUnit.isBanned()) {
                    orgUnit.setBan(true);
                    orgUnit.save();
                    notifyBannedOrgUnitError();

                }
            } else {
                if (orgUnit != null && orgUnit.isBanned()) {
                    orgUnit.setBan(false);
                    orgUnit.save();
                    notifyReOpenOrgUnit();
                }
                runPush();
            }
        } catch (Exception ex) {
            notifyPushError();
        }
    }

    private boolean isOrgUnitBanned() {
        String url = ServerAPIController.getServerUrl();
        String orgUnitNameOrCode = ServerAPIController.getOrgUnit();

        if (orgUnitNameOrCode.isEmpty()) {
            return false;
        }

        return !ServerAPIController.isOrgUnitOpen(url, orgUnitNameOrCode);
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
        //TODO: use case should not invoke directly Survey because belongs to the outer layer
        List<org.eyeseetea.malariacare.domain.entity.Survey> sentSurveys = new ArrayList<>();
        //Survey.getAllHideAndSentSurveys(mSurveysThresholds.getCount());

        if (OverLimitSurveysService.isSurveysOverLimit(sentSurveys, mSurveysThresholds)) {
            banOrgUnit();
        }
    }

    private void banOrgUnit() {
        String url = ServerAPIController.getServerUrl();
        String orgUnitNameOrCode = ServerAPIController.getOrgUnit();

        if (!orgUnitNameOrCode.isEmpty()) {
            ServerAPIController.banOrg(url, orgUnitNameOrCode);
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

