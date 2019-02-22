package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ReadPolicy;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

public class CreateSurveyUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private ISurveyRepository mSurveyRepository;
    private IProgramRepository mProgramRepository;
    private IOrganisationUnitRepository mOrganisationUnitRepository;
    private IUserRepository mUserRepository;
    private Callback mCallback;
    private String mProgramUID;
    private int mSurveyType;


    public CreateSurveyUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            ISurveyRepository surveyRepository,
            IProgramRepository programRepository,
            IOrganisationUnitRepository organisationUnitRepository,
            IUserRepository userRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mSurveyRepository = surveyRepository;
        mProgramRepository = programRepository;
        mOrganisationUnitRepository = organisationUnitRepository;
        mUserRepository = userRepository;
    }

    public void execute(Callback callback, String programUID, int surveyType) {
        mCallback = callback;
        mProgramUID = programUID;
        mSurveyType = surveyType;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        try {
            UserAccount userAccount = mUserRepository.getLoggedUser();
            OrganisationUnit organisationUnit =
                    mOrganisationUnitRepository.getCurrentOrganisationUnit(
                            ReadPolicy.CACHE);
            Survey survey = new Survey(mProgramUID, organisationUnit.getUid(),
                    userAccount.getUserUid(),
                    mSurveyType);
            long idSurvey = mSurveyRepository.save(survey);
            survey.setId(idSurvey);
            notifySurveyCreated(survey);

        } catch (NetworkException e) {
            e.printStackTrace();
            notifyError(e);
        } catch (ApiCallException e) {
            e.printStackTrace();
            notifyError(e);
        } catch (Exception e){
            e.printStackTrace();
            notifyError(e);
        }
    }

    private void notifySurveyCreated(final Survey survey) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onCreateSurvey(survey);
            }
        });
    }

    private void notifyError(final Throwable throwable) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onErrorCreatingSurvey(throwable);
            }
        });
    }

    public interface Callback {
        void onCreateSurvey(Survey survey);

        void onErrorCreatingSurvey(Throwable e);
    }
}
