package utils;

/**
 * Created by ina on 02/08/2016.
 */
public class PhoneMask {

    /**
     * Formatted telephone mask: (xxx) xxx-xxx = 9 (020) xxxx-xxxx = 11  (030) xxx-xxxx = 10
     */
    public static final String FORMATTED_PHONENUMBER_MASK = "(\\(020\\) \\d{4}-\\d{3})|(\\(030\\) \\d{3}-\\d{4})|(\\(\\d{3}\\) \\d{3}-\\d{3})";

    /**
     * Plain telephone mask: xxxxxxxxx = 9 020xxxxxxxx = 11  030xxxxxxx = 10
     */
    public static final String PLAIN_PHONENUMBER_MASK = "(((020)\\d{8})|((030)\\d{7})|\\d{9})";

    /**
     * Formats number according to mask (xxx) xxx-xxx = 9 (020) xxxx-xxxx = 11  (030) xxx-xxxx = 10
     * @param phoneValue
     * @return
     */
    public static String formatPhoneNumber(String phoneValue) {
        //Empty -> nothing to format
        if (phoneValue == null) {
            phoneValue = "";
        }

        //Already formatted -> done
        if(phoneValue.isEmpty() || phoneValue.matches(FORMATTED_PHONENUMBER_MASK)){
            return phoneValue;
        }

        String formattedNumber;

        if(phoneValue.length()==11)
            //NNNNNNNN -> (NNN) NNNN-NNNN
            formattedNumber="("+phoneValue.substring(0,3)+") "+phoneValue.substring(3,7)+"-"+phoneValue.substring(6,phoneValue.length());
        else
            //NNNNNNNN -> (NNN) NNN-NNN || (NNN) NNN-NNNN
            formattedNumber="("+phoneValue.substring(0,3)+") "+phoneValue.substring(3,6)+"-"+phoneValue.substring(6,phoneValue.length());

        return  formattedNumber;
    }

    /**
     * Checks if the given string corresponds a correct phone number according to mask:
     *  (xxx) xxx-xxx = 9 (020) xxxx-xxxx = 11  (030) xxx-xxxx = 10
     * @param phoneValue
     * @return true|false
     */
    public static boolean checkPhoneNumberByMask(String phoneValue){

        //Empty  is ok
        if (phoneValue == null) {
            phoneValue = "";
        }
        return phoneValue.isEmpty() || phoneValue.matches(FORMATTED_PHONENUMBER_MASK) || phoneValue.replace(" ", "").matches(PLAIN_PHONENUMBER_MASK);
    }
}
