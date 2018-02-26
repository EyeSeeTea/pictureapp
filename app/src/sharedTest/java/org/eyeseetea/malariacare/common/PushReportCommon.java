package org.eyeseetea.malariacare.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.common.network.ApiMessage;

import java.io.IOException;

public class PushReportCommon {

    public static final String IMPORT_SUMMARY_SUCCESS_RESPONSE =
            "import_summary_success_response.json";

    public static final String IMPORT_SUMMARY_ERROR_RESPONSE = "import_summary_error_response.json";

    public static final String IMPORT_SUMMARY_CONFLICT_RESPONSE =
            "import_summary_conflict_response.json";

    public static final String IMPORT_SUMMARY_DATA_VALUES = "import_summary_data_values.json";

    public static final String EVENT_UID_KEY = "DSifqmkzKfJ";


    public static Object getApiMessageFromJson(String jsonFile) throws IOException {
        String json = new FileReader().getStringFromFile(
                jsonFile);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (mapper.readValue(json,
                    ApiMessage.class)).getResponse().getImportSummaries().get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
