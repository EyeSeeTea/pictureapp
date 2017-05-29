package org.eyeseetea.malariacare.data.sync.mappers;


import static org.eyeseetea.malariacare.common.PushReportCommon.EVENT_UID_KEY;
import static org.eyeseetea.malariacare.common.PushReportCommon.IMPORT_SUMMARY_CONFLICT_RESPONSE;
import static org.eyeseetea.malariacare.common.PushReportCommon.IMPORT_SUMMARY_SUCCESS_RESPONSE;
import static org.eyeseetea.malariacare.common.PushReportCommon.getApiMessageFromJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.domain.entity.pushsummary.PushReport;
import org.hisp.dhis.client.sdk.models.common.importsummary.ImportSummary;
import org.junit.Test;

import java.io.IOException;

public class PushReportMapperTest {

    @Test
    public void test_conversion_of_success_import_summary() throws IOException {
        ImportSummary importSummary = givenAImportSummary(IMPORT_SUMMARY_SUCCESS_RESPONSE);

        PushReport pushReport = PushReportMapper.mapFromImportSummaryToPushReport(importSummary,
                EVENT_UID_KEY);
        //then
        assertThat(pushReport.getDescription().equals(importSummary.getDescription()), is(true));
        assertThat(pushReport.getEventUid().equals(importSummary.getReference()), is(true));
        assertThat(pushReport.getHref().equals(importSummary.getHref()), is(true));
        assertThat((pushReport.getStatus().equals(PushReport.Status.SUCCESS)), is(true));
        assertThat((importSummary.getStatus().equals(ImportSummary.Status.SUCCESS)), is(true));
        assertThat(pushReport.getPushedValues().getImported() == (importSummary.getImportCount()
                .getImported()), is(true));
        assertThat(pushReport.getPushedValues().getDeleted() == (importSummary.getImportCount()
                .getDeleted()), is(true));
        assertThat(pushReport.getPushedValues().getIgnored() == (importSummary.getImportCount()
                .getIgnored()), is(true));
        assertThat(pushReport.getPushedValues().getUpdated() == (importSummary.getImportCount()
                .getUpdated()), is(true));
        assertThat(pushReport.getPushConflicts().isEmpty(), is(true));
    }

    @Test
    public void test_conversion_of_success_import_summary_with_conflicts()
            throws IOException {
        //given
        ImportSummary importSummary = givenAImportSummary(IMPORT_SUMMARY_CONFLICT_RESPONSE);

        PushReport pushReport = PushReportMapper.mapFromImportSummaryToPushReport(importSummary,
                EVENT_UID_KEY);
        //then
        assertThat(pushReport.getEventUid().equals(importSummary.getReference()), is(true));
        assertThat(pushReport.getHref().equals(importSummary.getHref()), is(true));
        assertThat((pushReport.getStatus().equals(PushReport.Status.SUCCESS)), is(true));
        assertThat((importSummary.getStatus().equals(ImportSummary.Status.SUCCESS)), is(true));
        assertThat(pushReport.getPushedValues().getImported() == (importSummary.getImportCount()
                .getImported()), is(true));
        assertThat(pushReport.getPushedValues().getDeleted() == (importSummary.getImportCount()
                .getDeleted()), is(true));
        assertThat(pushReport.getPushedValues().getIgnored() == (importSummary.getImportCount()
                .getIgnored()), is(true));
        assertThat(pushReport.getPushedValues().getUpdated() == (importSummary.getImportCount()
                .getUpdated()), is(true));

        assertThat(pushReport.getPushConflicts().get(0).getUid().equals(
                importSummary.getConflicts().get(0).getObject()), is(true));
        assertThat(pushReport.getPushConflicts().get(0).getValue().equals(
                importSummary.getConflicts().get(0).getValue()), is(true));
    }

    private ImportSummary givenAImportSummary(String jsonFile) throws IOException {
        return (ImportSummary) getApiMessageFromJson(jsonFile);
    }
}
