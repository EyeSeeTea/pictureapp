package org.eyeseetea.malariacare;


import static junit.framework.Assert.assertTrue;

import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil.cleanUsedTables;
import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil.getOptionsDBCount;
import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil.getQuestionDBCount;
import static org.eyeseetea.malariacare.common.configurationimporter.ConfigurationImporterUtil.getQuestionOptionDBCount;

import com.squareup.okhttp.Credentials;

import org.eyeseetea.malariacare.data.di.Injector;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.MetadataConfigurationDBImporter;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.network.retrofit.BasicAuthInterceptor;
import org.junit.After;
import org.junit.Before;

public class RealMetadataConfigurationDBImporterShould {


    private MetadataConfigurationDBImporter importer;

    private  final Program program = new Program("T_TZ","low6qUS2wc9");

    @Before
    public void setUp() throws Exception {

        cleanUsedTables();

        String credentials = Credentials.basic("eref.webapp", "8frhKmMe");

        IMetadataConfigurationDataSource apiClient =
                Injector.provideMetadataConfigurationDataSource(
                        new BasicAuthInterceptor(credentials)
                );
        importer = new MetadataConfigurationDBImporter(
                apiClient, Injector.provideQuestionConverter()
        );
    }

    @After
    public void tearDown() {
        cleanUsedTables();
    }

    //This test hit a real serve only run it when you want to verify
    // if the importer still works with the actual configuration
    // files
    //@Test
    public void import_metadata_successfully() throws Exception {

        whenImportMetadata();

        thenAssertMetadataIsInsertedInTheDB();
    }

    private void whenImportMetadata() throws Exception {
        importer.importMetadata(program);
    }

    private void thenAssertMetadataIsInsertedInTheDB() {
        int questionsCount = getQuestionDBCount();
        int questionsOptionsCount = getQuestionOptionDBCount();
        int optionsCount = getOptionsDBCount();

        assertTrue(questionsCount > 0);
        assertTrue(questionsOptionsCount > 0);
        assertTrue(optionsCount > 0);
    }
}
