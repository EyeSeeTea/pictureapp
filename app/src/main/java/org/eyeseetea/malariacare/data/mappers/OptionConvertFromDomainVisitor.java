package org.eyeseetea.malariacare.data.mappers;


import com.raizlabs.android.dbflow.annotation.NotNull;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.Option;

public class OptionConvertFromDomainVisitor implements
        IConvertDomainDBVisitor<Option, OptionDB> {

    @NotNull
    @Override
    public OptionDB visit(@NotNull Option domainModel) {
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
