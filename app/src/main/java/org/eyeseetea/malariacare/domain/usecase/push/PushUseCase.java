package org.eyeseetea.malariacare.domain.usecase.push;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ClosedUserPushException;
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

        void onClosedUser();

        void onBannedOrgUnitError();

        void onReOpenOrgUnit();

        void onApiCallError();
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
            public void onSuccess(Boolean isBanned) {
                if(isBanned == null){
                    callback.onApiCallError();
                    return;
                }
                OrgUnit orgUnit = OrgUnit.findByName(PreferencesState.getInstance().getOrgUnit());
                if (isBanned)
                {
                    if (orgUnit!=null && !orgUnit.isBanned()) {
                        orgUnit.setBan(true);
                        orgUnit.save();
                        callback.onBannedOrgUnitError();

                    }
                } else {
                    if (orgUnit!=null && orgUnit.isBanned()) {
                        orgUnit.setBan(false);
                        orgUnit.save();
                        callback.onReOpenOrgUnit();
                    }
                    runPush(callback);
                }
            }

            @Override
            public void onError() {
                callback.onPushError();
            }
        });


    }

    private void resetOrgUnit() {
        //TODO: use case should not invoke directly PreferenceState because belongs to the outer
        // layer
        PreferencesState.getInstance().saveStringPreference(R.string.org_unit, "");
        PreferencesState.getInstance().reloadPreferences();
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
                }else if (throwable instanceof ClosedUserPushException){
                    callback.onClosedUser();
                }else if (throwable instanceof ApiCallException){
                    callback.onApiCallError();
                } else {
                    callback.onPushError();
                }
            }
        });
    }

    private void banOrgUnitIfRequired(final Callback callback) {
        //TODO: use case should not invoke directly Survey because belongs to the outer layer
        List<Survey> sentSurveys = Survey.getAllHideAndSentSurveys(
                DHIS_LIMIT_SENT_SURVEYS_IN_ONE_HOUR);

        if (isSurveysOverLimit(sentSurveys)) {
            mBanOrgUnitExecutor.banOrgUnit(new BanOrgUnitExecutor.banOrgUnitCallback() {
                @Override
                public void onSuccess() {
                    System.out.println("OrgUnit banned successfully");
                    resetOrgUnit();
                }

                @Override
                public void onError() {
                    callback.onPushError();
                }
            });
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
}

