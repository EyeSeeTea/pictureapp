package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.eyeseetea.malariacare.domain.exception.InvalidPositiveNumberException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PositiveNumberTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throw_invalid_positiveNumber_exception_if_positiveNumber_is_empty_string() throws
            InvalidPositiveNumberException {
        thrown.expect(InvalidPositiveNumberException.class);

        PositiveNumber positiveNumber = PositiveNumber.parse("");
    }

    @Test
    public void throw_invalid_positiveNumber_exception_if_positiveNumber_is_text() throws
            InvalidPositiveNumberException {
        thrown.expect(InvalidPositiveNumberException.class);

        PositiveNumber positiveNumber = PositiveNumber.parse("any text");
    }

    @Test
    public void
    throw_invalid_positiveNumber_exception_if_create_with_number_input_greather_than_99()
            throws InvalidPhoneException, InvalidPositiveNumberException {
        thrown.expect(InvalidPositiveNumberException.class);

        PositiveNumber positiveNumber = new PositiveNumber(100);
    }

    @Test
    public void throw_invalid_positiveNumber_exception_if_create_with_number_input_smaller_than_1()
            throws InvalidPhoneException, InvalidPositiveNumberException {
        thrown.expect(InvalidPositiveNumberException.class);

        PositiveNumber positiveNumber = new PositiveNumber(0);
    }

    @Test
    public void return_positiveNumber_if_create_with_number_input_between_1_and_99()
            throws InvalidPhoneException, InvalidPositiveNumberException {

        PositiveNumber positiveNumber = new PositiveNumber(1);

        assertThat(positiveNumber, notNullValue());
    }

    @Test
    public void return_positiveNumber_if_parse_with_string_input_between_1_and_99()
            throws InvalidPhoneException, InvalidPositiveNumberException {


        PositiveNumber positiveNumber = PositiveNumber.parse("99");

        assertThat(positiveNumber, notNullValue());
    }

}
