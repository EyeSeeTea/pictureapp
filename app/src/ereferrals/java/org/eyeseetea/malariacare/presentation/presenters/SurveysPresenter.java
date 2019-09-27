package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.DeleteSurveyByUidUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetQuestionsByProgramUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSurveysByProgram;
import org.eyeseetea.malariacare.domain.usecase.GetUserProgramUseCase;
import org.eyeseetea.malariacare.presentation.models.SurveyViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveysPresenter {

    private View view;

    private final GetUserProgramUseCase getUserProgramUseCase;
    private final GetSurveysByProgram getSurveysByProgram;
    private final GetQuestionsByProgramUseCase getQuestionsByProgramUseCase;
    private final DeleteSurveyByUidUseCase deleteSurveyUseCase;

    private List<Survey> surveys;
    private String programUid;
    private Map<String, Question> questions;

    public SurveysPresenter(
            GetUserProgramUseCase getUserProgramUseCase,
            GetSurveysByProgram getSurveysByProgram,
            GetQuestionsByProgramUseCase getQuestionsByProgramUseCase,
            DeleteSurveyByUidUseCase deleteSurveyUseCase) {
        this.getUserProgramUseCase = getUserProgramUseCase;
        this.getSurveysByProgram = getSurveysByProgram;
        this.getQuestionsByProgramUseCase = getQuestionsByProgramUseCase;
        this.deleteSurveyUseCase = deleteSurveyUseCase;
    }

    public void attachView(View view) {
        this.view = view;

        loadData();
    }

    public void detachView() {
        view = null;
    }

    public void selectSurvey(String surveyUid) {
        if (view != null) {
            view.navigateToSurvey(surveyUid);
        }
    }

    public void refresh() {
        loadSurveys();
    }

    public void deleteSurvey(String surveyUid) {
        deleteSurveyUseCase.execute(new DeleteSurveyByUidUseCase.Callback() {
            @Override
            public void onSurveyDeleted() {
                loadSurveys();
            }
        }, surveyUid);
    }

    private void loadData() {
        getUserProgramUseCase.execute(new GetUserProgramUseCase.Callback() {
            @Override
            public void onSuccess(Program program) {
                SurveysPresenter.this.programUid = program.getId();
                getQuestions();
            }

            @Override
            public void onError() {
                System.out.println("An error has occurred loading user program");
            }
        });
    }

    private void getQuestions() {
        getQuestionsByProgramUseCase.execute(new GetQuestionsByProgramUseCase.Callback() {
            @Override
            public void onGetQuestions(List<Question> questions) {

                SurveysPresenter.this.questions = new HashMap<>();

                for (Question question : questions) {
                    SurveysPresenter.this.questions.put(question.getUid(), question);
                }

                loadSurveys();
            }
        }, programUid);
    }

    private void loadSurveys() {
        getSurveysByProgram.execute(new GetSurveysByProgram.Callback() {
            @Override
            public void onGetSurveysSuccess(List<Survey> surveys) {
                SurveysPresenter.this.surveys = surveys;

                showSurveys();

            }

            @Override
            public void onGetSurveysError(Exception e) {
                if (view != null) {
                    view.showErrorLoadingSurveys();
                }
            }
        }, programUid);
    }

    private void showSurveys() {
        List<SurveyViewModel> surveyViewModels = mapToViewModelList(this.surveys);

        if (view != null) {
            view.showSurveys(surveyViewModels);
        }
    }

    private List<SurveyViewModel> mapToViewModelList(List<Survey> surveys) {
        List<SurveyViewModel> surveyViewModels = new ArrayList<>();

        for (Survey survey : surveys) {
            SurveyViewModel surveyViewModel = mapToViewModel(survey);

            surveyViewModels.add(surveyViewModel);
        }

        return surveyViewModels;
    }

    private SurveyViewModel mapToViewModel(Survey survey) {
        Date eventDate = survey.getSurveyDate();
        String uid = survey.getUId();
        String voucherUid = survey.getVoucherUid();
        String visibleVoucherUid = survey.getVisibleVoucherUid();
        boolean isCompleted = survey.isCompleted();
        boolean hasPhone = survey.hasPhone();
        boolean noIssueVoucher = survey.noIssueVoucher();
        int status = survey.getStatus();

        List<String> importantValues = new ArrayList<>();
        List<String> visibleValues = new ArrayList<>();

        for (Value value : survey.getValues()) {
            if (value.getVisibility() == Question.Visibility.IMPORTANT) {
                importantValues.add(getValue(value));
            } else if (value.getVisibility() == Question.Visibility.VISIBLE) {
                visibleValues.add(getValue(value));
            }
        }

        SurveyViewModel surveyViewModel =
                new SurveyViewModel(eventDate, uid, voucherUid, visibleVoucherUid, isCompleted,
                        hasPhone, noIssueVoucher, importantValues, visibleValues, status);

        return surveyViewModel;
    }

    private String getValue(Value value) {
        Option optionSelected = null;

        if (value.getOptionCode() != null) {
            for (Option option : questions.get(value.getQuestionUId()).getOptions()) {
                if (option.getCode().equals(value.getOptionCode())) {
                    optionSelected = option;
                }
            }
        }

        String textValue;

        if (optionSelected != null) {
            textValue = optionSelected.getName();
        } else {
            textValue = value.getValue();
        }

        return textValue;
    }

    public interface View {
        void showSurveys(List<SurveyViewModel> surveyViewModels);
        void showErrorLoadingSurveys();

        void navigateToSurvey(String surveyUid);
    }
}