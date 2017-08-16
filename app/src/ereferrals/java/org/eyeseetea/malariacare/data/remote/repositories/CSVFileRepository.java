package org.eyeseetea.malariacare.data.remote.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.sync.importer.CSVImporter;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICSVFileRepository;


public class CSVFileRepository implements ICSVFileRepository {

    @Override
    public void getCSVFile(String filename, final IDataSourceCallback<byte[]> callback) {
        CSVImporter csvImporter = new CSVImporter();
        csvImporter.importCSV(filename, new CSVImporter.CSVImporterCallBack<byte[]>() {
            @Override
            public void onSuccess(byte[] csvBytes) {
                callback.onSuccess(csvBytes);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void saveCSVFile(String filename, byte[] fileToSave,
            IDataSourceCallback<Void> callback) {

    }
}
