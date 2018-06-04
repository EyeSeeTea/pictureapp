package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOptionRepository;
import org.eyeseetea.malariacare.domain.entity.Option;

public class OptionLocalDataSource implements IOptionRepository {
    @Override
    public Option recoveryOptionsByQuestionAndValue(String questionUId, String value) {
        OptionDB optionDB = OptionDB.getOptionsByQuestionAndValue(questionUId, value);
        if(optionDB == null){
            return null;
        }
        optionDB = OptionDB.getById(optionDB.getId_option());//fix Option fields (dbflow bug)
        return new Option(optionDB.getId_option(), optionDB.getCode(), optionDB.getName());
    }
}
