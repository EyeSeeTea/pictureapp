package org.eyeseetea.malariacare;


import static junit.framework.Assert.assertEquals;

import static org.eyeseetea.malariacare.configurationimporter.ConfigurationImporterUtil.cleanUsedTables;
import static org.eyeseetea.malariacare.configurationimporter.ConfigurationImporterUtil.getOptionsDBCount;
import static org.eyeseetea.malariacare.configurationimporter.ConfigurationImporterUtil.getPhoneFormatDBCount;
import static org.eyeseetea.malariacare.configurationimporter.ConfigurationImporterUtil.getProgramsDBCount;
import static org.eyeseetea.malariacare.configurationimporter.ConfigurationImporterUtil.getQuestionDBCount;
import static org.eyeseetea.malariacare.configurationimporter.ConfigurationImporterUtil.getQuestionOptionDBCount;
import static org.eyeseetea.malariacare.configurationimporter.ConstantsMetadataConfigurationImporterTest.COUNTRIES_VERSION;
import static org.eyeseetea.malariacare.configurationimporter.ConstantsMetadataConfigurationImporterTest.TZ_CONFIG_ANDROID_2_0_JSON;

import org.eyeseetea.malariacare.data.authentication.CredentialsReader;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.server.Dhis2MockServer;
import org.eyeseetea.malariacare.data.sync.factory.ConverterFactory;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.MetadataConfigurationApiClient;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.MetadataConfigurationDBImporter;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


public class MetadataConfigurationDBImporterShould {

    private Dhis2MockServer dhis2MockServer;

    private final Program program = new Program("T_TZ", "low6qUS2wc9");


    @Before
    public void setUp() throws Exception {
        cleanUsedTables();
        CredentialsReader credentialsReader = CredentialsReader.getInstance();
        Session.setCredentials(
                new Credentials("/", credentialsReader.getUser(),
                        credentialsReader.getPassword()));

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
         cleanUsedTables();
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
}
