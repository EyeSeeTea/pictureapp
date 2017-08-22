package org.eyeseetea.malariacare.data.sync.importer;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.sdk.common.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class MetadataUpdater {
    private static final String TAG = "MetadataUpdater";
    private CSVImporter csvImporter;
    private Context mContext;

    public MetadataUpdater(Context context) {
        csvImporter = new CSVImporter();
        mContext = context;
    }


    public boolean hasToUpdateMetadata() throws IOException {
        int githubVersion = getGithubCSVVersion();
        if (getPhoneCSVersion() < githubVersion) {
            return true;
        }
        return false;
    }

    public void saveNewVersion(int githubVersion) throws IOException {
        FileUtils.saveFile(PopulateDB.VERSIONS_CSV, String.valueOf(githubVersion).getBytes(),
                mContext);
    }

    private int getPhoneCSVersion() throws IOException {
        String version = null;
        CSVReader reader = null;
        try {
            if (FileUtils.fileExists(PopulateDB.VERSIONS_CSV, mContext)) {
                reader = new CSVReader(
                        new InputStreamReader(
                                new PopulateDBStrategy().openFile(mContext,
                                        PopulateDB.VERSIONS_CSV)));
            } else {
                AssetManager assetMgr = mContext.getAssets();
                reader = new CSVReader(
                        new InputStreamReader(assetMgr.open(PopulateDB.VERSIONS_CSV)));
            }
            String[] line = reader.readNext();
            version = line[0];
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "File not found:" + e.getMessage());
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "IOException:" + e.getMessage());
            throw e;
        }
        if (version != null) {
            return Integer.parseInt(version);
        }
        return 1;
    }

    private int getGithubCSVVersion() throws IOException {
        CSVImporter csvImporter = new CSVImporter();
        return Integer.parseInt(csvImporter.getCSVVersion().replace("\n", ""));
    }


}
