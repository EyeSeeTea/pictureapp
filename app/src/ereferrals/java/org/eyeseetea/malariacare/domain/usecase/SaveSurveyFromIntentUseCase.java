package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.mappers.ConnectVoucherMapper;
import org.eyeseetea.malariacare.data.mappers.ConnectVoucherValueMapper;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAuthRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;
import org.eyeseetea.malariacare.domain.entity.intent.ConnectVoucher;

import java.io.IOException;
import java.util.List;

public class SaveSurveyFromIntentUseCase implements UseCase {

    public interface Callback {
        void onSurveySaved(Survey survey);
        void onEmptySurvey();
        void onInvalidIntentJson();
        void onInvalidProgramOrUser();
    }

    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private ISurveyRepository mSurveyRepository;
    private IValueRepository mValueRepository;
    private IAuthRepository mAuthRepository;
    private Callback mCallback;
    private String mConnectVoucherJson;
    private ConnectVoucher mConnectVoucher;
    private Survey mSurvey;

    public SaveSurveyFromIntentUseCase(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            ISurveyRepository surveyRepository,
            IValueRepository valueRepository,
            IAuthRepository authRepository,
            String connectVoucherJson) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mSurveyRepository = surveyRepository;
        mSurveyRepository = surveyRepository;
        mValueRepository = valueRepository;
        mAuthRepository = authRepository;
        mConnectVoucherJson = connectVoucherJson;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }


    @Override
    public void run() {
        if(mConnectVoucherJson == null || mConnectVoucherJson.isEmpty()){
            notifyEmptySurvey();
            return;
        }
        try{
            mConnectVoucher = ConnectVoucherMapper.parseJson(mConnectVoucherJson);
        } catch (IOException e) {
            notifyInvalidIntentJson();
            return;
        }

        if(mConnectVoucher.getValues()==null || mConnectVoucher.getValues().size()==0) {
            notifyEmptySurvey();
            return;
        }
        Auth auth = mConnectVoucher.getAuth();
        if(auth==null){
            auth = new Auth("","");
        }

        mAuthRepository.saveAuth(auth);
        mSurveyRepository.removeInProgress();
        mSurvey = mSurveyRepository.createNewSurvey();
        if(mSurvey==null){
            notifyInvalidProgramOrUser();
            return;
        }
        mSurvey.setId(mSurveyRepository.save(mSurvey));
        saveValuesFromIntent(mSurvey);
    }

    private void saveValuesFromIntent(Survey survey) {
        List<Value> values = ConnectVoucherValueMapper.mapValueFromConnectVoucher(mConnectVoucher.getValues());
        if(values.size()>0) {
            saveValues(survey, values);
        }
    }

    private void saveValues(Survey survey, List<Value> values) {
        for(Value value : values) {
            mValueRepository.saveValue(value, survey.getId());
        }
        notifySurveySaved();
    }

    private void notifySurveySaved() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSurveySaved(mSurvey);
            }
        });
    }

    private void notifyEmptySurvey() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onEmptySurvey();
            }
        });
    }

    private void notifyInvalidIntentJson() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onInvalidIntentJson();
            }
        });
    }

    private void notifyInvalidProgramOrUser() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onInvalidProgramOrUser();
            }
        });
    }

}
