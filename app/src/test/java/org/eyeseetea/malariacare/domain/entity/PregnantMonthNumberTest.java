package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.eyeseetea.malariacare.domain.exception.InvalidPregnantMonthNumberException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PregnantMonthNumberTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throw_invalid_pregnant_number_exception_if_positiveNumber_is_empty_string()
            throws InvalidPregnantMonthNumberException {
        thrown.expect(InvalidPregnantMonthNumberException.class);

        PregnantMonthNumber pregnantMonthNumber = PregnantMonthNumber.parse("");
    }

    @Test
    public void throw_invalid_pregnant_number_exception_if_positiveNumber_is_text()
            throws InvalidPregnantMonthNumberException {
        thrown.expect(InvalidPregnantMonthNumberException.class);

        PregnantMonthNumber pregnantMonthNumber = PregnantMonthNumber.parse("text");
    }

    @Test
    public void return_pregnant_number_if_create_with_number_input_between_1_and_99()
            throws InvalidPregnantMonthNumberException {

        PregnantMonthNumber pregnantMonthNumber = new PregnantMonthNumber(9);

        assertThat(pregnantMonthNumber, notNullValue());
    }

    @Test
    public void return_pregnant_number_if_number_is_over_max()
            throws InvalidPregnantMonthNumberException {
        thrown.expect(InvalidPregnantMonthNumberException.class);
        PregnantMonthNumber pregnantMonthNumber = new PregnantMonthNumber(10);
    }


    @Test
    public void return_pregnant_number_if_text_to_parse_is_number_is_over_max()
            throws InvalidPregnantMonthNumberException {
        thrown.expect(InvalidPregnantMonthNumberException.class);
        PregnantMonthNumber.parse("15");
    }
}
