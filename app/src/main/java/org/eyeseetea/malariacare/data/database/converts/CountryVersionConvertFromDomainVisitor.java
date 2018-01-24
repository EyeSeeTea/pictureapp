package org.eyeseetea.malariacare.data.database.converts;


import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.Configuration;

public class CountryVersionConvertFromDomainVisitor implements IConvertDomainDBVisitor<
        Configuration.CountryVersion, CountryVersionDB> {

    @Override
    public CountryVersionDB visit(Configuration.CountryVersion domainModel) {
        CountryVersionDB countryVersionDB = new CountryVersionDB();
        countryVersionDB.setCountry(domainModel.getCountry());
        countryVersionDB.setVersion(domainModel.getVersion());
        countryVersionDB.setLast_update(domainModel.getLastUpdate());
        countryVersionDB.setUid(domainModel.getUid());
        return countryVersionDB;
    }
}
