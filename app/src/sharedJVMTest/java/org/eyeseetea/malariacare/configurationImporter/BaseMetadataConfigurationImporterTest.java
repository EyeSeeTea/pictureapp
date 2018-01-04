package org.eyeseetea.malariacare.configurationImporter;


import static junit.framework.Assert.fail;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.common.BaseMockWebServerTest;
import org.eyeseetea.malariacare.data.sync.importer.metadata.configuration.MetadataConfigurationApiClient;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.junit.Before;

import java.util.Collections;
import java.util.List;

public class BaseMetadataConfigurationImporterTest extends BaseMockWebServerTest {

    protected MetadataConfigurationApiClient apiClient;

    protected static final String MZ_CONFIG_ANDROID_1_0_JSON =
            "mz_config_android_1_0.json";

    protected static final String NP_CONFIG_ANDROID_1_0_JSON =
            "np_config_android_1_0.json";

    protected static final String TZ_CONFIG_ANDROID_1_0_JSON =
            "tz_config_android_1_0.json";

    protected static final String ZW_CONFIG_ANDROID_1_0_JSON =
            "zw_config_android_1_0.json";

    protected static final String MZ_CONFIG_ANDROID_2_0_JSON =
            "mz_config_android_2_0.json";

    protected static final String TZ_CONFIG_ANDROID_2_0_JSON =
            "tz_config_android_2_0.json";
    protected static final String COUNTRIES_VERSION = "countries_version.json";

    @Before
    public void setUp() throws Exception {
        super.setUp();
        apiClient = initializeApiClient();
    }

    @NonNull
    private MetadataConfigurationApiClient initializeApiClient() throws Exception {
        return new MetadataConfigurationApiClient(server.url("/").toString());
    }

    @NonNull
    protected List<Question> getQuestionsClient(String countryJSONFile, String countryCode) {
        List<Question> questions = Collections.emptyList();

        try {
            enqueueResponse(countryJSONFile);

            questions = apiClient.getQuestionsFor(countryCode);


        } catch (Exception e) {
            fail(e.getMessage());
        }

        return questions;
    }
}
