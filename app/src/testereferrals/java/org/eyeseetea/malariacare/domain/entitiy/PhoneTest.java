package org.eyeseetea.malariacare.domain.entitiy;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PhoneTest {


    public static final String PHONE_7_DIGITS = "0006007";
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_0000_with_min_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("0000" + PHONE_7_DIGITS);
    }


    @Test
    public void valid_phone_value_if_phone_format_starts_with_0000_with_min_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("0001" + PHONE_7_DIGITS + "1");

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+01" + PHONE_7_DIGITS + "1"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_0000_with_max_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("0000" + PHONE_7_DIGITS + "1234567");
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_0000_with_max_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("0000" + PHONE_7_DIGITS + "123456");

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+00" + PHONE_7_DIGITS + "123456"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_plus_symbol_with_min_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("+" + PHONE_7_DIGITS + "12");
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_plus_symbol_with_min_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("+" + PHONE_7_DIGITS + "123");

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+" + PHONE_7_DIGITS + "123"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_plus_symbol_with_max_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("+" + PHONE_7_DIGITS + "123456789");
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_plus_symbol_with_max_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("+" + PHONE_7_DIGITS + "12345678");

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+" + PHONE_7_DIGITS + "12345678"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_06_with_min_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("06" + PHONE_7_DIGITS + "1");
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_06_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("06" + PHONE_7_DIGITS + "12");

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+2556" + PHONE_7_DIGITS + "12"));
    }


    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_06_with_max_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("06" + PHONE_7_DIGITS + "123");
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_06_with_max_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("06" + PHONE_7_DIGITS + "12");

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+2556" + PHONE_7_DIGITS + "12"));
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_07_with_min_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("07" + PHONE_7_DIGITS + "1");
    }

    @Test
    public void throw_invalid_phone_exception_if_phone_format_starts_with_07_with_max_not_valid_value()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("07" + PHONE_7_DIGITS + "123");
    }

    @Test
    public void valid_phone_value_if_phone_format_starts_with_07_valid_value()
            throws InvalidPhoneException {
        Phone phone = new Phone("07" + PHONE_7_DIGITS + "12");

        assertThat(phone, notNullValue());
        assertTrue(phone.getValue().equals("+2557" + PHONE_7_DIGITS + "12"));
    }
}
