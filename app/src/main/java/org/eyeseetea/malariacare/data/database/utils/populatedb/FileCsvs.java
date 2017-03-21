package org.eyeseetea.malariacare.data.database.utils.populatedb;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class FileCsvs {
    private Context mContext;
    private final static String TAG = ".FileCsvs";
    private static final List<String> csvsToCreate = Arrays.asList(
            PopulateDB.STRING_KEY_CSV,
            PopulateDB.TRANSLATION_CSV,
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
            PopulateDB.PARTNER_CSV,
            PopulateDB.TREATMENT_CSV,
            PopulateDB.DRUG_COMBINATIONS_CSV,
            PopulateDB.TREATMENT_MATCHES_CSV,
            PopulateDB.ORG_UNIT_CSV,
            PopulateDB.ORG_UNIT_LEVEL_CSV

    );

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
        if (assetFileExits(csvName)) {
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
    }

    public void copyCsvFile(String csvFrom, String csvTo) throws IOException {
        if (!fileExists(csvTo)) {
            createFile(csvTo);
        } else {
            mContext.deleteFile(csvTo);
            createFile(csvTo);
        }
        if (fileExists(csvFrom)) {
            FileOutputStream outputStream = mContext.openFileOutput(csvTo, Context.MODE_PRIVATE);
            BufferedReader reader = new BufferedReader(
                    new FileReader(mContext.getFilesDir() + "/" + csvFrom));
            String line = "";
            while ((line = reader.readLine()) != null) {
                outputStream.write((line + "\n").getBytes());
            }
            outputStream.close();
            reader.close();
        }
    }

    private boolean fileExists(String fname) {
        File file = mContext.getFileStreamPath(fname);
        return file.exists();
    }

    private boolean assetFileExits(String csvName) throws IOException {
        return Arrays.asList(mContext.getAssets().list("")).contains(csvName);
    }

    private File createFile(String fileName) throws IOException {
        boolean created;
        File file = new File(mContext.getFilesDir(), fileName);
        try {
            created = file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Error creating new file " + fileName);
            e.printStackTrace();
            throw e;
        }
        return created ? file : null;
    }

    public void insertCsvLine(String csvFileName, String[] line) throws IOException {
        if (!fileExists(csvFileName) || line == null) {
            return;
        } else {

            BufferedReader input = new BufferedReader(
                    new FileReader(mContext.getFilesDir() + "/" + csvFileName));
            StringBuilder contents = new StringBuilder();
            String lineR = null;
            while ((lineR = input.readLine()) != null) {
                contents.append(lineR + "\n");
            }

            File file = new File(mContext.getFilesDir() + "/" + csvFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String textLine = "";
            boolean first = true;
            for (String row : line) {
                if (!first) {
                    textLine += ";" + row;
                } else {
                    first = false;
                    textLine += row;
                }
            }
            writer.write(contents.toString());
            writer.write(textLine);
            writer.close();
        }
    }


    public void deleteCsvLineWithId(String csvFilename, Long id) throws IOException {
        if (!fileExists(csvFilename)) {
            return;
        } else {
            File file = new File(mContext.getFilesDir() + "/" + csvFilename);
            File tempFile = createFile("Temp" + csvFilename);

            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splitedLine = line.split(";");
                if (Long.parseLong(splitedLine[0]) != id) {
                    writer.write(line + "\n");
                }
            }
            writer.close();
            reader.close();
            tempFile.renameTo(file);
            mContext.deleteFile("Temp" + csvFilename);
        }
    }

}
