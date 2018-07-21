package utils;

import org.eyeseetea.malariacare.domain.entity.PhoneFormat;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneProcessor {

    public static boolean checkPhoneNumberByMask(String phoneValue, PhoneFormat phoneFormat) {

        //Empty  is ok
        if (phoneValue == null) {
            phoneValue = "";
        }
        applyValueTransformations(phoneValue, phoneFormat);
        return phoneValue.isEmpty() || phoneValue.matches(phoneFormat.getPhoneMatcher());
    }

    public static String applyValueTransformations(String value, PhoneFormat phoneFormat) {
        String valueText = value;
        String national = String.format(PhoneFormat.GROUP_TOKEN, PhoneFormat.NATIONAL);
        String international = String.format(PhoneFormat.GROUP_TOKEN, PhoneFormat.INTERNATIONAL);

            if (valueText.matches(phoneFormat.getPhoneMatcher())) {
                String[] regexps = phoneFormat.getPhoneMask().split(PhoneFormat.END_TOKEN);
                for(int i=0; i<regexps.length; i++) {
                    if (regexps[i].contains(national)) {
                        if (phoneFormat.getTrunkPrefix().isEmpty()) {
                            String realRegExp = regexps[i].replace(national, "");
                            if (valueText.matches(realRegExp)) {
                                valueText = phoneFormat.getTrunkPrefix() + valueText;
                                return valueText;
                            }
                        }
                    }else if(regexps[i].contains(international)){
                        if (phoneFormat.getPrefixtToPut().isEmpty()) {
                            String realRegExp = regexps[i].replace(international, "");
                            if (valueText.matches(realRegExp)) {
                                valueText = valueText.substring(0, valueText.indexOf("\\d"));
                                valueText = phoneFormat.getPrefixtToPut() + valueText;
                                return valueText;
                            }
                        }
                    }
                }
            }

        return valueText;
    }
}
