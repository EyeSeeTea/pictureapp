package org.eyeseetea.malariacare.domain.service;

import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoucherSuffixDomainService {
    public String calculate(Survey survey, List<Question> questions){
        Map<String, Question> questionsMap = new HashMap<>();

        for (Question question : questions) {
            questionsMap.put(question.getUid(), question);
        }

        List<String> suffixItems = new ArrayList<>();

        for(Value value: survey.getValues()){
            Question question = questionsMap.get(value.getQuestionUId());

            if (question!= null && question.getVoucherCodeSuffix() != null &&
                    question.getVoucherCodeSuffix().suffix != null &&
                    question.getVoucherCodeSuffix().valueCondition != null){

                if (value.getValue().equals(question.getVoucherCodeSuffix().valueCondition)){
                    suffixItems.add(question.getVoucherCodeSuffix().suffix);
                }
            }
        }

        return createSuffixFromList("_",suffixItems);
    }

    private String createSuffixFromList(String separator, List<String> suffixItems) {

        if (suffixItems == null || suffixItems.size() <= 0) return "";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < suffixItems.size(); i++) {

            sb.append(suffixItems.get(i));

            // if not the last item
            if (i != suffixItems.size() - 1) {
                sb.append(separator);
            }
        }

        return "_" + sb.toString();
    }
}
