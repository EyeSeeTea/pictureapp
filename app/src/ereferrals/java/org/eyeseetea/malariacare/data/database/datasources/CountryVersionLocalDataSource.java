package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICountryVersionRepository;
import org.eyeseetea.malariacare.domain.entity.Configuration;


public class CountryVersionLocalDataSource implements ICountryVersionRepository {
    @Override
    public Configuration.CountryVersion getCountryVersionForUID(String uid) {
        CountryVersionDB countryVersionDB = CountryVersionDB.getCountryVersionByUID(uid);
        return convertFromCountryVersionDB(countryVersionDB);
    }

    private Configuration.CountryVersion convertFromCountryVersionDB(
            CountryVersionDB countryVersionDB) {
        if (countryVersionDB != null) {
            return Configuration.CountryVersion.newBuilder()
                    .country(countryVersionDB.getCountry())
                    .uid(countryVersionDB.getUid())
                    .reference(countryVersionDB.getReference())
                    .lastUpdate(countryVersionDB.getLast_update())
                    .version(countryVersionDB.getVersion())
                    .build();
        }
        return null;
    }
}
