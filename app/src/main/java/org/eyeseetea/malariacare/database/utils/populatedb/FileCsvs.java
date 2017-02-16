package org.eyeseetea.malariacare.database.utils.populatedb;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class FileCsvs {
    private Context mContext;
    private final static String TAG = ".FileCsvs";
    private static final List<String> csvsToCreate = Arrays.asList(
            PopulateDB.PROGRAMS_CSV,
            PopulateDB.TABS_CSV,
            PopulateDB.HEADERS_CSV,
            PopulateDB.ANSWERS_CSV,
            PopulateDB.OPTION_ATTRIBUTES_CSV,
            PopulateDB.OPTIONS_CSV,
            PopulateDB.QUESTIONS_CSV,
            PopulateDB.QUESTION_RELATIONS_CSV,
            PopulateDB.MATCHES,
            PopulateDB.QUESTION_OPTIONS_CSV,
            PopulateDB.QUESTION_THRESHOLDS_CSV,
            PopulateDB.DRUGS_CSV,
            PopulateDB.ORGANISATIONS_CSV,
            PopulateDB.TREATMENT_CSV,
            PopulateDB.DRUG_COMBINATIONS_CSV,
            PopulateDB.TREATMENT_MATCHES_CSV);

    public FileCsvs() {
        mContext = PreferencesState.getInstance().getContext();
    }

    public void saveCsvsInFileIfNeeded() throws IOException {
        String filename = PopulateDB.TABS_CSV;
        if (!fileExists(filename)) {
            for (String csvName : csvsToCreate) {
                saveCsvFromAssetsToFile(csvName);
            }
        }
    }


    public void saveCsvFromAssetsToFile(String csvName) throws IOException {
        if (!fileExists(csvName)) {
            createFile(csvName);
        } else {
            mContext.deleteFile(csvName);
            createFile(csvName);
        }
        FileOutputStream outputStream = mContext.openFileOutput(csvName, Context.MODE_PRIVATE);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(mContext.getAssets().open(csvName)));
        String line = "";
        while ((line = reader.readLine()) != null) {
            outputStream.write((line + "\n").getBytes());
        }
        outputStream.close();
        reader.close();
    }


    private boolean fileExists(String fname) {
        File file = mContext.getFileStreamPath(fname);
        return file.exists();
    }

    private boolean createFile(String fileName) throws IOException {
        boolean created;
        File file = new File(mContext.getFilesDir(), fileName);
        try {
            created = file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Error creating new file " + fileName);
            e.printStackTrace();
            throw e;
        }
        return created;
    }

}
