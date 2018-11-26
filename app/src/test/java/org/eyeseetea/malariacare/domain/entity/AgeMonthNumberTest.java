package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eyeseetea.malariacare.domain.exception.InvalidAgeMonthNumberException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AgeMonthNumberTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throw_invalid_age_number_exception_if_positiveNumber_is_empty_string()
            throws InvalidAgeMonthNumberException {
        thrown.expect(InvalidAgeMonthNumberException.class);

        AgeMonthNumber ageMonthNumber = AgeMonthNumber.parse("");
    }

    @Test
    public void throw_invalid_age_number_exception_if_positiveNumber_is_text()
            throws InvalidAgeMonthNumberException {
        thrown.expect(InvalidAgeMonthNumberException.class);

        AgeMonthNumber ageMonthNumber = AgeMonthNumber.parse("text");
    }

    @Test
    public void return_age_number_if_create_with_number_input_between_0_and_12()
            throws InvalidAgeMonthNumberException {

        AgeMonthNumber ageMonthNumber = new AgeMonthNumber(AgeMonthNumber.MIN_AGE);

        assertThat(ageMonthNumber, notNullValue());

        ageMonthNumber = new AgeMonthNumber(AgeMonthNumber.MAX_AGE);

        assertThat(ageMonthNumber, notNullValue());
    }

    @Test
    public void return_age_number_if_create_with_text_number_input_between_0_and_12()
            throws InvalidAgeMonthNumberException {

        AgeMonthNumber ageMonthNumber = AgeMonthNumber.parse("" + AgeMonthNumber.MIN_AGE);

        assertThat(ageMonthNumber, notNullValue());

        ageMonthNumber = AgeMonthNumber.parse("" + AgeMonthNumber.MAX_AGE);

        assertThat(ageMonthNumber, notNullValue());
    }

    @Test
    public void throw_InvalidAgeMonthNumberException_if_number_is_over_max()
            throws InvalidAgeMonthNumberException {
        thrown.expect(InvalidAgeMonthNumberException.class);
        AgeMonthNumber ageMonthNumber = new AgeMonthNumber(AgeMonthNumber.MAX_AGE + 1);
    }


    @Test
    public void throw_InvalidAgeMonthNumberException_if_text_to_parse_is_number_is_over_max()
            throws InvalidAgeMonthNumberException {
        thrown.expect(InvalidAgeMonthNumberException.class);
        AgeMonthNumber.parse("" + (AgeMonthNumber.MAX_AGE + 2));
    }
}
