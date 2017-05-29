package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;

import utils.PhoneMask;

public class Phone {

    private String value;

    public Phone(String value) throws InvalidPhoneException {
        if (!isValid(value)) {
            throw new InvalidPhoneException();
        }

        this.value = applyTransformations(value);
    }

    private String applyTransformations(String value){
        return PhoneMask.applyValueTransformations(value);
    }

    private boolean isValid(String value) {
        return PhoneMask.checkPhoneNumberByMask(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Phone)) return false;

        Phone other = (Phone) o;
        if (!this.value.equals(other.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "Phone{" +
                "value=" + value + '}';
    }
}
