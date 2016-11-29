package org.eyeseetea.malariacare.domain.entitiy;


import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PhoneTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throw_invalid_phone_exception_if_phone_format_not_is_valid()
            throws InvalidPhoneException {
        thrown.expect(InvalidPhoneException.class);

        Phone phone = new Phone("678 848 8");
    }

    @Test
    public void return_phone_if_phone_have_valid_formatted_format() throws InvalidPhoneException {
        Phone phone = new Phone("(455) 345-678");

        assertThat(phone, notNullValue());
    }

    @Test
    public void return_phone_if_phone_have_valid_plain_format() throws InvalidPhoneException {
        Phone phone = new Phone("02056736956");


        assertThat(phone, notNullValue());
    }

}
