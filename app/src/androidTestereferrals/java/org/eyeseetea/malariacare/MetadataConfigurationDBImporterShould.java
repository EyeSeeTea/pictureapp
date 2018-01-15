package org.eyeseetea.malariacare;


import static junit.framework.Assert.assertEquals;

import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil
        .cleanUsedTables;
import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil
        .getOptionsDBCount;
import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil
        .getQuestionDBCount;

import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil
        .getQuestionOptionDBCount;
import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil
        .loadMetadataFromCSV;
import static org.eyeseetea.malariacare.configurationImporter
        .ConstantsMetadataConfigurationImporterTest.COUNTRIES_VERSION;
import static org.eyeseetea.malariacare.configurationImporter
        .ConstantsMetadataConfigurationImporterTest.MZ_CONFIG_ANDROID_2_0_JSON;
import static org.eyeseetea.malariacare.configurationImporter
        .ConstantsMetadataConfigurationImporterTest.TZ_CONFIG_ANDROID_2_0_JSON;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.eyeseetea.malariacare.data.authentication.CredentialsReader;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.di.Injector;
import org.eyeseetea.malariacare.data.server.Dhis2MockServer;
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


public class MetadataConfigurationDBImporterShould {

    private Dhis2MockServer dhis2MockServer;

    @Before
    public void setUp() throws Exception {
        cleanUsedTables();
        CredentialsReader credentialsReader = CredentialsReader.getInstance();
        Session.setCredentials(
                new Credentials("/", credentialsReader.getUser(),
                        credentialsReader.getPassword()));

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        Context context = InstrumentationRegistry.getTargetContext();
        loadMetadataFromCSV(context);
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
}
