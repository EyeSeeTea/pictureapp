package org.eyeseetea.malariacare.domain.exception.organisationunit;

import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.WarningException;

public class ExistsMoreThanOneOrgUnitByPhoneException extends WarningException {

    public static final String ERROR_MESSAGE =
            "Exists more than one organisation unit by phone: ";
    private OrganisationUnit selectedOrganisationUnit;

    private String phone;

    public ExistsMoreThanOneOrgUnitByPhoneException(String phone,
            OrganisationUnit organisationUnit) {
        super(ERROR_MESSAGE + phone);
        this.phone = phone;
        this.selectedOrganisationUnit = organisationUnit;

    }

    public String getPhone() {
        return phone;
    }

    public OrganisationUnit getSelectedOrganisationUnit() {
        return selectedOrganisationUnit;
    }
}
