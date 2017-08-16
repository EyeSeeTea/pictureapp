package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;

public interface ICSVFileRepository {

    void getCSVFile(String filename, IDataSourceCallback<byte[]> callback);

    void saveCSVFile(String filename, byte[] fileToSave, IDataSourceCallback<Void> callback);
}
