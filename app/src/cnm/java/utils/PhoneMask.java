package utils;

import org.eyeseetea.malariacare.domain.entity.PhoneFormat;

/**
 * Created by ina on 02/08/2016.
 */
public class PhoneMask {

    /**
     * Formatted telephone mask: 0NN NNN NNN{N}
     */
    public static final String FORMATTED_PHONENUMBER_MASK = "0\\d{2} \\d{3} \\d{3,4}";

    /**
     * PLAIN telephone mask: *
     */
    public static final String PLAIN_PHONENUMBER_MASK = "\\d*";

    /**
     * Formats number according to mask 0NN NNN NNN{N}
     */
    public static String formatPhoneNumber(String phoneValue) {
        //Empty -> nothing to format
        if (phoneValue == null) {
            phoneValue = "";
        }

        //Already formatted -> done
        if (phoneValue.isEmpty() || phoneValue.matches(FORMATTED_PHONENUMBER_MASK)) {
            return phoneValue;
        }

        //0NNNNNNNN{N} -> 0NN NNN NNN{N}
        String formattedNumber = phoneValue.substring(0, 3) + " " + phoneValue.substring(3, 6) + " "
                + phoneValue.substring(6, phoneValue.length());
        return formattedNumber;
    }

    /**
     * Checks if the given string corresponds a correct phone number according to mask:
     * 0NN NNN NNN{N}
     *
     * @return true|false
     */
    public static boolean checkPhoneNumberByMask(String phoneValue, PhoneFormat phoneFormat) {

        //Empty  is ok
        if (phoneValue == null) {
            phoneValue = "";
        }
        return phoneValue.isEmpty() || phoneValue.replace(" ", "").matches(
                FORMATTED_PHONENUMBER_MASK) || phoneValue.replace(" ", "").matches(
                PLAIN_PHONENUMBER_MASK);
    }

    public static String applyValueTransformations(String value, PhoneFormat phoneFormat) {
        return value;
    }
}
