package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.domain.exception.RegExpValidationException;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class RegExpValidator {
    String regExp;
    String message;

    public RegExpValidator(String regExp, String message){
        this.regExp=required(regExp, "reg exp is required");
        this.message = message;
    }

    public void match(String value) throws RegExpValidationException {
        if (!value.matches(regExp)){
            throw new RegExpValidationException(value);
        }
    }

    public String getError(){
        return message;
    }
}
