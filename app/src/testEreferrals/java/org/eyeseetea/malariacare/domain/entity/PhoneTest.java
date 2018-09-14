package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PhoneTest {


    public static final String PHONE_10_DIGITS = "2200060073";
    public static final String PLAIN_PHONE_NUMBER_MASK =
            "((^(00)\\d{10,15})|(^(\\+)\\d{10,15}$)|(^(06|07|\\+2556|\\+2557)\\d{9}$))";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void
    throw_invalid_phone_exception_if_phone_format_starts_with_plus_and_with_more_than_fifteen()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("+000000" + PHONE_10_DIGITS,
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));
    }


    @Test
    public void valid_phone_value_if_phone_format_starts_with_00_with_min_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("00" + PHONE_10_DIGITS,
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("00" + PHONE_10_DIGITS));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_0000_with_max_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("0000" + PHONE_10_DIGITS + "1234567",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_00_with_max_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("00" + PHONE_10_DIGITS + "12345",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("00" + PHONE_10_DIGITS + "12345"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_plus_symbol_with_min_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("+" + PHONE_10_DIGITS.replace("3", ""),
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_plus_symbol_with_min_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("+" + PHONE_10_DIGITS + "123",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+" + PHONE_10_DIGITS + "123"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_plus_symbol_with_max_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("+" + PHONE_10_DIGITS + "123456789",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_plus_symbol_with_max_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("+" + PHONE_10_DIGITS + "12345",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+" + PHONE_10_DIGITS + "12345"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_06_with_min_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("06" + PHONE_10_DIGITS + "1",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_006_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("006" + PHONE_10_DIGITS + "12",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("006" + PHONE_10_DIGITS + "12"));
    }


    @Test
    public void
    throw_invalid_phone_exception_if_phone_format_starts_with_06_with_max_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("06" + PHONE_10_DIGITS + "123",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_006_with_max_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("006" + PHONE_10_DIGITS + "12",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("006" + PHONE_10_DIGITS + "12"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_07_with_min_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("07" + PHONE_10_DIGITS + "1",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_07_with_max_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("07" + PHONE_10_DIGITS + "123",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_007_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("007" + PHONE_10_DIGITS + "12",
                new PhoneFormat(PLAIN_PHONE_NUMBER_MASK, "", ""));

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("007" + PHONE_10_DIGITS + "12"));
    }
}
