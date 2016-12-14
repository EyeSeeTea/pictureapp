package utils;

/**
 * Created by ina on 02/08/2016.
 */
public class PhoneMask {

    /**
     * Plain telephone mask: between xxxxxxx = 7 and xxxxxxxxxxxxxxx = 15
     */
    public static final String PLAIN_PHONE_NUMBER_MASK =
            "^\\d{5,15}$";

    /**
     * Formats number according to mask (xxx) xxx-xxx = 9 (020) xxxx-xxxx = 11  (030) xxx-xxxx = 10
     */
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

    /**
     * Checks if the given string corresponds a correct phone number according to mask:
     * (xxx) xxx-xxx = 9 (020) xxxx-xxxx = 11  (030) xxx-xxxx = 10
     *
     * @return true|false
     */
    public static boolean checkPhoneNumberByMask(String phoneValue) {

        //Empty  is ok
        if (phoneValue == null) {
            phoneValue = "";
        }
        return phoneValue.isEmpty() || phoneValue.matches(PLAIN_PHONE_NUMBER_MASK);
    }
}
