package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;

import utils.PhoneMask;

public class Phone {

    private String value;
    private PhoneFormat mPhoneFormat;

    public Phone(String value) throws InvalidPhoneException {
        if (!isValid(value)) {
            throw new InvalidPhoneException();
        }

        this.value = applyTransformations(value);
    }

    public Phone(String value, PhoneFormat phoneFormat) throws InvalidPhoneException {
        mPhoneFormat = required(phoneFormat, "program is required");
        if (!isValid(value)) {
            throw new InvalidPhoneException();
        }

        this.value = applyTransformations(value);
    }

    private String applyTransformations(String value){
        return PhoneMask.applyValueTransformations(value, mPhoneFormat);
    }

    private boolean isValid(String value) {
        return PhoneMask.checkPhoneNumberByMask(value, mPhoneFormat);
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
