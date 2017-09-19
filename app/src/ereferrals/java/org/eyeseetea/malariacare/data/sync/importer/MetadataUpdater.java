package org.eyeseetea.malariacare.data.sync.importer;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.QuestionThresholdDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.database.utils.populatedb.FileCsvsStrategy;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.sdk.common.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class MetadataUpdater {
    private static final String TAG = "MetadataUpdater";
    private CSVImporter csvImporter;
    private Context mContext;
    private int githubVersion;

    public MetadataUpdater(Context context) {
        csvImporter = new CSVImporter();
        mContext = context;
    }


    public boolean hasToUpdateMetadata() throws IOException {
        githubVersion = getGithubCSVVersion();
        if (getPhoneCSVersion() < githubVersion) {
            return true;
        }
        return false;
    }

    public void updateMetadata() throws IOException {
        List<String> csvsToImport = FileCsvsStrategy.getCsvsToCreate();
        for (String csvName : csvsToImport) {
            byte[] csvImported = csvImporter.importCSV(csvName);
            FileUtils.saveFile(csvName, csvImported, mContext);
        }
        updateCSVDB();
    }

    private void updateCSVDB() throws IOException {
        deleteDB();
        try {
            PopulateDB.populateDB(mContext);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        //Get maximum total of questions
        Session.setMaxTotalQuestions(ProgramDB.getMaxTotalQuestions());
        saveNewVersion();
    }

    private void saveNewVersion() throws IOException {
        FileUtils.saveFile(PopulateDB.VERSIONS_CSV, String.valueOf(githubVersion).getBytes(),
                mContext);
    }


    public static int getPhoneCSVersion() throws IOException {
        String version = null;
        CSVReader reader = null;
        Context context = PreferencesState.getInstance().getContext();
        try {
            if (FileUtils.fileExists(PopulateDB.VERSIONS_CSV, context)) {
                reader = new CSVReader(
                        new InputStreamReader(
                                new PopulateDBStrategy().openFile(context,
                                        PopulateDB.VERSIONS_CSV)));
            } else {
                AssetManager assetMgr = context.getAssets();
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
        String version = null;
        try {
            version = csvImporter.getCSVVersion().replace("\n", "");
        } catch (Exception e) {
            Log.e(TAG, "Can't download csv version: " + e.getMessage());
            e.printStackTrace();
        }
        return version == null || version.isEmpty() ? 0 : Integer.parseInt(version);
    }

    private void deleteDB() {
        SurveyDB.deleteAll();
        ProgramDB.deleteAll();
        TabDB.deleteAll();
        HeaderDB.deleteAll();
        AnswerDB.deleteAll();
        OptionAttributeDB.deleteAll();
        OptionDB.deleteAll();
        QuestionDB.deleteAll();
        QuestionRelationDB.deleteAll();
        MatchDB.deleteAll();
        QuestionOptionDB.deleteAll();
        QuestionThresholdDB.deleteAll();
    }

}
