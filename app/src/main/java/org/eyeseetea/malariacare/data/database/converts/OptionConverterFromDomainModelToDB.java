package org.eyeseetea.malariacare.data.database.converts;


import com.raizlabs.android.dbflow.annotation.NotNull;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.domain.boundary.converters.IConverter;
import org.eyeseetea.malariacare.domain.entity.Option;

public class OptionConverterFromDomainModelToDB implements IConverter<Option, OptionDB> {

    @NotNull
    @Override
    public OptionDB convert(@NotNull Option domainModel) {
        OptionDB dbModel = new OptionDB();
        dbModel.setCode(domainModel.getCode());
        dbModel.setName(domainModel.getName());
        dbModel.setId_option_attribute_fk(getAttributeIdFrom(domainModel));
        return dbModel;
    }

    private long getAttributeIdFrom(Option domainOption) {
        return (domainOption.getAttribute() != null) ? domainOption.getAttribute().getId() : 0;
    }
}
