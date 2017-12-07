package org.eyeseetea.malariacare;


import static junit.framework.Assert.assertEquals;

import static org.eyeseetea.malariacare.common.android.test.BaseMockWebServerAndroidTest.readFileContentFromAssets;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.configurationImporter.BaseMetadataConfigurationImporterTest;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.di.Injector;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.MetadataConfigurationDBImporter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;

public class MetadataConfigurationDBImporterShould extends BaseMetadataConfigurationImporterTest {

    protected Context context;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        context = InstrumentationRegistry.getContext();
    }

    @Test
    public void insert_questions_to_db_after_download_the_configurations() throws Exception {

        enqueueResponse(MZ_CONFIG_ANDROID_1_0_JSON);

        cleanQuestionsTable();


        shouldNotBeAnyQuestionInTheDB();


        MetadataConfigurationDBImporter importer = new MetadataConfigurationDBImporter(
                apiClient, Injector.provideQuestionConverter()
        );

        importer.importMetadata();

        shouldBeInDB(16, 28, 28);
    }

    private void shouldNotBeAnyQuestionInTheDB() {
        shouldBeInDB(0, 0, 0);

    }

    private void shouldBeInDB(int expectedQuestionsCount, int expectedQuestionsOptionsCount,
            int expectedOptionsCount) {

        int questionsCount = getQuestionDBCount();
        int questionsOptionsCount = getQuestionOptionDBCount();
        int optionsCount = getOptionsDBCount();

        assertEquals(questionsCount, expectedQuestionsCount);
        assertEquals(questionsOptionsCount, expectedQuestionsOptionsCount);
        assertEquals(optionsCount, expectedOptionsCount);
    }

    private int getOptionsDBCount() {
        List<OptionDB> optionDBS = OptionDB.getAllOptions();

        return optionDBS.size();
    }

    private int getQuestionOptionDBCount() {
        List<QuestionOptionDB> questionOptionDBS = new Select().from(
                QuestionOptionDB.class).queryList();

        return questionOptionDBS.size();
    }

    private int getQuestionDBCount() {
        List<QuestionDB> questionDBS = QuestionDB.getAllQuestions();
        return questionDBS.size();
    }

    private void cleanQuestionsTable() {
        QuestionDB.deleteAll();
        OptionDB.deleteAll();
        QuestionOptionDB.deleteAll();
    }

    @Override
    protected void enqueueResponse(String fileName) throws IOException {
        MockResponse mockResponse = new MockResponse();
        String fileContent = readFileContentFromAssets(context, fileName);
        mockResponse.setBody(fileContent);
        server.enqueue(mockResponse);
    }
}
