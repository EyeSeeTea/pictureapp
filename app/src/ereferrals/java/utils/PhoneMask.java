package utils;

/**
 * Created by ina on 02/08/2016.
 */
public class PhoneMask {

    /**
     * Plain telephone mask: number stars with 00|+ and have 11 more digits or starts with
     * 07|06|+|+2556|+2557 and have 9 more digits
     */
    public static final String PLAIN_PHONE_NUMBER_MASK =
            "((^(00)\\d{10,15})|(^(\\+)\\d{10,15}$)|(^(06|07|\\+2556|\\+2557)\\d{9}$))";

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

    public static String applyValueTransformations(String value){
        String valueText = value;
        if (valueText.startsWith("00")) {
            valueText = "+" + valueText.substring(2,valueText.length());
        } else if (valueText.startsWith("06")) {
            valueText = "+2556" + valueText.substring(2,valueText.length());
        } else if (valueText.startsWith("07")) {
            valueText = "+2557" + valueText.substring(2,valueText.length());
        }
        return valueText;
    }
}
