package org.eyeseetea.malariacare.domain.entity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import utils.PhoneMask;

public class PhoneTest {
    private PhoneFormat phoneFormat = new PhoneFormat("^(0?7)\\d{8}|^(\\+)\\d*|^(00)\\d*", "0", "+263");

    public static final String PHONE_7_DIGITS = "0006007";
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void do_not_modify_phone_value_if_starts_with_00_value()
            throws InvalidPhoneException {

        String phoneValue = "00" + PHONE_7_DIGITS;
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

    @Test
    public void do_not_modify_phone_value_if_starts_with_plus_symbol_value()
            throws InvalidPhoneException {

        String phoneValue = "+" + PHONE_7_DIGITS;
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

    @Test
    public void add_prefix_when_phone_value_starts_with_7_and_height_digits()
            throws InvalidPhoneException {

        String phoneValue = "70" + PHONE_7_DIGITS;
        Phone phone = new Phone(phoneValue, phoneFormat);
        phoneValue = phoneFormat.getPrefixtToPut()+phoneValue;

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

    @Test
    public void add_prefix_when_phone_value_starts_with_07_and_height_digits()
            throws InvalidPhoneException {

        String phoneValue = "070" + PHONE_7_DIGITS;
        Phone phone = new Phone(phoneValue, phoneFormat);
        phoneValue = phoneFormat.getPrefixtToPut()+"70" + PHONE_7_DIGITS;

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_7_and_has_more_than_expected_length()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);
        String phoneValue = "701" + PHONE_7_DIGITS;
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertFalse(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));
    }

    @Test
    public void throw_invalid_phone_exception_phone_if_format_starts_with_7_and_has_less_than_expected_length()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);
        String phoneValue = "7" + PHONE_7_DIGITS;
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertFalse(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));
    }

    @Test
    public void return_true_phone_if_format_starts_with_plus_symbol()
            throws InvalidPhoneException {
        String phoneValue = "+";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }


    @Test
    public void return_true_phone_if_format_starts_with_plus_symbol_and_has_more_than_12_length()
            throws InvalidPhoneException {
        String phoneValue = "+1"+"0123456789112";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

    @Test
    public void return_true_phone_if_format_starts_with_00()
            throws InvalidPhoneException {
        String phoneValue = "00";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }


    @Test
    public void return_true_phone_if_format_starts_with_00_and_has_more_than_12_length()
            throws InvalidPhoneException {
        String phoneValue = "001"+"0123456789112";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

    @Test
    public void do_not_add_prefix_phone_if_format_starts_with_plus_symbol_and_has_0_length()
            throws InvalidPhoneException {
        String phoneValue = "+";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

    @Test
    public void do_not_add_prefix_phone_if_format_starts_with_plus_symbol_and_has_at_least_1_length()
            throws InvalidPhoneException {
        String phoneValue = "+1";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }


    @Test
    public void do_not_add_prefix_phone_if_format_starts_with_plus_symbol_and_has_more_than_12_length()
            throws InvalidPhoneException {
        String phoneValue = "+1"+"0123456789112";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }
    @Test
    public void do_not_add_prefix_phone_if_format_starts_with_00_and_has_0_length()
            throws InvalidPhoneException {
        String phoneValue = "00";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

    @Test
    public void do_not_add_prefix_phone_if_format_starts_with_00_and_has_at_least_1_length()
            throws InvalidPhoneException {
        String phoneValue = "001";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }


    @Test
    public void do_not_add_prefix_phone_if_format_starts_with_00_and_has_more_than_12_length()
            throws InvalidPhoneException {
        String phoneValue = "001"+"0123456789112";
        Phone phone = new Phone(phoneValue, phoneFormat);

        assertTrue(PhoneMask.checkPhoneNumberByMask(phone.getValue(), phoneFormat));

        assertTrue(phoneValue.equals(phone.getValue()));
    }

}
