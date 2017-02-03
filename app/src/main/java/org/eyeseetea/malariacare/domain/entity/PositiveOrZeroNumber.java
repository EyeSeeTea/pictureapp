package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.exception.InvalidPositiveOrZeroNumberException;

public class PositiveOrZeroNumber {

    private int value;

    public PositiveOrZeroNumber(int value) throws InvalidPositiveOrZeroNumberException {
        if (!isValid(value)) {
            throw new InvalidPositiveOrZeroNumberException("Invalid positive number");
        }

        this.value = value;
    }

    public static PositiveOrZeroNumber parse(String positiveNumber)
            throws InvalidPositiveOrZeroNumberException {
        int numericValue;

        try {
            numericValue = Integer.parseInt(positiveNumber);
        } catch (NumberFormatException e) {
            throw new InvalidPositiveOrZeroNumberException("Invalid positive Number");
        }

        return new PositiveOrZeroNumber(numericValue);
    }

    private boolean isValid(int value) {
        return (value >= 0 && value <= 99);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof PositiveOrZeroNumber)) return false;

        PositiveOrZeroNumber other = (PositiveOrZeroNumber) o;
        return (this.value == other.value);
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return "PositiveNumber{" +
                "value=" + value + '}';
    }
}
