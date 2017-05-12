package utils;

/**
 * Created by ina on 02/08/2016.
 */
public class PhoneMask {

    /**
     * Plain telephone mask: between xxxxxxx = 7 and xxxxxxxxxxxxxxx = 15
     */
    public static final String PLAIN_PHONE_NUMBER_MASK =
            "^\\d{7,15}$";

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
    
    public static boolean checkPhoneNumberByMask(String phoneValue) {

        //Empty  is ok
        if (phoneValue == null) {
            phoneValue = "";
        }
        return phoneValue.isEmpty() || phoneValue.matches(PLAIN_PHONE_NUMBER_MASK);
    }
}
