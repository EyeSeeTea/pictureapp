package org.eyeseetea.malariacare;


import static junit.framework.Assert.assertEquals;

import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil
        .getPhoneFormatDBCount;
import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil
        .getProgramsDBCount;
import static org.eyeseetea.malariacare.configurationImporter
        .ConstantsMetadataConfigurationImporterTest.COUNTRIES_VERSION;
import static org.eyeseetea.malariacare.configurationImporter
        .ConstantsMetadataConfigurationImporterTest.TZ_CONFIG_ANDROID_2_0_JSON;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.authentication.CredentialsReader;
import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.di.Injector;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationApiClient;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationDBImporter;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;


public class MetadataConfigurationDBImporterShould {


    private CustomMockServer dhis2MockServer;

    private final Program program = new Program("T_TZ", "low6qUS2wc9");


    @Before
    public void setUp() throws Exception {
        cleanUsedTables();
        CredentialsReader credentialsReader = CredentialsReader.getInstance();
        Session.setCredentials(
                new Credentials("/", credentialsReader.getUser(),
                        credentialsReader.getPassword()));

        dhis2MockServer = new CustomMockServer(new AssetsFileReader());

    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
         cleanUsedTables();
    }

    @Test
    public void insert_questions_to_db_after_download_the_configurations() throws Exception {

        whenImportMetadata();

        thenAssertMetadataIsInsertedInTheDB();
    }

    private void thenAssertMetadataIsInsertedInTheDB() {
        shouldBeInDB(17, 7, 37, 1, 1);
    }

    private void whenImportMetadata() throws Exception {
        dhis2MockServer.enqueueMockResponse(COUNTRIES_VERSION);
        dhis2MockServer.enqueueMockResponse(TZ_CONFIG_ANDROID_2_0_JSON);

        shouldNotBeAnyQuestionInTheDB();

        MetadataConfigurationApiClient apiClient = new MetadataConfigurationApiClient(
                dhis2MockServer.getBaseEndpoint(),
                new BasicAuthInterceptor(""));

        MetadataConfigurationDBImporter importer = new MetadataConfigurationDBImporter(
                apiClient, Injector.provideQuestionConverter()
        );

        importer.importMetadata(program);
    }


    private void shouldNotBeAnyQuestionInTheDB() {
        shouldBeInDB(0, 0, 0, 0, 0);

    }

    private void shouldBeInDB(int expectedQuestionsCount, int expectedQuestionsOptionsCount,
            int expectedOptionsCount, int expectedProgramsCount, int expectedPhoneFormatsCount) {

        int questionsCount = getQuestionDBCount();
        int questionsOptionsCount = getQuestionOptionDBCount();
        int optionsCount = getOptionsDBCount();
        int programsCount = getProgramsDBCount();
        int formatsCount = getPhoneFormatDBCount();

        assertEquals(questionsCount, expectedQuestionsCount);
        assertEquals(questionsOptionsCount, expectedQuestionsOptionsCount);
        assertEquals(optionsCount, expectedOptionsCount);
        assertEquals(programsCount, expectedProgramsCount);
        assertEquals(formatsCount, expectedPhoneFormatsCount);
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

    private void cleanUsedTables() {
        HeaderDB.deleteAll();
        TabDB.deleteAll();
        ProgramDB.deleteAll();
        CountryVersionDB.deleteAll();


        QuestionDB.deleteAll();
        OptionDB.deleteAll();
        QuestionOptionDB.deleteAll();
    }
}
