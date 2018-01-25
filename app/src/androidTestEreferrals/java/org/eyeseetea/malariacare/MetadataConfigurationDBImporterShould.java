package org.eyeseetea.malariacare;


import static junit.framework.Assert.assertEquals;

import static org.eyeseetea.malariacare.configurationimporter
        .ConstantsMetadataConfigurationImporterTest.COUNTRIES_VERSION;
import static org.eyeseetea.malariacare.configurationimporter
        .ConstantsMetadataConfigurationImporterTest.TZ_CONFIG_ANDROID_2_0_JSON;


import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.authentication.CredentialsReader;
import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.PhoneFormatDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.sync.factory.ConverterFactory;
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
        CredentialsReader credentialsReader = CredentialsReader.getInstance();
        Session.setCredentials(
                new Credentials("/", credentialsReader.getUser(),
                        credentialsReader.getPassword()));

        dhis2MockServer = new CustomMockServer(new AssetsFileReader());

    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    public void insert_questions_to_db_after_download_the_configurations() throws Exception {

        /* TODO: This is actually not providing an empty DB but checking that DB is empty, we
        would need to provide an empty DB or avoid checking that, or we will have problems if we
        change this test position */
        givenAnEmptyDB();

        whenCountryConfigFilesAreReceived();
        whenConfigFilesAreParsed();

        thenAssertMetadataIsInsertedInTheDB();
    }

    private void givenAnEmptyDB(){
        shouldNotBeAnyQuestionInTheDB();
    }

    private void whenCountryConfigFilesAreReceived() throws Exception {
        dhis2MockServer.enqueueMockResponse(COUNTRIES_VERSION);
        dhis2MockServer.enqueueMockResponse(TZ_CONFIG_ANDROID_2_0_JSON);
    }

    private void thenAssertMetadataIsInsertedInTheDB() {
        shouldBeInDB(17, 7, 37, 1, 1);
    }

    private void whenConfigFilesAreParsed() throws Exception {

        MetadataConfigurationApiClient apiClient = new MetadataConfigurationApiClient(
                dhis2MockServer.getBaseEndpoint(),
                new BasicAuthInterceptor(""));

        MetadataConfigurationDBImporter importer = new MetadataConfigurationDBImporter(
                apiClient, ConverterFactory.getQuestionConverter()
        );

        importer.importMetadata(program);
    }


    private void shouldNotBeAnyQuestionInTheDB() {
        shouldBeInDB(0, 0, 0, 0, 0);

    }

    private void shouldBeInDB(int expectedQuestionsCount, int expectedQuestionsOptionsCount,
            int expectedOptionsCount, int expectedProgramsCount, int expectedPhoneFormatsCount) {

        int questionsCount = QuestionDB.getQuestionDBCount();
        int questionsOptionsCount = QuestionOptionDB.getQuestionOptionDBCount();
        int optionsCount = OptionDB.getOptionsDBCount();
        int programsCount = ProgramDB.getProgramsDBCount();
        int formatsCount = PhoneFormatDB.getPhoneFormatDBCount();

        assertEquals(questionsCount, expectedQuestionsCount);
        assertEquals(questionsOptionsCount, expectedQuestionsOptionsCount);
        assertEquals(optionsCount, expectedOptionsCount);
        assertEquals(programsCount, expectedProgramsCount);
        assertEquals(formatsCount, expectedPhoneFormatsCount);
    }
}
