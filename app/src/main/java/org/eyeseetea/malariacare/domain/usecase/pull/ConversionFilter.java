package org.eyeseetea.malariacare.domain.usecase.pull;

public class ConversionFilter {
    boolean convertData;
    boolean convertMetaData;
    boolean convertOrgUnitFromDB;
    public void setConvertData(boolean convertData) {
        this.convertData = convertData;
    }
    public boolean dataConversion() {
        return convertData;
    }
    public void setConvertMetaData(boolean convertMetaData) {
        this.convertMetaData = convertMetaData;
    }
    public boolean metadataConversion() {
        return convertMetaData;
    }
    public void setOrgUnitFromDB(boolean convertOrgUnitFromDB) {
        this.convertOrgUnitFromDB = convertOrgUnitFromDB;
    }
    public boolean getOrgUnitFromDB() {
        return convertOrgUnitFromDB;
    }
}
