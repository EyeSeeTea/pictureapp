package org.eyeseetea.malariacare.domain.usecase.pull;

import java.util.Date;

public class PullFilters {

    Date startDate;
    Date endDate;
    int maxEvents;
    boolean isDemo;
    boolean downloadData;
    boolean downloadMetaData;
    String dataByOrgUnit;
    boolean pullDataAfterMetadata;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getMaxEvents() {
        return maxEvents;
    }

    public void setMaxEvents(int maxEvents) {
        this.maxEvents = maxEvents;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public void setDemo(boolean demo) {
        isDemo = demo;
    }

    public boolean downloadData() {
        return downloadData;
    }

    public void setDownloadDataRequired(boolean downloadData) {
        this.downloadData = downloadData;
    }

    public void setPullMetaData(boolean downloadMetaData) {
        this.downloadMetaData = downloadMetaData;
    }
    public boolean pullMetaData() {
        return downloadMetaData;
    }

    public String getDataByOrgUnit() {
        return dataByOrgUnit;
    }

    public void setDataByOrgUnit(String dataByOrgUnit) {
        this.dataByOrgUnit
                = dataByOrgUnit;
    }

    public void setPullDataAfterMetadata(boolean pullDataAfterMetadata) {
        this.pullDataAfterMetadata = pullDataAfterMetadata;
    }
    public boolean pullDataAfterMetadata() {
        return pullDataAfterMetadata;
    }
}
