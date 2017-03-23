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

    public void setDownloadData(boolean downloadMetaData) {
        this.downloadMetaData = downloadMetaData;
    }

    public boolean downloadMetaData() {
        return downloadMetaData;
    }

    public void setDownloadMetaData(boolean downloadMetaData) {
        this.downloadMetaData = downloadMetaData;
    }

    public String getDataByOrgUnit() {
        return dataByOrgUnit;
    }

    public void setDataByOrgUnit(String dataByOrgUnit) {
        this.dataByOrgUnit
                = dataByOrgUnit;
    }
}
