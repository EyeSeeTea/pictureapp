package org.eyeseetea.malariacare.data.sync.importer.converter;


import static junit.framework.TestCase.assertTrue;

import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.converter
        .PhoneFormatConvertToDomainVisitor;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model.CountryMetadataApi;
import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.entity.PhoneFormat;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class PhoneFormatConvertToDomainVisitorShould {

    private PhoneFormatConvertToDomainVisitor converter;
    private PhoneFormat domainPhoneFormat;

    @Before
    public void setup() {
        converter = new PhoneFormatConvertToDomainVisitor();
    }

    @Test
    public void convert_from_api_to_domain_phone_format() throws InvalidPhoneException {

        whenConvertFromApiModelToDomainModel();

        thenAssertPhoneFormatWasConverted();

    }

    private void thenAssertPhoneFormatWasConverted() throws InvalidPhoneException {

        assertTrue(domainPhoneFormat != null);

        //Not throw a InvalidPhoneException
         new Phone("+255753140000", domainPhoneFormat);
    }

    private void whenConvertFromApiModelToDomainModel() {
        CountryMetadataApi.PhoneFormat apiPhoneFormat = givenAAPIPhoneFormat();
        domainPhoneFormat = converter.visit(apiPhoneFormat);
    }

    private CountryMetadataApi.PhoneFormat givenAAPIPhoneFormat() {
        CountryMetadataApi.PhoneFormat phoneFormat = new CountryMetadataApi
                .PhoneFormat();

        phoneFormat.accepted = new ArrayList<>();
        phoneFormat.accepted.add(buildAccept("6", "9", ""));
        phoneFormat.accepted.add(buildAccept("7", "9", "national"));
        phoneFormat.accepted.add(buildAccept("+", null, ""));
        phoneFormat.accepted.add(buildAccept("00", null, "national"));

        phoneFormat.details = new CountryMetadataApi.PhoneFormat.FormatDetails();

        phoneFormat.details.trunkPrefix = "0";
        phoneFormat.details.dialingCode = "+255";


        return phoneFormat;
    }

    private CountryMetadataApi.PhoneFormat.AcceptedFormat buildAccept(String starts,
                                                                      String length, String comment) {
        CountryMetadataApi.PhoneFormat.AcceptedFormat
                acceptedFormat = new CountryMetadataApi.PhoneFormat.AcceptedFormat();
        acceptedFormat.starts = starts;
        acceptedFormat.length = length;
        acceptedFormat.comment = comment;
        return acceptedFormat;

    }
}
