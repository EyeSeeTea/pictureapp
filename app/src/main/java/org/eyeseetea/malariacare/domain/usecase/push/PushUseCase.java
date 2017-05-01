package org.eyeseetea.malariacare.domain.usecase.push;

import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.exception.ClosedUserPushException;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.ImportSummaryErrorException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.domain.usecase.UseCase;
import org.eyeseetea.malariacare.network.ServerAPIController;

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

    private static int DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR = 30;

    private static int DHIS_LIMIT_HOURS = 1;

    private IPushController mPushController;

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;

    private Callback mCallback;

    public PushUseCase(IPushController pushController, IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor) {
        mPushController = pushController;
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
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
        List<Survey> sentSurveys = Survey.getAllHideAndSentSurveys(
                DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR);

        if (isSurveysOverLimit(sentSurveys)) {
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

    private boolean isSurveysOverLimit(List<Survey> surveyList) {
        //TODO: For Cambodia the surveys are never above the limit, we may need it for laos,
        // it is commented for this moment necessary.
        // Surely it would have to create a strategy in that case
        return false;


/*        //TODO: simplify this method
        int countDates = 0;

        if (surveyList.size() >= DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR) {
            for (int i = 0; i < surveyList.size(); i++) {
                Calendar actualSurvey = Utils.DateToCalendar(surveyList.get(i).getEventDate());
                for (int d = 0; d < surveyList.size(); d++) {
                    Calendar nextSurvey = Utils.DateToCalendar(surveyList.get(d).getEventDate());
                    if (actualSurvey.before(nextSurvey)) {
                        if (!Utils.isDateOverLimit(actualSurvey, nextSurvey, DHIS_LIMIT_HOURS)) {
                            countDates++;
                            Log.d(TAG, "Surveys sents in one hour:" + countDates);
                            if (countDates >= DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;*/
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

