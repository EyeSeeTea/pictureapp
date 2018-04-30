package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.StockSummary;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetStockSummaryUseCase implements UseCase {
    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;
    private ISurveyRepository mSurveyRepository;
    private IQuestionRepository mQuestionRepository;
    private Callback mCallback;

    public GetStockSummaryUseCase(
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            ISurveyRepository surveyRepository, IQuestionRepository questionRepository) {
        mAsyncExecutor = asyncExecutor;
        mMainExecutor = mainExecutor;
        mSurveyRepository = surveyRepository;
        mQuestionRepository = questionRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        try {
            List<StockSummary> drugValues = getStockSummaryValues();
            notifyGetStockValues(drugValues);
        } catch (Exception exception) {
            notifyError(exception);
        }

    }

    private List<StockSummary> getStockSummaryValues() {
        List<Survey> surveys = mSurveyRepository.getAllCompletedSurveys();
        return getDrugValuesFromSurveys(surveys);
    }

    private List<StockSummary> getDrugValuesFromSurveys(List<Survey> surveys) {
        List<Survey> stockSurveys = new ArrayList<>();
        for (Survey survey : surveys) {
            if (survey.getType() != Constants.SURVEY_NO_TYPE) {
                stockSurveys.add(survey);
            }
        }
        return initDrugsValuesList(stockSurveys);
    }


    private List<StockSummary> initDrugsValuesList(List<Survey> surveys) {
        List<StockSummary> drugsValuesList = new ArrayList<>();
        if (!surveys.isEmpty()) {
            for (Question question : mQuestionRepository.getQuestionsByProgram(surveys.get(
                    0).getProgram().getId())) {
                drugsValuesList.add(createStockSummaryRow(question, surveys));
            }
        }
        return drugsValuesList;
    }

    private StockSummary createStockSummaryRow(Question question, List<Survey> surveys) {
        String drugLabel = question.getName();
        int received = getValuesForDrugAndSurveyType(question, Constants.SURVEY_RECEIPT,
                surveys);
        int usedToday = getValuesForDrugAndSurveyTypeAfterDate(question, Constants.SURVEY_ISSUE,
                Utils.getTodayDate(), surveys);
        int expense = getValuesForDrugAndSurveyType(question, Constants.SURVEY_ISSUE,
                surveys);
        return new StockSummary(drugLabel, received, usedToday, expense);
    }


    private int getValuesForDrugAndSurveyType(Question questionRow, int surveyType,
            List<Survey> surveys) {
        int drugSum = 0;
        for (Survey survey : surveys) {
            if (survey.getType() == surveyType) {
                for (Question question : survey.getQuestions()) {
                    if (question.getUid().equals(questionRow.getUid())) {
                        drugSum += Integer.parseInt(question.getValue().getValue());
                    }
                }
            }
        }
        return drugSum;
    }

    private int getValuesForDrugAndSurveyTypeAfterDate(Question questionRow, int surveyType,
            Date date, List<Survey> surveys) {
        int drugSum = 0;
        for (Survey survey : surveys) {
            if (survey.getType() == surveyType && survey.getSurveyDate().after(date)) {
                for (Question question : survey.getQuestions()) {
                    if (question.getUid().equals(questionRow.getUid())) {
                        drugSum += Integer.parseInt(question.getValue().getValue());
                    }
                }
            }
        }
        return drugSum;
    }

    private void notifyGetStockValues(final List<StockSummary> drugValues) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onGetStockValues(drugValues);
            }
        });
    }

    private void notifyError(final Exception exception) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onError(exception);
            }
        });
    }


    public interface Callback {
        void onGetStockValues(List<StockSummary> drugValues);

        void onError(Exception e);
    }
}
