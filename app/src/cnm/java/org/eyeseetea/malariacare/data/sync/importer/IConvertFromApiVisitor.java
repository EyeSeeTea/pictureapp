package org.eyeseetea.malariacare.data.sync.importer;

import org.eyeseetea.malariacare.data.sync.importer.models.OrgUnitTree;

import java.util.List;

public interface IConvertFromApiVisitor {
    void visit(List<OrgUnitTree> orgUnitTree);
}
