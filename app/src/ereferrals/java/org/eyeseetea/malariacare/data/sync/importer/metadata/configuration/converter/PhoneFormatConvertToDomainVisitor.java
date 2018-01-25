package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.converter;


import com.raizlabs.android.dbflow.annotation.NotNull;

import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.model
        .MetadataConfigurationsApi;
import org.eyeseetea.malariacare.domain.entity.PhoneFormat;

public class PhoneFormatConvertToDomainVisitor implements
        IConvertDomainDBVisitor<MetadataConfigurationsApi.PhoneFormat, PhoneFormat> {

    @NotNull
    @Override
    public PhoneFormat visit (
            MetadataConfigurationsApi.PhoneFormat apiPhoneFormat) {

        String phoneMask = parseToPhoneMask(apiPhoneFormat);
        String trunkPrefix = apiPhoneFormat.details.trunkPrefix;
        String prefixToPut = apiPhoneFormat.details.dialingCode;

        return new PhoneFormat(phoneMask, trunkPrefix, prefixToPut);
    }

    public String parseToPhoneMask(MetadataConfigurationsApi.PhoneFormat apiPhoneFormat) {
        StringBuilder regularExpression = new StringBuilder();

        for (int i = 0; i < apiPhoneFormat.accepted.size(); i++) {

            MetadataConfigurationsApi.PhoneFormat.AcceptedFormat acceptedFormat =
                    apiPhoneFormat.accepted.get(i);
            String regExTemplate = "^(#starts)\\d#length";

            String starts = scapeSpecialCharacters(acceptedFormat.starts);
            regExTemplate = regExTemplate.replace("#starts", starts);

            if (acceptedFormat.length != null) {
                int length = Integer.parseInt(acceptedFormat.length) - 1;
                regExTemplate = regExTemplate.replace("#length", "{" + length + "}");
            } else {
                regExTemplate = regExTemplate.replace("#length", "*");
            }

            boolean isNotTheLastItem = ((i + 1) < apiPhoneFormat.accepted.size());

            if (isNotTheLastItem) {
                regExTemplate += "|";
            }

            regularExpression.append(regExTemplate);
        }

        return regularExpression.toString();
    }

    private String scapeSpecialCharacters(String text) {
        switch (text) {
            case "+":
                text = "\\+";
                break;
        }
        return text;
    }

}
