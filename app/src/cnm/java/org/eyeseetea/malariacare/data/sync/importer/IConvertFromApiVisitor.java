package org.eyeseetea.malariacare.data.sync.importer;

import org.eyeseetea.malariacare.data.sync.importer.models.OrgUnitTree;

public interface IConvertFromApiVisitor {
    void visit(OrgUnitTree orgUnitTree);
}
