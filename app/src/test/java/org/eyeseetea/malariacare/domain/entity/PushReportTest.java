package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.common.FileReader;
import org.eyeseetea.malariacare.data.sync.mappers.PushReportMapper;
import org.eyeseetea.malariacare.domain.entity.pushsummary.PushReport;
import org.hisp.dhis.client.sdk.core.common.network.ApiMessage;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;
import org.junit.Test;

import java.io.IOException;

public class PushReportTest {

    public final String IMPORT_SUMMARY_ERROR_RESPONSE = "import_summary_error_response.json";

    private String IMPORT_SUMMARY_CONFLICT_RESPONSE ="import_summary_conflict_response.json";

    public final String IMPORT_SUMMARY_DATA_VALUES = "import_summary_data_values.json";

    public final String EVENT_UID_KEY = "DSifqmkzKfJ";

    @Test
    public void test_when_import_summary_has_conflict_push_report_error_is_false() throws
            IOException {
        ImportSummary importSummary = (ImportSummary) FileReader.getApiMessageFromJson(IMPORT_SUMMARY_CONFLICT_RESPONSE);
        PushReport pushReport = PushReportMapper.mapFromImportSummaryToPushReport(importSummary,
                EVENT_UID_KEY);
        assertThat(pushReport.hasPushErrors(), is(false));
    }

    @Test
    public void test_when_import_summary_has_errors_push_report_error_is_true() throws IOException {
        ImportSummary importSummary = (ImportSummary) FileReader.getApiMessageFromJson(IMPORT_SUMMARY_ERROR_RESPONSE);
        PushReport pushReport = PushReportMapper.mapFromImportSummaryToPushReport(importSummary,
                EVENT_UID_KEY);
        assertThat(pushReport.hasPushErrors(), is(true));
    }
    @Test
    public void test_when_import_summary_has_0_imported_data_values_push_report_error_is_true() throws IOException {
        ImportSummary importSummary = (ImportSummary) FileReader.getApiMessageFromJson(IMPORT_SUMMARY_DATA_VALUES);
        PushReport pushReport = PushReportMapper.mapFromImportSummaryToPushReport(importSummary,
                EVENT_UID_KEY);
        assertThat(pushReport.hasPushErrors(), is(true));
    }
}
