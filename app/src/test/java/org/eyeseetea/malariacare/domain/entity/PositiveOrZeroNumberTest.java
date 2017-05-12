package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

import org.eyeseetea.malariacare.domain.exception.InvalidPositiveOrZeroNumberException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PositiveOrZeroNumberTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throw_invalid_positiveNumber_exception_if_positiveNumber_is_empty_string() throws
            InvalidPositiveOrZeroNumberException {
        thrown.expect(InvalidPositiveOrZeroNumberException.class);

        PositiveOrZeroNumber positiveOrZeroNumber = PositiveOrZeroNumber.parse("");
    }

    @Test
    public void throw_invalid_positiveNumber_exception_if_positiveNumber_is_text() throws
            InvalidPositiveOrZeroNumberException {
        thrown.expect(InvalidPositiveOrZeroNumberException.class);

        PositiveOrZeroNumber positiveOrZeroNumber = PositiveOrZeroNumber.parse("any text");
    }

    @Test
    public void
    throw_invalid_positiveNumber_exception_if_create_with_number_input_greather_than_99()
            throws InvalidPositiveOrZeroNumberException {
        thrown.expect(InvalidPositiveOrZeroNumberException.class);

        PositiveOrZeroNumber positiveOrZeroNumber = new PositiveOrZeroNumber(100);
    }

    @Test
    public void throw_invalid_positiveNumber_exception_if_create_with_number_input_smaller_than_1()
            throws InvalidPositiveOrZeroNumberException {
        thrown.expect(InvalidPositiveOrZeroNumberException.class);

        PositiveOrZeroNumber positiveOrZeroNumber = new PositiveOrZeroNumber(-1);
    }

    @Test
    public void return_positiveNumber_if_create_with_number_input_between_0_and_99()
            throws InvalidPositiveOrZeroNumberException {

        PositiveOrZeroNumber positiveOrZeroNumber = new PositiveOrZeroNumber(0);

        assertThat(positiveOrZeroNumber, notNullValue());
    }

    @Test
    public void return_positiveNumber_if_parse_with_string_input_between_0_and_99()
            throws InvalidPositiveOrZeroNumberException {


        PositiveOrZeroNumber positiveOrZeroNumber = PositiveOrZeroNumber.parse("99");

        assertThat(positiveOrZeroNumber, notNullValue());
    }

}
