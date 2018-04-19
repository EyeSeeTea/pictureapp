package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.usecase.GetSurveysByProgram;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockTablePresenter {
    private static final String STOCK_PROGRAM_UID = "zaRb1RuqGVk";
    private static final String ASMQ_25_50_UID = "WA1ouIgQyCr";

    View mView;
    private GetSurveysByProgram mGetSurveysByProgram;

    public StockTablePresenter(
            GetSurveysByProgram getSurveysByProgram) {
        mGetSurveysByProgram = getSurveysByProgram;
    }

    public void attachView(View view) {
        mView = view;
        initValues();
    }

    private void initValues() {
        mGetSurveysByProgram.execute(new GetSurveysByProgram.Callback() {
            @Override
            public void onGetSurveys(List<Survey> surveys) {
                mView.showStockValues(initDrugsValuesList(surveys));
            }
        }, STOCK_PROGRAM_UID);
    }

    private List<String[]> initDrugsValuesList(List<Survey> surveys) {
        List<String[]> drugsValuesList = new ArrayList<>();
        drugsValuesList.add(
                createDrugRow("ipc_issueEntry_q_stock_asmq_25_50", ASMQ_25_50_UID, surveys));
        String[] asmq100_200 = new String[5];
        asmq100_200[0] = "ipc_issueEntry_q_stock_asmq_100_200";
        String[] pq = new String[5];
        pq[0] = "ipc_issueEntry_q_stock_primaquine";
        String[] RDT = new String[5];
        RDT[0] = "ipc_issueEntry_q_stock_rdt";

        return null;
    }

    private String[] createDrugRow(String drugString, String drugUID, List<Survey> surveys) {
        String[] drugRow = new String[5];
        drugRow[0] = drugString;
        drugRow[1] = getValuesForDrugAndSurveyType(drugUID, Constants.SURVEY_RECEIPT,
                surveys);
        drugRow[2] = getValuesForDrugAndSurveyTypeAfterDate(drugUID, Constants.SURVEY_ISSUE,
                Utils.getTodayDate(), surveys);
        drugRow[3] = getValuesForDrugAndSurveyType(drugUID, Constants.SURVEY_ISSUE,
                surveys);
//        drugRow[3] =
        return drugRow;
    }


    private String getValuesForDrugAndSurveyType(String drugUID, int surveyType,
            List<Survey> surveys) {
        int drugSum = 0;
        for (Survey survey : surveys) {
            if (survey.getType() == surveyType) {
                for (Question question : survey.getQuestions()) {
                    if (question.getUid().equals(drugUID)) {
                        drugSum++;
                    }
                }
            }
        }
        return drugSum == 0 ? "" : String.valueOf(drugSum);
    }

    private String getValuesForDrugAndSurveyTypeAfterDate(String drugUID, int surveyType,
            Date date, List<Survey> surveys) {
        int drugSum = 0;
        for (Survey survey : surveys) {
            if (survey.getType() == surveyType && survey.getSurveyDate().after(date)) {
                for (Question question : survey.getQuestions()) {
                    if (question.getUid().equals(drugUID)) {
                        drugSum++;
                    }
                }
            }
        }
        return drugSum == 0 ? "" : String.valueOf(drugSum);
    }

    public void detachView() {
        mView = null;
    }


    public interface View {
        void showStockValues(List<String[]> drugsValuesList);

        void showError();

    }


}
