package org.eyeseetea.malariacare.domain.usecase.pull;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PullFilters {

    Date startDate;
    Date endDate;
    int maxEvents;
    boolean isDemo;
    boolean downloadData;
    boolean downloadMetaData;
    String dataByOrgUnit;
    boolean pullDataAfterMetadata;
    boolean isAutoConfig;
    String activeOrgUnitUid;
    Set<String> listOfOrgUnitsToPull;

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

    public boolean isAutoConfig() {
        return isAutoConfig;
    }

    public void setAutoConfig(boolean autoConfig) {
        isAutoConfig = autoConfig;
    }

    public Set<String> getListOfOrgUnitsToPull() {
        return listOfOrgUnitsToPull;
    }

    public void addOrgUnitUidToPull(String orgUnitUid) {
        if(listOfOrgUnitsToPull==null){
            listOfOrgUnitsToPull = new HashSet<>();
        }
        listOfOrgUnitsToPull.add(orgUnitUid);
    }
}
