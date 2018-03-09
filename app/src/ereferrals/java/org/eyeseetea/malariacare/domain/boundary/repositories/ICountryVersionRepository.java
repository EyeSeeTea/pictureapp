package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Configuration;

public interface ICountryVersionRepository {

    Configuration.CountryVersion getCountryVersionForUID(String uid);
}
