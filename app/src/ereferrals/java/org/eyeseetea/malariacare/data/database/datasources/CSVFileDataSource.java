package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
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
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICSVFileRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CSVFileDataSource implements ICSVFileRepository {
    private static final String TAG = "CSVFileDataSource";
    private Context mContext;

    public CSVFileDataSource(Context context) {
        mContext = context;
    }

    @Override
    public void getCSVFile(String filename, IDataSourceCallback<byte[]> callback) {

    }

    @Override
    public void saveCSVFile(String filename, byte[] fileToSave,
            IDataSourceCallback<Void> callback) {
        try {
            if (!fileExists(filename)) {
                createFile(filename);
            } else {
                mContext.deleteFile(filename);
                createFile(filename);
            }
            FileOutputStream outputStream = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(fileToSave);
            outputStream.flush();
            outputStream.close();
            callback.onSuccess(null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            callback.onError(e);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onError(e);
        }

    }

    @Override
    public void updateCSVDB(IDataSourceCallback<Void> callback) {
        deleteDB();
        try {
            PopulateDB.populateDB(mContext);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onError(e);
        }
        //Get maximum total of questions
        Session.setMaxTotalQuestions(ProgramDB.getMaxTotalQuestions());
        callback.onSuccess(null);
    }


    private boolean fileExists(String fname) {
        File file = mContext.getFileStreamPath(fname);
        return file.exists();
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
