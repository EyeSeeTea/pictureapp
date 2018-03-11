package org.eyeseetea.malariacare.data.database.converts;


import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.database.model.PhoneFormatDB;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.PhoneFormat;

public class PhoneFormatConverterFromDomainToDB implements
        IConvertDomainDBVisitor<PhoneFormat, PhoneFormatDB> {
    @Override
    public PhoneFormatDB visit(@Nullable PhoneFormat domainModel) {

        if (domainModel == null) return null;

        PhoneFormatDB phoneFormatDB = new PhoneFormatDB();
        phoneFormatDB.setPhoneMask(domainModel.getPhoneMask());
        phoneFormatDB.setPrefixToPut(domainModel.getPrefixtToPut());
        phoneFormatDB.setTrunkPrefix(domainModel.getTrunkPrefix());
        phoneFormatDB.setPhoneMask(domainModel.getPhoneMask());

        return phoneFormatDB;
    }
}
