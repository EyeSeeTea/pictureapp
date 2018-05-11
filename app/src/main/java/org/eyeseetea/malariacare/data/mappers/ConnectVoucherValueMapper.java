package org.eyeseetea.malariacare.data.mappers;

import org.eyeseetea.malariacare.data.database.datasources.OptionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.QuestionLocalDataSource;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Value;

public class ConnectVoucherValueMapper {

    public static Value mapValueFromConnectVoucher(String questionUId, String value) {
        QuestionLocalDataSource questionLocalDataSource = new QuestionLocalDataSource();
        if(!questionLocalDataSource.existsByUId(questionUId)){
            return null;
        }
        if(questionLocalDataSource.hasOptions(questionUId)){
            OptionLocalDataSource optionLocalDataSource = new OptionLocalDataSource();
            Option option = optionLocalDataSource.recoveryOptionsByQuestionAndValue(questionUId, value);
            if(option!=null) {
                return new Value(value, questionUId, option.getCode());
            }else{
                return null;
            }
        }else{
            return new Value(value, questionUId);
        }
    }
}
