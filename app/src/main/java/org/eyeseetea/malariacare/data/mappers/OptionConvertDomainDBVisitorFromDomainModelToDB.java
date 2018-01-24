package org.eyeseetea.malariacare.data.mappers;


import com.raizlabs.android.dbflow.annotation.NotNull;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.Option;

public class OptionConvertDomainDBVisitorFromDomainModelToDB implements
        IConvertDomainDBVisitor<Option, OptionDB> {

    @NotNull
    @Override
    public OptionDB visit(@NotNull Option domainModel) {
        OptionDB dbModel = new OptionDB();
        dbModel.setCode(domainModel.getCode());
        dbModel.setName(domainModel.getName());

        return dbModel;
    }
}
