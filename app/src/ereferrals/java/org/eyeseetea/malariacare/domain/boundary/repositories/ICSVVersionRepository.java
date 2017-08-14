package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;

public interface ICSVVersionRepository {
    void getCSVVersion(IDataSourceCallback<Integer> callback);

    void saveCSVVersion(int version, IDataSourceCallback<Integer> callback);
}
