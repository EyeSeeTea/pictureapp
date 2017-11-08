package utils;

import org.eyeseetea.malariacare.domain.entity.PhoneFormat;

public class PhoneMask {

    public static boolean checkPhoneNumberByMask(String phoneValue, PhoneFormat phoneFormat) {

        //Empty  is ok
        if (phoneValue == null) {
            phoneValue = "";
        }
        return phoneValue.isEmpty() || phoneValue.matches(phoneFormat.getPhoneMask());
    }

    public static String applyValueTransformations(String value, PhoneFormat phoneFormat) {
        String valueText = value;
        if (!(valueText.startsWith("00") || valueText.startsWith("+"))) {
            if (phoneFormat.getTrunkPrefix().isEmpty()) {
                valueText = phoneFormat.getPrefixtToPut() + valueText;
            } else if (value.startsWith(phoneFormat.getTrunkPrefix())) {
                valueText = phoneFormat.getPrefixtToPut() + valueText.substring(
                        phoneFormat.getTrunkPrefix().length());
            }
        }

        return valueText;
    }
}
