package org.eyeseetea.malariacare.domain.utils;

import org.eyeseetea.malariacare.SplashScreenActivity;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ValueLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.mappers.ConnectVoucherValueMapper;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.SaveSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveValueUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.strategies.SplashActivityStrategy;

import java.util.HashMap;
import java.util.List;

public class IntentSurveyCreator {
    HashMap<String, String> keyValueList;
    SplashActivityStrategy.Callback callback;
    IMainExecutor mainExecutor;
    IAsyncExecutor asyncExecutor;

    public void createFromConnectVoucher(HashMap<String, String> values, SplashActivityStrategy.Callback callback) {
        this.keyValueList=values;
        this.callback = callback;
        mainExecutor = new UIThreadExecutor();
        asyncExecutor = new AsyncExecutor();
        openNewSurvey();
    }


    private void openNewSurvey() {
        Survey survey = createNewConnectSurvey();

        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        SaveSurveyUseCase saveSurveyUseCase = new SaveSurveyUseCase(asyncExecutor, mainExecutor, surveyRepository);

        saveSurveyUseCase.execute(survey, new SaveSurveyUseCase.Callback() {
            @Override
            public void onSurveySaved(Survey survey) {
                saveValuesFromIntent(survey);
            }
        });
    }

    private Survey createNewConnectSurvey() {
        ProgramDB programDB = ProgramDB.findById(PreferencesEReferral.getUserProgramId());
        UserAccount userAccount = new UserAccount(Session.getUserDB().getName(),
                Session.getUserDB().getUid(),
                PreferencesState.getCredentialsFromPreferences().isDemoCredentials());
        Program program = new Program(programDB.getUid(), programDB.getUid());
        return Survey.createNewConnectSurvey(program, userAccount);
    }

    private void saveValuesFromIntent(Survey survey) {
        List<Value> values = ConnectVoucherValueMapper.mapValueFromConnectVoucher(keyValueList);
        if(values.size()>0) {
            saveValues(survey, values);
        }
    }


    private void saveValues(Survey survey, List<Value> values) {
        SaveValueUseCase saveValueUseCase = new SaveValueUseCase(asyncExecutor, mainExecutor,
                new ValueLocalDataSource());
        saveValueUseCase.execute(new SaveValueUseCase.Callback() {
            @Override
            public void onValueSaved(List<Value> value) {
                callback.onSuccess();
            }
        }, survey.getId(), values);
    }

}
