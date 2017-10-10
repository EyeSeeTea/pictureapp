package org.eyeseetea.malariacare.data.sync.importer;

public interface IVisitableFromApi {
    void accept(IConvertFromApiVisitor iConvertFromApiVisitor);
}
