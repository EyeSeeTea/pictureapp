package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;

import java.util.ArrayList;
import java.util.List;

public class GetSurveyByUidUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private Callback mCallback;
    private ISurveyRepository surveyRepository;
    private String surveyUId;

    public GetSurveyByUidUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            ISurveyRepository surveyRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        this.surveyRepository = surveyRepository;
    }

    public void execute(String surveyUid, Callback callback) {
        mCallback = callback;
        surveyUId = surveyUid;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        try{
            Survey survey = surveyRepository.getSurveyByUid(surveyUId);
            final List<Value> orderValues = orderValues(
                    survey.getValues());

            survey = new Survey(
                    survey.getId(),
                    survey.getUid(),
                    survey.getVoucherUid(),
                    survey.getStatus(),
                    null,
                    survey.getSurveyDate(),
                    survey.getProgramUid(),
                    survey.getOrgUnitUid(),
                    survey.getUserUid(),
                    survey.getType(),
                    orderValues,
                    survey.getVisibleVoucherUid());

            final Survey finalSurvey = survey;
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onSuccess(finalSurvey);
                }
            });
        } catch (Exception e){

        }
    }


    //TODO: Review this order method on the future, many coupled code to navigator and db models
    //For the moment is duplicated from GetReviewValuesBySurveyIdUseCaseStrategy
    public List<Value> orderValues(List<Value> values) {
        List<Value> orderedList = new ArrayList<>();
        //TODO refactor navigation controller to be in domain layer (issue 1354)
        NavigationController navigationController = Session.getNavigationController();
        navigationController.first();
        String uidNextQuestion = null;
        do {
            QuestionDB currentQuestion = navigationController.getCurrentQuestion();
            if (TabDB.isMultiQuestionTab(currentQuestion.getHeaderDB().getTabDB()
                    .getType())) {
                List<QuestionDB> tabQuestionDBs = currentQuestion.getQuestionsByTab(
                        currentQuestion.getHeaderDB()
                                .getTabDB());
                for (QuestionDB questionDB : tabQuestionDBs) {
                    for (Value value : values) {
                        if (value.getQuestionUId().equals(questionDB.getUid())) {
                            orderedList.add(value);
                            break;
                        }
                    }
                }
                //TODO put uidNextQuestion=navigationController.next() not working with this now
                uidNextQuestion = null;
            } else {
                for (Value value : values) {
                    if (value.getQuestionUId() != null) {
                        if (value.getQuestionUId().equals(currentQuestion.getUid())) {
                            orderedList.add(value);
                            uidNextQuestion = navigationController.next(
                                    OptionDB.findByCode(value.getOptionCode())).getUid();
                        }
                    }
                }
            }
        } while (uidNextQuestion != null);
        return orderedList;
    }

    public interface Callback {
        void onSuccess(Survey survey);
        void onError(Exception ex);
    }
}
