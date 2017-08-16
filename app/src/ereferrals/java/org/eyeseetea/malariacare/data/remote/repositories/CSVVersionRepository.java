package org.eyeseetea.malariacare.data.remote.repositories;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.sync.importer.CSVImporter;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICSVVersionRepository;


public class CSVVersionRepository implements ICSVVersionRepository {

    @Override
    public void getCSVVersion(final IDataSourceCallback<Integer> callback) {
        CSVImporter csvImporter = new CSVImporter();
        csvImporter.getCSVVersion(new CSVImporter.CSVImporterCallBack<String>() {
            @Override
            public void onSuccess(String csvString) {
                callback.onSuccess(Integer.parseInt(csvString.replace("\n", "")));
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    @Override
    public void saveCSVVersion(int version, IDataSourceCallback<Integer> callback) {

    }

}
