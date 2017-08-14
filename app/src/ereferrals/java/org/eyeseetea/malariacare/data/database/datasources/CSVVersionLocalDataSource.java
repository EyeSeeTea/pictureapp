package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;
import android.util.Log;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICSVVersionRepository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class CSVVersionLocalDataSource implements ICSVVersionRepository {
    private static final String TAG = "CSVVersionLDS";
    private Context mContext;

    public CSVVersionLocalDataSource(Context context) {
        mContext = context;
    }

    @Override
    public void getCSVVersion(IDataSourceCallback<Integer> callback) {
        String version = null;
        CSVReader reader = null;
        try {
            reader = new CSVReader(
                    new InputStreamReader(
                            new PopulateDBStrategy().openFile(mContext, PopulateDB.VERSIONS_CSV)));
            String[] line = reader.readNext();
            version = line[0];
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "File not found:" + e.getMessage());
            callback.onError(e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException:" + e.getMessage());
            callback.onError(e);
        }
        if (version != null) {
            callback.onSuccess(Integer.parseInt(version));
        } else {
            callback.onError(new Exception(TAG + " version saved in files is null"));
        }
    }

    @Override
    public void saveCSVVersion(int version, IDataSourceCallback<Integer> callback) {
        try {
            FileOutputStream outputStream = mContext.openFileOutput(PopulateDB.VERSIONS_CSV,
                    Context.MODE_PRIVATE);
            outputStream.write(String.valueOf(version).getBytes());
            outputStream.close();
            callback.onSuccess(version);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "File not found:" + e.getMessage());
            callback.onError(e);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onError(e);
        }
    }
}
