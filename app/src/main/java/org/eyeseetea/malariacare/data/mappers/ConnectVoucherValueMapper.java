package org.eyeseetea.malariacare.data.mappers;

import org.eyeseetea.malariacare.data.database.datasources.OptionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.QuestionLocalDataSource;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ConnectVoucherValueMapper {

    public static List<Value> mapValueFromConnectVoucher(HashMap<String, String> valuesPairKeyValue) {
        Iterator it = valuesPairKeyValue.entrySet().iterator();
        List<Value> values = addValues(it);
        return values;
    }

    private static List<Value> addValues(Iterator it){
        List<Value> values = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            Value value = addValue(pair.getKey().toString(), pair.getValue().toString());
            if(value!=null && !values.contains(value)) {
                values.add(value);
            }
            it.remove();
        }
        return values;
    }

    private static Value addValue(String questionUId, String value) {

        QuestionLocalDataSource questionLocalDataSource = new QuestionLocalDataSource();
        Question question = questionLocalDataSource.getByUId(questionUId);
        if(question==null){
            return null;
        }
        if(question.hasOptions()){
            OptionLocalDataSource optionLocalDataSource = new OptionLocalDataSource();
            Option option = optionLocalDataSource.recoveryOptionsByQuestionAndValue(questionUId, value);
            if(option!=null) {
                return new Value(value, questionUId, option.getCode());
            }else{
                //invalid option value should be ignored
                return null;
            }
        }else{
            return new Value(value, questionUId);
        }
    }
}
