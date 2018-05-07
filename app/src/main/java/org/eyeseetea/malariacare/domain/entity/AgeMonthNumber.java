package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.exception.InvalidAgeMonthNumberException;

public class AgeMonthNumber {
    public static final int MIN_AGE=0;
    public static final int MAX_AGE=11;
    private int value;

    public AgeMonthNumber(int value) throws InvalidAgeMonthNumberException {
        if (!isValid(value)) {
            throw new InvalidAgeMonthNumberException("Invalid positive number");
        }

        this.value = value;
    }

    public static AgeMonthNumber parse(String positiveNumber)
            throws InvalidAgeMonthNumberException {
        int numericValue;

        try {
            numericValue = Integer.parseInt(positiveNumber);
        } catch (NumberFormatException e) {
            throw new InvalidAgeMonthNumberException("Invalid positive Number");
        }

        return new AgeMonthNumber(numericValue);
    }

    private boolean isValid(int value) {
        return (value >= MIN_AGE && value <= MAX_AGE);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof AgeMonthNumber)) return false;

        AgeMonthNumber other = (AgeMonthNumber) o;
        return (this.value == other.value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "Age month number{" +
                "value=" + value + '}';
    }
}
