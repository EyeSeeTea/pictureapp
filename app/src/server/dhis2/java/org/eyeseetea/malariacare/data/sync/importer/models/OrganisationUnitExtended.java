package org.eyeseetea.malariacare.data.sync.importer.models;

import org.eyeseetea.malariacare.data.sync.importer.IConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.VisitableFromSDK;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow;

import java.util.ArrayList;
import java.util.List;

public class OrganisationUnitExtended implements VisitableFromSDK {
    OrganisationUnitFlow orgUnit;

    public OrganisationUnitExtended() {
    }

    public OrganisationUnitExtended(OrganisationUnitFlow orgUnit) {
        this.orgUnit = orgUnit;
    }

    @Override
    public void accept(IConvertFromSDKVisitor visitor) {
        visitor.visit(this);
    }

    public OrganisationUnitFlow getOrgUnit() {
        return orgUnit;
    }

    public String getId() {
        return orgUnit.getUId();
    }

    public String getLabel() {
        return orgUnit.getDisplayName();
    }

    public int getLevel() {
        return orgUnit.getLevel();
    }

    public String getParent() {
        return orgUnit.getParent().getUId();
    }


    public static List<OrganisationUnitExtended> getExtendedList(
            List<OrganisationUnitFlow> flowList) {
        List<OrganisationUnitExtended> extendedsList = new ArrayList<>();
        for (OrganisationUnitFlow flowPojo : flowList) {
            extendedsList.add(new OrganisationUnitExtended(flowPojo));
        }
        return extendedsList;
    }
}
