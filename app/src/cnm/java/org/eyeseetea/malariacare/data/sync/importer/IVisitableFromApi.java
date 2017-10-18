package org.eyeseetea.malariacare.data.sync.importer;

import java.util.List;

public interface IVisitableFromApi <T>{
    void accept(IConvertFromApiVisitor iConvertFromApiVisitor);

    void accept(IConvertFromApiVisitor iConvertFromApiVisitor, List<T> objects);
}
