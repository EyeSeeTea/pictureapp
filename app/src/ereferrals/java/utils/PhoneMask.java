package utils;

import org.eyeseetea.malariacare.domain.entity.PhoneFormat;

/**
 * Created by ina on 02/08/2016.
 */
public class PhoneMask {

    /**
     * Plain telephone mask: number stars with 00|+ and have 11 more digits or starts with
     * 07|06|+|+2556|+2557 and have 9 more digits
     */
    public static final String PLAIN_PHONE_NUMBER_MASK = "";

    public static String formatPhoneNumber(String phoneValue) {
        //Empty -> nothing to format
        if (phoneValue == null) {
            phoneValue = "";
        }

        //Already formatted -> done
        if (phoneValue.isEmpty() || phoneValue.matches(PLAIN_PHONE_NUMBER_MASK)) {
            return phoneValue;
        }

        return phoneValue;
    }

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
