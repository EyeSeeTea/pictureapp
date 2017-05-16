package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.exception.InvalidPregnantMonthNumberException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PregnantMonthNumberTest {


    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void ten_month_invalid_pregnant_month_number()
            throws InvalidPregnantMonthNumberException {
        thrown.expect(InvalidPregnantMonthNumberException.class);
        PregnantMonthNumber pregnantMonthNumber = new PregnantMonthNumber(10);
    }

    @Test
    public void parse_string_not_valid_number() throws InvalidPregnantMonthNumberException {
        thrown.expect(InvalidPregnantMonthNumberException.class);
        PregnantMonthNumber.parse("15");
    }
}
