package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.exception.InvalidPregnantMonthNumberException;

public class PregnantMonthNumber {

    private int value;

    public PregnantMonthNumber(int value) throws InvalidPregnantMonthNumberException {
        if (!isValid(value)) {
            throw new InvalidPregnantMonthNumberException("Invalid positive number");
        }

        this.value = value;
    }

    public static PregnantMonthNumber parse(String positiveNumber)
            throws InvalidPregnantMonthNumberException {
        int numericValue;

        try {
            numericValue = Integer.parseInt(positiveNumber);
        } catch (NumberFormatException e) {
            throw new InvalidPregnantMonthNumberException("Invalid positive Number");
        }

        return new PregnantMonthNumber(numericValue);
    }

    private boolean isValid(int value) {
        return (value >= 0 && value <= 9);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof PregnantMonthNumber)) return false;

        PregnantMonthNumber other = (PregnantMonthNumber) o;
        return (this.value == other.value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "Pregnant month number{" +
                "value=" + value + '}';
    }
}
