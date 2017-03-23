package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.ImportSummaryErrorException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.SurveysToPushNotFoundException;
import org.eyeseetea.malariacare.network.BanOrgUnitExecutor;

import java.util.List;

public class PushUseCase {

    public interface Callback {
        void onComplete();

        void onPushError();

        void onPushInProgressError();

        void onSurveysNotFoundError();

        void onConversionError();

        void onNetworkError();

        void onInformativeError(String message);

        void onBannedOrgUnitError();
    }

    private static int DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR = 30;

    private static int DHIS_LIMIT_HOURS = 1;

    private IPushController mPushController;
    private BanOrgUnitExecutor mBanOrgUnitExecutor;

    public PushUseCase(IPushController pushController) {
        mPushController = pushController;
        mBanOrgUnitExecutor = new BanOrgUnitExecutor();
    }

    public void execute(final Callback callback) {
        if (mPushController.isPushInProgress()) {
            callback.onPushInProgressError();
            return;
        }

        mBanOrgUnitExecutor.isOrgUnitBanned(new BanOrgUnitExecutor.isOrgUnitBannedCallback() {
            @Override
            public void onSuccess(boolean isBanned) {
                if (isBanned) //TODO add user accept ban warning in APushServiceStrategy??
                {
                    callback.onBannedOrgUnitError();
                } else {
                    runPush(callback);
                }
            }

            @Override
            public void onError() {
                callback.onPushError();
            }
        });


    }

    private void runPush(final Callback callback) {
        mPushController.changePushInProgress(true);

        mPushController.push(new IPushController.IPushControllerCallback() {
            @Override
            public void onComplete() {
                mPushController.changePushInProgress(false);

                callback.onComplete();

                banOrgUnitIfRequired(callback);
            }

            @Override
            public void onError(Throwable throwable) {
                mPushController.changePushInProgress(false);

                if (throwable instanceof NetworkException) {
                    callback.onNetworkError();
                } else if (throwable instanceof ConversionException) {
                    callback.onConversionError();
                } else if (throwable instanceof SurveysToPushNotFoundException) {
                    callback.onSurveysNotFoundError();
                } else if (throwable instanceof ImportSummaryErrorException) {
                    callback.onInformativeError(throwable.getMessage());
                    banOrgUnitIfRequired(callback);
                } else {
                    callback.onPushError();
                }
            }
        });
    }

    private void banOrgUnitIfRequired(final Callback callback) {
        List<Survey> sentSurveys = Survey.getAllHideAndSentSurveys(
                DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR);

        if (isSurveysOverLimit(sentSurveys)) {
            mBanOrgUnitExecutor.banOrgUnit(new BanOrgUnitExecutor.banOrgUnitCallback() {
                @Override
                public void onSuccess() {
                    System.out.println("OrgUnit banned successfully");
                }

                @Override
                public void onError() {
                    callback.onPushError();
                }
            });
        }
    }

    private boolean isSurveysOverLimit(List<Survey> surveyList) {
        return true;

/*        int countDates = 0;

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
}

