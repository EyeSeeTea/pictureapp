package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.exception.InvalidPositiveNumberException;

public class PositiveNumber {

    private int value;

    public PositiveNumber(int value) throws InvalidPositiveNumberException {
        if (!isValid(value)) {
            throw new InvalidPositiveNumberException("Invalid positive number");
        }

        this.value = value;
    }

    public static PositiveNumber parse(String positiveNumber)
            throws InvalidPositiveNumberException {
        int numericValue;

        try {
            numericValue = Integer.parseInt(positiveNumber);
        } catch (NumberFormatException e) {
            throw new InvalidPositiveNumberException("Invalid positive Number");
        }

        return new PositiveNumber(numericValue);
    }

    private boolean isValid(int value) {
        return (value > 0 && value <= 99);
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof PositiveNumber)) return false;

        PositiveNumber other = (PositiveNumber) o;
        if (this.value != other.value) return false;

        return true;
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
