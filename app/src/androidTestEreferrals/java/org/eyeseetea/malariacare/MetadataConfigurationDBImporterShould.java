package org.eyeseetea.malariacare;


import static junit.framework.Assert.assertEquals;

import static org.eyeseetea.malariacare.configurationImporter
        .ConstantsMetadataConfigurationImporterTest.COUNTRIES_VERSION;
import static org.eyeseetea.malariacare.configurationImporter
        .ConstantsMetadataConfigurationImporterTest.MZ_CONFIG_ANDROID_2_0_JSON;
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
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.database.utils.populatedb.UpdateDB;
import org.eyeseetea.malariacare.data.di.Injector;
import org.eyeseetea.malariacare.data.server.CustomMockServer;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationApiClient;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationDBImporter;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;


public class MetadataConfigurationDBImporterShould {


    private CustomMockServer dhis2MockServer;


    @Before
    public void setUp() throws Exception {
        CredentialsReader credentialsReader = CredentialsReader.getInstance();
        Session.setCredentials(
                new Credentials("/", credentialsReader.getUser(),
                        credentialsReader.getPassword()));

        dhis2MockServer = new CustomMockServer(new AssetsFileReader());

        UpdateDB.updatePrograms(PreferencesState.getContextForTesting());
        UpdateDB.updateTabs(PreferencesState.getContextForTesting());
        UpdateDB.updateHeaders(PreferencesState.getContextForTesting());
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
        shouldBeInDB(32, 23, 78);
    }

    private void whenImportMetadata() throws Exception {
        dhis2MockServer.enqueueMockResponse(COUNTRIES_VERSION);
        dhis2MockServer.enqueueMockResponse(MZ_CONFIG_ANDROID_2_0_JSON);
        dhis2MockServer.enqueueMockResponse(TZ_CONFIG_ANDROID_2_0_JSON);

        shouldNotBeAnyQuestionInTheDB();

        MetadataConfigurationApiClient apiClient = new MetadataConfigurationApiClient(
                dhis2MockServer.getBaseEndpoint(),
                new BasicAuthInterceptor(""));

        MetadataConfigurationDBImporter importer = new MetadataConfigurationDBImporter(
                apiClient, Injector.provideQuestionConverter()
        );

        importer.importMetadata();
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
