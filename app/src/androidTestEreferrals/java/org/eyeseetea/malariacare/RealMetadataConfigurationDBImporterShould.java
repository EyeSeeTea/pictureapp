package org.eyeseetea.malariacare;


import static junit.framework.Assert.assertTrue;

import android.support.test.InstrumentationRegistry;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.sync.factory.ConverterFactory;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationDBImporter;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration
        .MetadataConfigurationDataSourceFactory;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.junit.Before;

public class RealMetadataConfigurationDBImporterShould {


    private MetadataConfigurationDBImporter importer;

    private  final Program program = new Program("T_TZ","low6qUS2wc9");

    @Before
    public void setUp() throws Exception {

        MetadataConfigurationDataSourceFactory metadataConfigurationDataSourceFactory =
                new MetadataConfigurationDataSourceFactory(
                        InstrumentationRegistry.getTargetContext());
        IMetadataConfigurationDataSource apiClient =
                metadataConfigurationDataSourceFactory.getMetadataConfigurationDataSource();
        importer = new MetadataConfigurationDBImporter(
                apiClient, ConverterFactory.getQuestionConverter()
        );
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
        int questionsCount = QuestionDB.getQuestionDBCount();
        int questionsOptionsCount = QuestionOptionDB.getQuestionOptionDBCount();
        int optionsCount = OptionDB.getOptionsDBCount();

        assertTrue(questionsCount > 0);
        assertTrue(questionsOptionsCount > 0);
        assertTrue(optionsCount > 0);
    }
}
